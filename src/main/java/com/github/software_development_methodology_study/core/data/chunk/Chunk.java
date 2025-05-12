package com.github.software_development_methodology_study.core.data.chunk;

import com.github.software_development_methodology_study.core.data.chunk.payload.Payload;

public final class Chunk <T> {
    private T header;
    private Payload payload;

    public T getHeader() {
        return header;
    }

    public Payload getPayload() {
        return payload;
    }

    public void setHeader(T header) {
        this.header = header;
    }

    public void setPayload(Payload payload) {
        this.payload = payload;
    }
}
