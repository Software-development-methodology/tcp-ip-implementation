package com.github.software_development_methodology_study.core.layer;

import com.github.software_development_methodology_study.core.data.chunk.header.Header;

public class TestHeader extends Header {
    @Override
    public int expectedLength() {
        return 0;
    }

    public boolean testHeaderMethod() {
        return true;
    }
}
