package com.wsp.workshophy.utilities;

import com.wsp.workshophy.constant.Constants;
import com.wsp.workshophy.exception.ConstructorException;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

public final class StringUtil {
    /* always at the bottom */
    private StringUtil() {
        throw new ConstructorException();
    }


    public static boolean isNumeric(String strNum) {
        return strNum.matches("-?\\d+(\\.\\d+)?");
    }

    public static String trim(String val, String defaultValue) {
        return StringUtils.isNotBlank(val) ? val.trim() : defaultValue;
    }

    private static String normalizeString(String val) {
        return val.replaceAll(Constants.SPECIAL_CHARACTERS, "\\\\$0");
    }

    public static String trim(String val) {
        return trim(val, StringUtils.EMPTY);
    }

    public static String normalize(String val) {
        return normalizeString(trim(val, StringUtils.EMPTY));
    }

    public static String camelToSnake(String str) {
        String result = "";
        char c = str.charAt(0);
        result = result + Character.toLowerCase(c);

        for (int i = 1; i < str.length(); i++) {
            char ch = str.charAt(i);
            if (Character.isUpperCase(ch)) {
                result = result + '_';
                result = result + Character.toLowerCase(ch);
            } else {
                result = result + ch;
            }
        }
        return result;
    }

    public static String generateRandomString(int length) {
        return RandomStringUtils.random(length, Constants.CHARACTERS);
    }

    public static String generateRandomCodeOrSerial(String prefix, int postfixLength) {
        Long timestamp = LocalDateTime.now().toInstant(ZoneOffset.UTC).toEpochMilli();
        return prefix + RandomStringUtils.random(postfixLength, timestamp.toString());
    }
}
