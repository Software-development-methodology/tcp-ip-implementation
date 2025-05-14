package org.example.dto;

import java.util.Arrays;

public class Chunk<H extends Header> {
    private H header;
    private byte[] data;

    public Chunk(byte[] data) {
        this.data = data;
    }

    public Chunk(H header, byte[] data) {
        this.header = header;
        this.data = data;
    }

    public byte[] concat() {
        byte[] header = this.header.toByteArr();
        byte[] res = Arrays.copyOf(header, header.length + data.length);
        System.arraycopy(data, 0, res, header.length, data.length);

        return res;
    }

    public H getHeader() {
        return this.header;
    }

    public byte[] getData() {
        return this.data;
    }
}
