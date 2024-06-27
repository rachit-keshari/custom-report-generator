package com.raredev.plugins;

import com.raredev.managers.TransformationConfigManager;
import com.raredev.constants.Constants;
import com.raredev.managers.CloudWatchLogManager;
import org.apache.commons.lang3.RegExUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;

import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@SuppressWarnings("java:S115")
public enum TransformationPluginsExtended implements Transformations {

    Lower {
        @Override
        public String resolve(String key, Map<String, String> data, String... params) {
            return key.toLowerCase(Locale.ENGLISH);
        }
    },
    Upper {
        @Override
        public String resolve(String key, Map<String, String> data, String... params) {
            return key.toUpperCase(Locale.ENGLISH);
        }
    },
    RemoveCharacter {
        @Override
        public String resolve(String key, Map<String, String> data, String... params) {
            return key.replace(params[Constants.ZERO], Constants.EMPTY);
        }
    },
    GetIndexOf {
        @Override
        public String resolve(String key, Map<String, String> data, String... params) {
            String character = params[Constants.ZERO];
            return String.valueOf(key.indexOf(character));
        }
    },
    GetLength {
        @Override
        public String resolve(String key, Map<String, String> data, String... params) {
            return String.valueOf(key.length());
        }
    },
    Contains {
        @Override
        public String resolve(String key, Map<String, String> data, String... params) {
            // {candidate_string}:{default_value}:(list of strings that need to be contained in candidate string).(value
            // to be returned if contains)
            String candidateString = getValue(params[Constants.ZERO], data);
            for (int i = 2; i < params.length; ++i) {
                String matchPair = params[i];
                String[] matchPairSplit = matchPair.split(Constants.DOUBLE_BACKSLASH + Constants.DOT);
                String[] toBeMatched = matchPairSplit[Constants.ZERO].split(Constants.COMMA);
                String toBeSet = getValue(matchPairSplit[Constants.ONE], data);
                if (StringUtils.containsAnyIgnoreCase(candidateString, toBeMatched)) {
                    return toBeSet;
                }

            }
            return getValue(params[Constants.ONE], data);
        }
    },
    Matches {
        @Override
        public String resolve(String key, Map<String, String> data, String... params) {
            // {candidate_string}:{default_value}:(list of strings to be matched).(value to be returned if matched)
            String candidateString = getValue(params[Constants.ZERO], data);
            for (int i = 2; i < params.length; ++i) {
                String matchPair = params[i];
                String[] matchPairSplit = matchPair.split(Constants.DOUBLE_BACKSLASH + Constants.DOT);
                String[] toBeMatched = matchPairSplit[Constants.ZERO].split(Constants.COMMA);
                toBeMatched = Arrays.stream(toBeMatched).map(str -> getValue(str, data)).toArray(String[]::new);
                String toBeSet = getValue(matchPairSplit[Constants.ONE], data);
                if (StringUtils.equalsAnyIgnoreCase(candidateString, toBeMatched)) {
                    return toBeSet;
                }
            }
            return getValue(params[Constants.ONE], data);
        }
    },
    LastCharacters {
        @Override
        public String resolve(String key, Map<String, String> data, String... params) {
            int offset = Integer.parseInt(params[Constants.ZERO]);
            if (offset > key.length()) {
                return key;
            }
            return key.substring(key.length() - offset);
        }
    },
    IfBlankSet {
        @Override
        public String resolve(String key, Map<String, String> data, String... params) {
            if (StringUtils.isBlank(key)) {
                return params[Constants.ZERO];
            }
            return key;
        }
    },

    DecimalFormat {
        @Override
        public String resolve(String key, Map<String, String> data, String... params) {
            DecimalFormat df = new DecimalFormat(params[Constants.ZERO]);
            return Double.toString(Double.parseDouble(df.format(new Double(key))));
        }
    },
    // returns substring present before the character passed
    // eg: key="abc-123" params["-"] => returns "abc"
    GetCharactersBefore {
        @Override
        public String resolve(String key, Map<String, String> data, String... params) {
            String character = params[0];
            if (StringUtils.contains(key, character)) {
                int index = key.indexOf(character);
                return key.substring(0, index);
            }
            return key;
        }
    },
    // returns substring present after the character passed
    // eg: key="abc-123" params["-"] => returns "123"
    GetCharactersAfter {
        @Override
        public String resolve(String key, Map<String, String> data, String... params) {
            String character = params[0];
            if (StringUtils.contains(key, character)) {
                int index = key.indexOf(character);
                return key.substring(index + 1);
            }
            return key;
        }
    },
    GetEsnAfterZero {
        @Override
        public String resolve(String key, Map<String, String> data, String... params) {
            Matcher matcher = Constants.DIGIT_PATTERN.matcher(key);
            int firstNonZeroIndex = matcher.find() ? matcher.start() : -1;
            return firstNonZeroIndex >= 0 ? key.substring(firstNonZeroIndex) : key;
        }
    },
    // sets decimal point
    // eg: key="45932" => returns "459.32"
    SetDecimalPoint {
        @Override
        public String resolve(String key, Map<String, String> data, String... params) {
            return Double.toString(new Double(key) / 100);
        }
    },

    // returns substring
    // eg: key="abc123" params=["1,5"] => returns "bc12"
    GetSubstring {
        @Override
        public String resolve(String key, Map<String, String> data, String... params) {
            String[] indexes = params[Constants.ZERO].split(Constants.COMMA);
            int startIndex = Integer.parseInt(indexes[Constants.ZERO]);
            int lastIndex = Integer.parseInt(indexes[Constants.ONE]);
            return key.substring(startIndex, lastIndex);
        }
    },
    /*
     * Usage: Divide:placeholder0:placeholder1 Returns: result obtained after dividing placeholder0 by placeholder1
     */
    Divide {
        @Override
        public String resolve(String key, Map<String, String> data, String... params) {
            return String.valueOf(
                    Double.parseDouble(getValue(params[0], data)) / Double.parseDouble(getValue(params[1], data)));
        }
    },

    AllStringEquals {
        @Override
        public String resolve(String key, Map<String, String> data, String... params) {
            for (String input : params) {
                String[] splitInput = input.split(Constants.COMMA);
                if (!StringUtils.equals(getValue(splitInput[0], data), getValue(splitInput[1], data))) {
                    return String.valueOf(false);
                }
            }
            return String.valueOf(true);
        }
    },
    Add {
        @Override
        public String resolve(String key, Map<String, String> data, String... params) {
            Double result = (double) 0;
            for (String input : params) {
                if (StringUtils.equals("?", input)) {
                    input = key;
                }
                result += Double.parseDouble(getValue(input, data));
            }
            return String.valueOf(result);
        }
    },
    BankCommissionAmount {
        @Override
        public String resolve(String key, Map<String, String> data, String... params) {
            return Double.parseDouble(getValue(params[0], data)) < 2000.0 ? getValue(params[1], data)
                    : String.valueOf(Double.parseDouble(getValue(params[1], data)) / 1.18);
        }
    },
    BankGST {
        @Override
        public String resolve(String key, Map<String, String> data, String... params) {
            double mdrValue = Double.parseDouble(getValue(params[1], data));
            return Double.parseDouble(getValue(params[0], data)) < 2000.0 ? "0.0"
                    : String.valueOf(mdrValue - mdrValue / 1.18);
        }
    },
    AddFields {
        @Override
        public String resolve(String key, Map<String, String> data, String... params) {
            String[] allParams = params[0].split(",");
            Double result = (double) 0;
            for (String input : allParams) {
                if (StringUtils.equals("?", input)) {
                    input = key;
                }
                String value = getValue(input, data);
                if (StringUtils.isEmpty(value)) {
                    value = "0";
                }
                result += Double.parseDouble(value);
            }
            return String.valueOf(result);
        }
    },

    FormatDate {
        public String resolve(String key, Map<String, String> data, String... params) {

            String str = params[0];
            int lastIndexOfColon = str.lastIndexOf(":");
            String formatterPart = params[0];
            Integer nDay = null;
            if (lastIndexOfColon != -1) {
                formatterPart = params[0].substring(0, lastIndexOfColon);
                String nDayString = params[0].substring(lastIndexOfColon + 1);
                try {
                    nDay = Integer.parseInt(nDayString);
                } catch (NumberFormatException ignore) {
                    formatterPart = params[0];
                }
            }

            String[] d1 = formatterPart.split("->");
            String dateTimeFormat = d1[Constants.ZERO];
            String dateFormat = d1[Constants.ONE];
            boolean flag = dateTimeFormat.equals(Constants.CREATE_NEW_DATE);
            nDay = (nDay != null) ? nDay : 0;
            SimpleDateFormat formatter = new SimpleDateFormat(dateFormat);
            Date date;
            try {
                if (flag) {
                    date = new Date();
                } else {
                    Date tempDate = new SimpleDateFormat(dateTimeFormat).parse(key);
                    String d = formatter.format(tempDate);
                    date = formatter.parse(d);
                }
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            calendar.add(Calendar.DAY_OF_YEAR, nDay);
            return formatter.format(calendar.getTime());
        }
    },

    ConvertToRupee {
        @Override
        public String resolve(String key, Map<String, String> data, String... params) {
            try {
                BigDecimal amount = BigDecimal.valueOf(Double.parseDouble(key));
                return amount.divide(new BigDecimal(100), MathContext.DECIMAL64).setScale(2, RoundingMode.DOWN)
                        .toPlainString();
            } catch (NumberFormatException ex) {
                CloudWatchLogManager.getInstance()
                        .log("Error occurred while converting to Rupee for value : " + key + ". Returning Empty");
                return Constants.EMPTY;
            }

        }
    },
    RemoveFromStart {
        public String resolve(String key, Map<String, String> data, String... params) {
            String stringToRemove = params[0];
            return StringUtils.removeStart(key, stringToRemove);
        }
    },

    IfEmpty {
        public String resolve(String key, Map<String, String> data, String... params) {
            String[] allParams = params[0].split(",");
            for (String param : allParams) {
                String value = getValue(param, data);
                if (!StringUtils.isEmpty(value) && !value.equals("\"\"")) {
                    return Constants.FALSE;
                }
            }
            return Constants.TRUE;
        }
    },
    IfNotEmpty {
        public String resolve(String key, Map<String, String> data, String... params) {
            String[] allParams = params[0].split(",");
            for (String param : allParams) {
                String value = getValue(param, data);
                if (StringUtils.isEmpty(value) || value.equals("\"\"")) {
                    return Constants.FALSE;
                }
            }
            return Constants.TRUE;
        }
    },
    Constant {
        public String resolve(String key, Map<String, String> data, String... params) {
            return params[0];
        }
    },

    AddDoubleQuotes {
        @Override
        public String resolve(String key, Map<String, String> data, String... params) {
            if (key.matches("\".*\"") || key.contains(Constants.COMMA)) {
                return key;
            } else {
                return "\"" + key + "\"";
            }
        }
    },
    Map {
        @Override
        public String resolve(String key, java.util.Map<String, String> data, String... params) {
            Map<String, String> map = Arrays.stream(params[0].split(",")).map(pair -> pair.split("->"))
                    .collect(Collectors.toMap(pair -> pair[0], pair -> pair[1]));

            if (params.length == 1) {
                return map.getOrDefault(key, "");
            }
            return map.getOrDefault(key, params[1]);
        }
    },

    NumEquals {
        @Override
        public String resolve(String key, java.util.Map<String, String> data, String... params) {

            Object obj1 = getExpressionResult(params[0], data);
            Object obj2 = getExpressionResult(params[1], data);
            if (obj1 == null || obj2 == null)
                return "false";
            double num1 = obj1 instanceof Integer ? Double.valueOf((Integer) obj1) : (Double) (obj1);
            double num2 = obj2 instanceof Integer ? Double.valueOf((Integer) obj2) : (Double) (obj2);
            return num1 == num2 ? "true" : "false";
        }
    },

    // number1*number2 or multiply a previous calculated result with a given number
    MultiplyByValue {
        @Override
        public String resolve(String key, Map<String, String> data, String... params) {
            BigDecimal number1 = new BigDecimal(key);
            BigDecimal number2 = new BigDecimal(params[0]);
            BigDecimal result = number1.multiply(number2);
            return result.toString();
        }
    },

    LeftPad {
        @Override
        public String resolve(String key, Map<String, String> data, String... params) {
            String[] paramVals = params[0].split(",");
            // paramVals[0] = total size of output string after padding
            // paramVals[1] = pad string
            // Sample:
            // key = 1234
            // params = 6,0
            // output = 001234
            return StringUtils.leftPad(key, Integer.parseInt(paramVals[0]), paramVals[1]);
        }
    },
    DivideByValue {
        @Override
        public String resolve(String key, Map<String, String> data, String... params) {
            BigDecimal number1 = new BigDecimal(TransformationPluginsExtended.getValue(key, data));
            BigDecimal number2 = new BigDecimal(TransformationPluginsExtended.getValue(params[0], data));
            if (params.length == 1) {
                return number1.divide(number2, 2, RoundingMode.HALF_UP).toPlainString();
            }
            return number1.divide(number2, 2, RoundingMode.valueOf(params[1])).toPlainString();
        }
    },
    LimitLength {
        // This plugin is used to limit the string to a particular length
        @Override
        public String resolve(String key, Map<String, String> data, String... params) {
            int maxLength = Integer.parseInt(params[0]);
            // params[0] = total length of output string
            // Sample:
            // key = ThisIsTestString
            // params = 10
            // output = ThisIsTest
            return StringUtils.isBlank(key) ? "" : key.substring(0, Math.min(key.length(), maxLength));
        }
    },
    LimitLengthFromBack {
        // This plugin is used to limit the string to a particular length
        @Override
        public String resolve(String key, Map<String, String> data, String... params) {
            int maxLength = Integer.parseInt(params[0]);
            // params[0] = total length of output string
            // Sample:
            // key = ThisIsTestString
            // params = 10
            // output = ThisIsTest
            return StringUtils.isBlank(key) ? "" : key.substring(Math.min(key.length(),key.length()-maxLength),key.length());
        }
    },
    // GetFromSplitString:candidate_string:delimiter:index
    // eg: GetFromSplitString:{Json_Data}:|:2
    GetFromSplitString {
        @Override
        public String resolve(String key, Map<String, String> data, String... params) {
            String str = getValue(params[0], data);
            if (StringUtils.isBlank(str)) {
                return "";
            }
            String[] splitStr = str.split(params[1]);
            int index = Integer.parseInt(params[2]);
            return splitStr.length <= index ? "" : splitStr[index];
        }
    },
    // GetDate:format:offset
    // eg: GetDate:ddMMyyyy:1 will be tomorrow's date
    GetDate {
        @Override
        public String resolve(String key, Map<String, String> data, String... params) {
            String dateFormat = params[0];
            int offset = Integer.parseInt(params[1]);
            SimpleDateFormat dateFormatter = new SimpleDateFormat(dateFormat);
            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.DATE, offset);
            return dateFormatter.format(cal.getTime());
        }
    },

    UnixTimestamp {
        @Override
        public String resolve(String key, Map<String, String> data, String... params) {
            return String.valueOf(System.currentTimeMillis());
        }
    },

    AddPrefix {
        @Override
        public String resolve(String key, Map<String, String> data, String... params) {
            return params[0] + key;
        }
    };

    private static Object getExpressionResult(String exp, Map<String, String> data) {

        List<String> substrings = new ArrayList<>();
        Pattern pattern = Pattern.compile("\\{([^}]*)\\}");
        Matcher matcher = pattern.matcher(exp);
        while (matcher.find()) {
            substrings.add(matcher.group());
        }
        for (String val : substrings) {
            String replacingValue = getValue(val, data);
            if (StringUtils.isEmpty(replacingValue) || replacingValue.equals("\"\""))
                return null;
            exp = exp.replace(val, replacingValue);
        }
        exp = RegExUtils.replaceAll(exp, "add", "+");
        exp = RegExUtils.replaceAll(exp, "sub", "-");
        exp = RegExUtils.replaceAll(exp, "mul", "*");
        exp = RegExUtils.replaceAll(exp, "div", "/");
        try {
            return (new ScriptEngineManager().getEngineByName("JavaScript").eval(exp));
        } catch (ScriptException e) {
            CloudWatchLogManager.getInstance().log("Some exception occurred while while executing ScriptEngineManager");
            throw new RuntimeException("Some exception occurred while while executing ScriptEngineManager");
        }

    }

    public static List<TransformationConfigManager.Expression> getExpressionList(String transformation) {
        List<TransformationConfigManager.Expression> expressionsList = new ArrayList<>();
        if (transformation.isEmpty())
            return expressionsList;
        for (String expression : transformation.split("\\+")) {
            expressionsList.add(new TransformationConfigManager.Expression(expression));
        }
        return expressionsList;
    }

    private static Pair<Integer, Integer> getIndexes(String s) {
        if (!s.contains("("))
            return Pair.of(-1, -1);
        int closeBrackets = 0;
        int openBrackets = 0;
        int questionMarkIndex = -1;
        int dotIndex = -1;
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (c == '(') {
                openBrackets++;
            } else if (c == ')') {
                closeBrackets++;
            } else if (c == '?' && closeBrackets == openBrackets) {
                questionMarkIndex = i;
            } else if (c == '.' && closeBrackets == openBrackets) {
                dotIndex = i;
            }
        }
        return Pair.of(questionMarkIndex, dotIndex);
    }

    static {
        for (TransformationPluginsExtended d : TransformationPluginsExtended.values()) {
            lookup.put(d.name(), d);
        }
    }

    private static String getValue(String variable, Map<String, String> data) {
        if (variable.startsWith(Constants.OPEN_CURLY_BRACES)) {
            String key = variable.substring(1, variable.length() - 1);
            return data.get(key);
        }
        return variable;
    }
}
