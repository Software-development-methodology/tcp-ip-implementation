package com.github.can019.core.data.chunk.payload;

import java.util.Arrays;

public final class Payload {
    private final byte[] bytes;

    public Payload(byte[] bytes) {
        this.bytes = Arrays.copyOf(bytes, bytes.length);
    }

    public byte[] getBytes() {
        return Arrays.copyOf(bytes, bytes.length);
    }

    public int size() {
        return bytes.length;
    }

    public Payload slice(int from, int to) {
        if (from < 0 || to > bytes.length || from > to) {
            throw new IndexOutOfBoundsException("Invalid slice range");
        }
        return new Payload(Arrays.copyOfRange(bytes, from, to));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Payload)) return false;
        Payload payload = (Payload) o;
        return Arrays.equals(bytes, payload.bytes);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(bytes);
    }

    @Override
    public String toString() {
        return "Payload[length=" + bytes.length + "]";
    }
}