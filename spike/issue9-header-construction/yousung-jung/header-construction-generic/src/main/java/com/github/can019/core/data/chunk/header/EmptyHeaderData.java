package com.github.can019.core.data.chunk.header;

public final class EmptyHeaderData implements HeaderData {
    @Override
    public boolean equals(Object o) {
        return o instanceof EmptyHeaderData;
    }

    @Override
    public int hashCode() {
        return 0;
    }

    @Override
    public String toString() {
        return "EmptyHeaderData";
    }
}
