package com.github.can019.core.data.chunk;

import com.github.can019.core.data.chunk.header.Header;
import com.github.can019.core.data.chunk.payload.Payload;

import java.util.Objects;
import java.util.Optional;

public class Chunk<H extends Header> {
    private final H header;
    private final Payload payload;

    public Chunk(H header, Payload payload) {
        this.header = header;
        this.payload = Objects.requireNonNull(payload, "payload must not be null");
    }

    public Optional<H> getHeader() {
        return Optional.ofNullable(header);
    }

    public Payload getPayload() {
        return payload;
    }

    @Override
    public String toString() {
        return "Chunk{" +
                "header=" + header +
                ", payload=" + payload +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Chunk<?> that)) return false;
        return header.equals(that.header) && payload.equals(that.payload);
    }

    @Override
    public int hashCode() {
        return Objects.hash(header, payload);
    }
}
