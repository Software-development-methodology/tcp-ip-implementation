package com.github.software_development_methodology_study.core.layer;

import com.github.software_development_methodology_study.core.data.chunk.Chunk;
import com.github.software_development_methodology_study.core.data.chunk.header.EmptyHeader;
import com.github.software_development_methodology_study.core.data.chunk.payload.Payload;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EthernetLayerTest {
    private EthernetLayer ethernetLayer;

    @BeforeEach
    void setUp() {
        ethernetLayer = new EthernetLayer();
    }


    @Test
    void send_호출_시_Chunk의_Payload가_1500byte_보다_크면_illigalArgumentException이_발생한다() {
        Byte[] bytes = new Byte[(int) (Math.random() * 100) + 1501];
        Chunk<EmptyHeader> chunk = new Chunk<>();
        chunk.setPayload(new Payload(bytes));

        assertThrows(IllegalArgumentException.class, () -> ethernetLayer.send(chunk));
    }
}