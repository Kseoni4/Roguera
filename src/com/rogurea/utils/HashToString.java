package com.rogurea.utils;

public class HashToString {
    public static String convert(byte[] bytes){
        String hashLine = "";
        for(byte b: bytes){
            hashLine = hashLine.concat(String.format("%02x",b));
        }
        return hashLine;
    }
}
