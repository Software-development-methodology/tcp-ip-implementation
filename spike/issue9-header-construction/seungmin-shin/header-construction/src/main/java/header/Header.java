package header;

import java.util.BitSet;

public interface Header {

    Byte[] separateAndReturnByte(Byte[] bytes);
    Byte[] longToByte(long bit, int length);
    long byteToLong(Byte[] bytes, int start, int length);
    Byte[] getHeaderByteArray();
    int getHeaderLength();
}
