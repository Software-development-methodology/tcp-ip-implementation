package com.github.can019.core.data.header;

public abstract class Header {
    protected Byte[] data;

    protected Header(Header header) {
        this.data = header.getData();
    }

    public final Byte[] getData() {
        return this.data;
    }
}
