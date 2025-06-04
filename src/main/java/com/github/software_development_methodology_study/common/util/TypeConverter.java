package com.github.software_development_methodology_study.common.util;

import java.nio.charset.StandardCharsets;
import java.util.Objects;

public final class TypeConverter {
    private TypeConverter(){}

    public static Byte[] StringToByteArray(String str) {
        return toObject(str.getBytes(StandardCharsets.UTF_8));
    }

    public static String ByteArrayToString(Byte[] bytes) throws IllegalArgumentException {
        if (Objects.isNull(bytes)) throw new IllegalArgumentException("Input Byte array cannot be null");

        return new String(toPrimitive(bytes), StandardCharsets.UTF_8);
    }

    public static Byte[] toObject(byte[] bytes) {
        Byte[] result = new Byte[bytes.length];
        for (int i = 0; i < bytes.length; i++) {
            result[i] = bytes[i];
        }
        return result;
    }

    public static byte[] toPrimitive(Byte[] bytes) {
        byte[] result = new byte[bytes.length];
        for (int i = 0; i < bytes.length; i++) {
            result[i] = bytes[i]; // null이면 NPE 발생 가능
        }
        return result;
    }
}
