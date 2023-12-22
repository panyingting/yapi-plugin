package com.qbb.builder.encoding;

import org.apache.commons.lang3.StringUtils;

/**
 * @describe:
 * @author: pyt email:panyingting220415@credithc.com
 * @create_time: 2023/12/21 10:52
 */
public class AppTwoFieldConvertStrategy {

    private static final char UPPER_CASE_LOW = 'A';
    private static final char UPPER_CASE_UP = 'Z';
    private static final char LOWER_CASE_LOW = 'a';
    private static final char LOWER_CASE_UP = 'z';

    private static final int CASE_NUM = 26;

    public String encoding(String fieldName) {
        if (!StringUtils.isEmpty(fieldName)) {
            StringBuilder builder = new StringBuilder(fieldName.length());
            for (int i = 0; i < fieldName.length(); i++) {
                char ch = fieldName.charAt(i);
                builder.append(encodingChar(i, ch));
            }
            return builder.toString();
        }
        return fieldName;
    }

    private char encodingChar(int idx, char ch) {
        char caseLow = 0;
        if (isUpperCase(ch)) {
            caseLow = UPPER_CASE_LOW;
        } else if (isLowerCase(ch)) {
            caseLow = LOWER_CASE_LOW;
        }

        if (caseLow > 0) {
            return doEncodingChar(idx, ch, caseLow);
        }
        return ch;
    }

    private char doEncodingChar(int idx, char ch, char caseLow) {

        int water = ch - caseLow;
        water = (water + idx + 1) % CASE_NUM;
        return (char)(water + caseLow);
    }

    private boolean isUpperCase(char ch) {
        return ch >= UPPER_CASE_LOW && ch <= UPPER_CASE_UP;
    }

    private boolean isLowerCase(char ch) {
        return ch >= LOWER_CASE_LOW && ch <= LOWER_CASE_UP;
    }
}
