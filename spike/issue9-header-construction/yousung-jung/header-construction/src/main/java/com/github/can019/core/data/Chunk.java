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
}
