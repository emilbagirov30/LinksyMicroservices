package com.emil.linksy_user.util;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class CodeGenerator {
    private static final Map<String, String> codes = new HashMap<>();
    public static String generate(String email) {
        String code = String.valueOf(10000 + new Random().nextInt(90000));
        codes.put(email, code);
        return code;
    }


    public static boolean isValidCode(String email, String code) {
        return code.equals(codes.get(email));
    }


    public static void removeCode(String email) {
        codes.remove(email);
    }
}
