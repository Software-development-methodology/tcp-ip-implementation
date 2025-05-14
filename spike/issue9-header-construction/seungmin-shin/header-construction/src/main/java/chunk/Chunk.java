package chunk;

import header.Header;

public class Chunk {
    private Byte[] payload;
    private Header header = null;

    public Chunk(Byte[] bytes) {
        this. payload = bytes;
    }

    public void separateHeader() {
        payload = header.separateAndReturnByte(payload);
    }

    public void mergeHeader() {
        if(header == null)
            throw new NullPointerException();
        Byte[] newPayload = new Byte[payload.length + header.getHeaderLength()];
        System.arraycopy(header.getHeaderByteArray(), 0, newPayload, 0, header.getHeaderLength());
        System.arraycopy(payload, 0, newPayload, header.getHeaderLength(), payload.length);
        this.payload = newPayload;
    }

    public void setHeader(Header header) {
        this.header = header;
    }

    public Header getHeader() {
        return header;
    }

    public Byte[] getPayload() {
        return payload;
    }
}
