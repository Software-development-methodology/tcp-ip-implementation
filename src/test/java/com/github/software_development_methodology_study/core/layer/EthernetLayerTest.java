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

    @Test
    void receive_에서_헤더_제거_후_payload_크기가_46byte_미만이면_illigalArgumentException이_발생한다() {
        Chunk<EmptyHeader> chunk = new Chunk<>();
        Byte[] bytes = new Byte[30];
        chunk.setPayload(new Payload(bytes));

        assertThrows(IllegalArgumentException.class, ()-> ethernetLayer.receive(chunk));
    }

    @Test
    void receive_에서_헤더_제거_후_payload_크기가_정확히_46byte면_예외가_발생하지_않는다() {
        Chunk<EmptyHeader> chunk = new Chunk<>();
        Byte[] bytes = new Byte[46]; // 경계값
        chunk.setPayload(new Payload(bytes));

        assertDoesNotThrow(() -> ethernetLayer.receive(chunk));
    }

    @Test
    void receive_에서_헤더_제거_후_payload_크기가_1500byte_초과하면_illigalArgumentException이_발생한다() {
        Chunk<EmptyHeader> chunk = new Chunk<>();
        Byte[] bytes = new Byte[1501];
        chunk.setPayload(new Payload(bytes));

        assertThrows(IllegalArgumentException.class, ()-> ethernetLayer.receive(chunk));
    }

    @Test
    void receive_에서_헤더_제거_후_payload_크기가_정확히_1500byte면_예외가_발생하지_않는다() {
        Chunk<EmptyHeader> chunk = new Chunk<>();
        Byte[] bytes = new Byte[1500]; // 경계값
        chunk.setPayload(new Payload(bytes));

        assertDoesNotThrow(() -> ethernetLayer.receive(chunk));
    }

    @Test
    void receive_호출_시_Chunk_크기가_60byte를_미만이면_illigalArgumentException이_발생한다() {
        Chunk<EmptyHeader> chunk = new Chunk<>();
        Byte[] bytes = new Byte[57];
        chunk.setPayload(new Payload(bytes));

        assertThrows(IllegalArgumentException.class, ()-> ethernetLayer.receive(chunk));
    }

    @Test
    void receive_호출_시_Chunk_크기가_정확히_60byte면_illigalArgumentException이_발생하지_않는다() {
        Chunk<EmptyHeader> chunk = new Chunk<>();
        Byte[] bytes = new Byte[60]; // 경계값
        chunk.setPayload(new Payload(bytes));

        assertDoesNotThrow(()-> ethernetLayer.receive(chunk));
    }


    @Test
    void receive_호출_시_Chunk_크기가_1514byte_초과하면_illigalArgumentException이_발생한다() {
        Chunk<EmptyHeader> chunk = new Chunk<>();
        Byte[] bytes = new Byte[1518];
        chunk.setPayload(new Payload(bytes));

        assertThrows(IllegalArgumentException.class, ()-> ethernetLayer.receive(chunk));
    }

    @Test
    void receive_호출_시_Chunk_크기가_정확히_1514byte면_illigalArgumentException이_발생하지_않는다() {
        Chunk<EmptyHeader> chunk = new Chunk<>();
        Byte[] bytes = new Byte[1514]; // 경계값
        chunk.setPayload(new Payload(bytes));

        assertDoesNotThrow(()-> ethernetLayer.receive(chunk));
    }
}