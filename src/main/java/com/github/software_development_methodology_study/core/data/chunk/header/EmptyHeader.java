package com.github.software_development_methodology_study.core.data.chunk.header;


import com.github.software_development_methodology_study.core.data.contract.FixedSize;

/**
 *
 * 빈 헤더.
 * <p>
 * {@code null}을 사용하지 않고 의미를 명확하게 하기 위한 클래스입니다.
 *
 * @since 0.0.1
 * @version 1.0
 * @author jeong-yuseong
 */
public class EmptyHeader extends Header implements FixedSize {
    @Override
    public int expectedLength() {
        return 0;
    }
}
