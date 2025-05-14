import enums.FrameEnum;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

public class BitMaskingTest {
    @Test
    void testByteToLong() {
        Byte[] header = Creator.createEthernetHeader();
        long result = byteToLong(header, FrameEnum.DESTINATION_MAC_ADDRESS.startOffset, FrameEnum.DESTINATION_MAC_ADDRESS.length);
        System.out.println("result : " + Long.toHexString(result).toUpperCase());
    }

    @Test
    void testLongToByte() {
        Byte[] header = Creator.createEthernetHeader();
        long bit = byteToLong(header, FrameEnum.DESTINATION_MAC_ADDRESS.startOffset, FrameEnum.DESTINATION_MAC_ADDRESS.length);
        Byte[] result = longToByte(bit, FrameEnum.DESTINATION_MAC_ADDRESS.length);

        for(Byte b : result)
            System.out.println(Byte.toUnsignedLong(b));
    }

    public long byteToLong(Byte[] bytes, int start, int length) {
        long result = 0;
        for (int i = 0; i < length; i++) {
            int bitIndex = start + i;
            int byteIndex = bitIndex / 8;
            int bitOffset = 7 - (bitIndex % 8);
            int bit = (bytes[byteIndex] >> bitOffset) & 1;
            result = (result << 1) | bit;
        }
        return result;
    }

    public Byte[] longToByte(long bits, int length) {
        int byteLength = (length + 7) / 8;
        Byte[] result = new Byte[byteLength];

        Arrays.fill(result, (byte) 0);

        for (int i = 0; i < length; i++) {
            int bitIndex = length - 1 - i;
            long bit = (bits >> bitIndex) & 1L;

            int byteIndex = i / 8;
            int bitOffset = 7 - (i % 8);

            result[byteIndex] = (byte)(result[byteIndex] | (bit << bitOffset));
        }

        return result;
    }
}
