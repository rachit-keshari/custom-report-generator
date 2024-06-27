package com.raredev.managers;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.raredev.constants.ConfigJsonKeys;
import com.raredev.constants.Constants;
import com.raredev.plugins.TransformationPluginsBase;
import com.raredev.plugins.TransformationPluginsExtended;
import com.raredev.plugins.Transformations;
import lombok.Data;
import lombok.Getter;
import lombok.SneakyThrows;
import lombok.ToString;

import java.util.*;
import java.util.regex.Pattern;

@Data
public class TransformationConfigManager {

    @Getter
    private static final Map<String, List<Expression>> transformations = new LinkedHashMap<>();

    public TransformationConfigManager() {
    }

    public static void build(JsonObject jsonObject){
        loadTransformationFromInputColumns(jsonObject);
    }

    private static void loadTransformationFromInputColumns(JsonObject o) {
        if (o.has(ConfigJsonKeys.OUTPUT_FILE_CONFIG)) {
            for (JsonElement e : o.getAsJsonArray(ConfigJsonKeys.OUTPUT_FILE_CONFIG)) {
                if (e.getAsJsonObject().has(ConfigJsonKeys.NAME) && e.getAsJsonObject().has(ConfigJsonKeys.TRANSFORM)) {
                    String columnName = e.getAsJsonObject().get(ConfigJsonKeys.NAME).getAsString();
                    String transform = e.getAsJsonObject().get(ConfigJsonKeys.TRANSFORM).getAsString();
                    transformations.put(columnName, getExpressionList(transform));
                }
            }
        }
    }

    public static List<Expression> getExpressionList(String transformation) {
        List<Expression> expressionsList = new ArrayList<>();
        for (String expression : transformation.split(Pattern.quote(Constants.PIPE_SPLITTER))) {
            expressionsList.add(new Expression(expression));
        }
        return expressionsList;
    }

    @SneakyThrows
    public static String resolve(Map<String, String> data, String value, Expression expression, String key) {
        try {
            if (expression.getParams() != null) {
                value = Transformations.getFieldTransformationPlugins(expression.getPluginName()).resolve(value, data,
                        expression.getParams());
            } else {
                value = Transformations.getFieldTransformationPlugins(expression.getPluginName()).resolve(value, data);
            }
        } catch (Exception e) {
            throw new Exception(String.format("column: %s and transformation: %s", key, expression.getPluginName()));
        }
        return value;
    }

    public static void applyTransformation(String key, Map<String, String> data) {
        String value = data.get(key);
        if (transformations.get(key) != null) {
            for (Expression expression : transformations.get(key)) {
                value = resolve(data, value, expression, key);
            }
        }
        data.put(key, value);
    }

    public static void applyTransformation(Map<String, String> data) {
        for (String key : transformations.keySet()) {
            applyTransformation(key, data);
        }
    }

    public static String applyTransformation(String value, List<Expression> expressions, Map<String, String> data) {
        for (Expression expression : expressions) {
            value = resolve(data, value, expression, null);
        }
        return value;
    }

    @Getter
    @ToString
    public static class Expression {
        private final String pluginName;
        private String[] params;

        public Expression(String expression) {
            if (expression.contains(Constants.COLON)) {
                this.pluginName = expression.substring(0, expression.indexOf(Constants.COLON));
                String substring = expression.substring(expression.indexOf(Constants.COLON) + 1);
                if (!(this.pluginName.equals(Constants.IF_ELSE) || (this.pluginName.equals(Constants.FORMAT_DATE)))) {
                    this.params = substring.split(Pattern.quote(Constants.COLON));
                } else {
                    this.params = new String[] { substring };
                }

            } else {
                this.pluginName = expression;
            }
            if (!Transformations.lookup.containsKey(pluginName)) {
                CloudWatchLogManager.getInstance().log("transformation '" + pluginName + "' is invalid in config file");
                throw new RuntimeException(
                        "transformation '" + pluginName + "' is invalid in config file");

            }
        }
    }

    public static void initializeTransformations(){
        TransformationPluginsBase.values();
        TransformationPluginsExtended.values();
    }

    @Override
    public String toString() {
        return "TransformationConfigManager: [ "+transformations.toString()+" ]";
    }

    public static void clear(){
        transformations.clear();
    }
}

