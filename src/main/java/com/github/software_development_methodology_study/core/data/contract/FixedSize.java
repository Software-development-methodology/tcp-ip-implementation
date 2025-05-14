package com.github.software_development_methodology_study.core.data.contract;

/**
 *
 * 고정된 크기임을 나타내는 인터페이스입니다.
 * @see com.github.software_development_methodology_study.core.data.chunk.header.Header
 *
 * @since 0.0.1
 * @version 1.0
 * @author jeong-yuseong
 */
public interface FixedSize {
    int expectedLength();
}
