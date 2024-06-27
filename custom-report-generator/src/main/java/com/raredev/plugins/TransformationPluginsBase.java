package com.raredev.plugins;

import com.google.common.base.Strings;
import org.apache.commons.lang3.RegExUtils;
import org.apache.commons.lang3.StringUtils;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.util.Locale;
import java.util.Map;

@SuppressWarnings("java:S115")
public enum TransformationPluginsBase implements Transformations {
    RemoveSpaces {
        @Override
        public String resolve(String key, Map<String, String> data, String... params) {
            return key.trim();
        }
    },
    AddLeadingZeros {
        @Override
        public String resolve(String key, Map<String, String> data, String... params) {
            return Strings.padStart(key, Integer.parseInt(params[0]), '0');
        }
    },
    AddTrailingZeros {
        @Override
        public String resolve(String key, Map<String, String> data, String... params) {
            return Strings.padEnd(key, Integer.parseInt(params[0]), '0');
        }
    },
    SetZeroIfNotNumeric {
        @Override
        public String resolve(String key, Map<String, String> data, String... params) {
            if (!StringUtils.isNumeric(key)) {
                return "0";
            }
            return key;
        }
    },
    RemoveLeadingZeros {
        @Override
        public String resolve(String key, Map<String, String> data, String... params) {
            if (StringUtils.isEmpty(key)) {
                return key;
            }
            return StringUtils.stripStart(key.trim(), "0");
        }
    },
    AbsoluteAndParseDouble {
        @Override
        public String resolve(String key, Map<String, String> data, String... params) {
            if (StringUtils.isEmpty(key)) {
                return key;
            }
            return String.valueOf(Math.abs(Double.parseDouble(key)));
        }
    },
    Evaluate {
        @Override
        public String resolve(String key, Map<String, String> data, String... param) {
            ScriptEngineManager mgr = new ScriptEngineManager();
            ScriptEngine engine = mgr.getEngineByName("JavaScript");
            String expression = param[0];
            expression = RegExUtils.replaceAll(expression, "\\{OR}", "||");
            if (key != null) {
                expression = expression.replace("?", key);
            }
            while (expression.contains("{")) {
                String variable = expression.substring(expression.indexOf("{"), expression.indexOf("}") + 1);
                expression = expression.replace(variable, getValue(variable, data));
            }
            return getEvaluateResult(engine, expression);
        }

    },
    DecimalPrecision {
        @Override
        public String resolve(String key, Map<String, String> data, String... params) {
            int precision = Integer.parseInt(params[0]);
            String format = "%." + precision + "f";
            return String.format(format, Double.parseDouble(key));
        }
    },
    TextAppender {
        @Override
        public String resolve(String key, Map<String, String> data, String... params) {
            String expression = params[0];
            if (expression.contains("?")) {
                expression = expression.replace("?", key);
            }
            while (expression.contains("{")) {
                String variable = expression.substring(expression.indexOf("{"), expression.indexOf("}") + 1);
                expression = expression.replace(variable, getValue(variable, data));
            }
            return expression;
        }
    },
    UseField {
        @Override
        public String resolve(String key, Map<String, String> data, String... params) {
            String variable = params[0];
            if (variable.startsWith("{")) {
                variable = variable.substring(variable.indexOf("{") + 1, variable.indexOf("}"));
            }
            return data.get(variable);
        }
    },
    ExponentialToNumber {
        @Override
        public String resolve(String key, Map<String, String> data, String... params) {
            return String.valueOf(Double.valueOf(key).longValue());
        }
    },
    WhenContainsReturn {
        @Override
        public String resolve(String key, Map<String, String> data, String... params) {
            if (key.toLowerCase(Locale.ENGLISH).contains(params[0].toLowerCase(Locale.ENGLISH))) {
                return params[1];
            }
            return key;
        }
    };

    private static String getEvaluateResult(ScriptEngine engine, String expression) {
        try {
            Object result = engine.eval(expression);
            if (result instanceof Double) {
                double d = (Double) result;
                return d % 1.0 != 0 ? String.format("%s", d) : String.format("%.0f", d);
            }
            return result.toString();
        } catch (ScriptException e) {
            throw new RuntimeException("Invalid expression : " + expression);
        }
    }

    static {
        for (TransformationPluginsBase d : TransformationPluginsBase.values()) {
            lookup.put(d.name(), d);
        }
    }

    private static String getValue(String variable, Map<String, String> data) {
        String key = variable.substring(1, variable.length() - 1);
        return data.get(key);
    }
}

