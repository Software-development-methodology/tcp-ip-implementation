package org.example.util;

public class Util {
    public static byte[] mergeByteArrays(byte[]... arrays) {
        // 전체 길이 계산
        int totalLength = 0;
        for (byte[] array : arrays) {
            totalLength += array.length;
        }

        // 새 배열 생성
        byte[] result = new byte[totalLength];

        // 배열 복사
        int currentPos = 0;
        for (byte[] array : arrays) {
            System.arraycopy(array, 0, result, currentPos, array.length);
            currentPos += array.length;
        }

        return result;
    }
}
