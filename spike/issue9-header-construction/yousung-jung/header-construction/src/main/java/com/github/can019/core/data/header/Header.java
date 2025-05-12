package com.github.can019.core.data.header;

import java.util.Objects;

public abstract class Header {
    protected Byte[] data;

    protected Header(Header header) {
        if(Objects.isNull(header))
            this.data = new Byte[0];
        else
            this.data = header.getData();
    }

    public final Byte[] getData() {
        return this.data;
    }
}
