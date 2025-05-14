package com.github.software_development_methodology_study.core.data.chunk;

import com.github.software_development_methodology_study.core.data.chunk.payload.Payload;


/**
 * 네트워크 또는 데이터 처리 과정에서 사용하는 데이터 단위인 청크(Chunk)를 나타냅니다.
 * <p>
 * {@code header}와 {@code payload}로 구성되며, {@code header}는 타입 매개변수로 정의됩니다.
 *
 * @param <T> 이 청크에 포함되는 헤더 타입
 *
 * @see com.github.software_development_methodology_study.core.data.chunk.header.Header
 * @see com.github.software_development_methodology_study.core.data.chunk.payload.Payload
 *
 * @since 0.0.1
 * @version 1.0
 * @author jeong-yuseong
 */
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
