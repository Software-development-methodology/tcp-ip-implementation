package com.github.software_development_methodology_study.core.data.chunk.payload;


/**
 *
 * @since 0.0.1
 * @version 1.0
 * @author jeong-yuseong
 */
public final class Payload {
    private Byte[] bytes;

    public Payload() {

    }

    public Payload(Byte[] bytes) {
        this.bytes = bytes;
    }

    public Byte[] getBytes() {
        return bytes;
    }

    public void setBytes(Byte[] bytes) {
        this.bytes = bytes;
    }
}
