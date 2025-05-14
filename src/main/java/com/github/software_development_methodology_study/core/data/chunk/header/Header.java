package com.github.software_development_methodology_study.core.data.chunk.header;

import com.github.software_development_methodology_study.core.data.contract.FixedSize;

/**
 * 네트워크 전송 계층에서 사용되는 공통 헤더 추상 클래스입니다.
 * <p>
 * 모든 구체적인 헤더 클래스는 이 클래스를 확장하여 고정 크기 바이트 배열을 기반으로 구성됩니다.
 *
 * @see com.github.software_development_methodology_study.core.data.chunk.header.EmptyHeader
 * @since 0.0.1
 * @version 1.0
 * @author jeong-yuseong
 */
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
