package com.icfolson.sling.translate.runtime.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class MethodUtil {

    public static final int GETTER_START = 3;
    public static final String GETTER = "^get([A-Z]+).*";
    public static final Pattern GETTER_PATTERN = Pattern.compile(GETTER);

    public static String getGetterPropertyName(String methodName) {
        Matcher matcher = GETTER_PATTERN.matcher(methodName);
        if (!matcher.matches()) {
            throw new IllegalStateException("Method " + methodName + " is not a getter");
        }
        int end = matcher.end(1);
        if (end > GETTER_START + 1 && end <= methodName.length()) {
            end--;
        }
        return methodName.substring(GETTER_START, end).toLowerCase() + methodName.substring(end);
    }

    private MethodUtil() { }
}
