package header;

import enums.FrameEnum;

import java.util.Arrays;
import java.util.BitSet;

public class FrameHeader implements Header {
    private final int HEADER_LENGTH = 14;
    private Byte[] Destination_MAC_Address; // 0 ~ 5
    private Byte[] Source_MAC_Address;      // 6 ~ 11
    private Byte[] Type;                    // 12 ~ 13
    private Byte[] header;

    public FrameHeader() {
        this.Destination_MAC_Address = new Byte[FrameEnum.DESTINATION_MAC_ADDRESS.length];
        this.Source_MAC_Address = new Byte[FrameEnum.SOURCE_MAC_ADDRESS.length];
        this.Type = new Byte[FrameEnum.TYPE.length];
    }

    public FrameHeader(Byte[] bytes) {
        this.header = bytes;
    }


    @Override
    public Byte[] separateAndReturnByte(Byte[] bytes) {
        Byte[] headerBytes = Arrays.copyOfRange(bytes, 0, HEADER_LENGTH);
        this.header = headerBytes;
        parseDestinationMACAddress(headerBytes);
        parseSource_MAC_Address(headerBytes);
        parseType(headerBytes);
        return Arrays.copyOfRange(bytes, HEADER_LENGTH, bytes.length);
    }

    @Override
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

    @Override
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

    @Override
    public Byte[] getHeaderByteArray() {
        if(header == null)
            throw new NullPointerException();
        return header;
    }

    @Override
    public int getHeaderLength() {
        return HEADER_LENGTH;
    }

    private void parseDestinationMACAddress(Byte[] bytes) {
        long bit = byteToLong(bytes, FrameEnum.DESTINATION_MAC_ADDRESS.startOffset, FrameEnum.DESTINATION_MAC_ADDRESS.length);
        this.Destination_MAC_Address = longToByte(bit, FrameEnum.DESTINATION_MAC_ADDRESS.length);
    }
    private void parseSource_MAC_Address(Byte[] bytes) {
        long bit = byteToLong(bytes, FrameEnum.SOURCE_MAC_ADDRESS.startOffset, FrameEnum.SOURCE_MAC_ADDRESS.length);
        this.Source_MAC_Address = longToByte(bit, FrameEnum.SOURCE_MAC_ADDRESS.length);
    }
    private void parseType(Byte[] bytes) {
        long bit = byteToLong(bytes, FrameEnum.TYPE.startOffset, FrameEnum.TYPE.length);
        this.Type = longToByte(bit, FrameEnum.TYPE.length);
    }

    public Byte[] getDestination_MAC_Address() {
        return Destination_MAC_Address;
    }

    public void setDestination_MAC_Address(Byte[] destination_MAC_Address) {
        Destination_MAC_Address = destination_MAC_Address;
    }

    public Byte[] getSource_MAC_Address() {
        return Source_MAC_Address;
    }

    public void setSource_MAC_Address(Byte[] source_MAC_Address) {
        Source_MAC_Address = source_MAC_Address;
    }

    public Byte[] getType() {
        return Type;
    }

    public void setType(Byte[] type) {
        Type = type;
    }
}
