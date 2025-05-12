package chunk;

import header.Header;

public class Chunk {
    private Header header;
    private byte[] payload;

    public Chunk(Header header, byte[] payload) {
        this.header = header;
        this.payload = payload;
    }

    public Header getHeader() {
        return header;
    }

    public byte[] getPayload() {
        return payload;
    }

    public byte[] toBytes() {
        byte[] headerBytes = header.toBytes();
        byte[] result = new byte[headerBytes.length + payload.length];

        System.arraycopy(headerBytes, 0, result, 0, headerBytes.length);
        System.arraycopy(payload, 0, result, headerBytes.length, payload.length);

        return result;
    }
}
