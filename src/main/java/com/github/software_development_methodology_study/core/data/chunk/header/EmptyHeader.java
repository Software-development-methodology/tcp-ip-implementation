package com.github.software_development_methodology_study.core.data.chunk.header;

public class EmptyHeader extends Header{
    @Override
    public int expectedLength() {
        return 0;
    }
}
