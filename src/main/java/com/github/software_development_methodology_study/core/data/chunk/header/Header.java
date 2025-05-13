package com.github.software_development_methodology_study.core.data.chunk.header;

import com.github.software_development_methodology_study.core.data.contract.FixedSize;

public abstract class Header implements FixedSize {
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
