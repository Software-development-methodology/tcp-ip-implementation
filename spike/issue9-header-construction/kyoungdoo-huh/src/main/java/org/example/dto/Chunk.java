package org.example.dto;

import java.util.Arrays;

public class Chunk {
    private Header header;
    private byte[] data;

    public Chunk(byte[] data) {
        this.data = data;
    }

    public Chunk(Header header, byte[] data) {
        this.header = header;
        this.data = data;
    }

    public byte[] concat() {
        byte[] header = this.header.toByteArr();
        byte[] res = Arrays.copyOf(header, header.length + data.length);
        System.arraycopy(data, 0, res, header.length, data.length);

        return res;
    }

    public Header getHeader() {
        return this.header;
    }

    public byte[] getData() {
        return this.data;
    }
}
