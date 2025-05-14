package com.github.software_development_methodology_study.core.layer;

import com.github.software_development_methodology_study.core.data.chunk.Chunk;
import com.github.software_development_methodology_study.core.data.chunk.header.EmptyHeader;
import com.github.software_development_methodology_study.core.data.chunk.header.Header;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class LayerTest {

    @Test
    void 레이어에서_receive가_호출되었을_때_Header_타입이_변경되어야한다() {
        Chunk<Header> chunk = new Chunk<>();
        chunk.setHeader(new EmptyHeader());

        //Layer<?> layer = new TestLayer();
        TestLayer testLayer = new TestLayer();
        testLayer.receive(chunk);

        assertAll(
                () -> assertTrue(testLayer.receivedHeader instanceof EmptyHeader),
                () -> assertTrue(testLayer.newHeader instanceof TestHeader)
        );
    }
}