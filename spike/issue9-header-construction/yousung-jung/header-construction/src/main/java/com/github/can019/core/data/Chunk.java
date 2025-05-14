package com.github.can019.core.data;

import com.github.can019.core.data.header.Header;

public class Chunk {
    Header header;
    Payload payload;

    public Chunk(Payload payload) {
        this(null, payload);
    }

    public Chunk(Header header, Payload payload){
        this.header = header;
        this.payload = payload;
    }

    public Header getHeader() {
        return header;
    }

    public void setHeader(Header header) {
        this.header = header;
    }

    public Payload getPayload() {
        return payload;
    }

    public void setPayload(Payload payload) {
        this.payload = payload;
    }
}
