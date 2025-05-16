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

    @Test
    void 두_레이어끼리_receive_체인_시_형변환_없이_타입이_변경되어야한다() {
        Chunk<Header> chunk = new Chunk<>();
        chunk.setHeader(new EmptyHeader());

        TestLayer testLayer = new TestLayer();
        TestLayer2 testLayer2 = new TestLayer2();
        testLayer2.setUpperLayer(testLayer);

        testLayer2.receive(chunk);

        assertAll(
                () -> assertTrue(testLayer.receivedHeader instanceof EmptyHeader),
                () -> assertTrue(testLayer.newHeader instanceof TestHeader)
        );
    }
}