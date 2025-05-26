package com.github.software_development_methodology_study.common.util;

import java.nio.charset.StandardCharsets;
import java.util.Objects;

public final class Convertor {
    private Convertor(){}

    public static Byte[] StringToByteArray(String str) {
        byte[] byteArray = str.getBytes(StandardCharsets.UTF_8); // or Charset.forName("UTF-8")
        Byte[] byteObjectArray = new Byte[byteArray.length];

        for (int i = 0; i < byteArray.length; i++) {
            byteObjectArray[i] = byteArray[i]; // auto-boxing
        }
        return byteObjectArray;
    }

    public static String ByteArrayToString(Byte[] bytes) throws IllegalArgumentException {
        if (Objects.isNull(bytes)) throw new IllegalArgumentException("Input Byte array cannot be null");

        byte[] primitiveBytes = new byte[bytes.length];
        for (int i = 0; i < bytes.length; i++) {
            if (bytes[i] == null) {
                throw new IllegalArgumentException("Byte array contains null at index " + i);
            }
            primitiveBytes[i] = bytes[i];
        }

        return new String(primitiveBytes, StandardCharsets.UTF_8);
    }
}
