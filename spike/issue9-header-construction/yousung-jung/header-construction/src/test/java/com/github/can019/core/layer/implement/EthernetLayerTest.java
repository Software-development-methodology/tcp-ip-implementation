package com.github.can019.core.layer.implement;

import com.github.can019.core.data.Chunk;
import com.github.can019.core.data.Payload;
import com.github.can019.core.data.header.implement.FrameHeader;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.awt.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.assertj.core.api.Assertions.*;

class EthernetLayerTest {
    private EthernetLayer ethernetLayer;
    private InternetLayer internetLayer;

    @BeforeEach
    void beforeEach() {
        this.ethernetLayer = new EthernetLayer();
        this.internetLayer = new InternetLayer();

        this.ethernetLayer.setUpperLayer(this.internetLayer);
        this.internetLayer.setLowerLayer(this.ethernetLayer);
    }

    @Test
    void cast_테스트(){
        Payload payload = new Payload();
        Chunk beforeCastChunk = new Chunk(payload);

        Chunk afterCastChunk = ethernetLayer.castToSupportedChunk(beforeCastChunk);

        assertThat(afterCastChunk)
                .as("afterCastChunk should be instance of FrameHeader")
                .isInstanceOf(FrameHeader.class);
    }
}