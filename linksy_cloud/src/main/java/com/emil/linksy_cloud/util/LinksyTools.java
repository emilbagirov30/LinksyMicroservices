package com.emil.linksy_cloud.util;

public class LinksyTools {

    public static String clearQuotes (String source){
        return source.substring(1,source.length()-1).replace("\\n", "\n");
    }
}
