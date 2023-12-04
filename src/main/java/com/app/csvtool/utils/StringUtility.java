package com.app.csvtool.utils;

import java.util.regex.Pattern;

public class StringUtility {

    public static String removeSpecialCharacters(String input, String cases) {
        // Define a regular expression pattern to match non-alphanumeric characters
        String pattern = "[^a-zA-Z0-9]";

        // Use the Pattern.compile() method to create a compiled pattern object
        Pattern p = Pattern.compile(pattern);

        // Use the Matcher.replaceAll() method to replace all occurrences of the pattern with an empty string

        input = p.matcher(input).replaceAll("").trim();

        // Convert to uppercase if specified
        if (cases != null && cases.equals("UPPER")) {
            return input.toUpperCase();
        }

        return input.toLowerCase();
    }

}
