package com.github.software_development_methodology_study.core.data.chunk.header;

public abstract class Header {
    protected final Byte[] bytes;

    protected Header() {
        this(new Byte[0]);
    }

    protected Header(Byte[] bytes) {
        this.bytes = bytes;
    }

    public final Byte[] getBytes() {
        return bytes;
    }
}
