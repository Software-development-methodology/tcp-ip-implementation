package com.github.software_development_methodology_study.common.util;

import java.nio.charset.StandardCharsets;

public final class DataType {
    private DataType(){}

    public static Byte[] StringToByteArray(String str) {
        byte[] byteArray = str.getBytes(StandardCharsets.UTF_8); // or Charset.forName("UTF-8")
        Byte[] byteObjectArray = new Byte[byteArray.length];

        for (int i = 0; i < byteArray.length; i++) {
            byteObjectArray[i] = byteArray[i]; // auto-boxing
        }
        return byteObjectArray;
    }

    public static String ByteArrayToString() throws Exception{
        // @TODO 구현: can019
        throw new Exception("Method not implemented");
    }
}
