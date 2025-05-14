package com.github.can019.core.layer;

import com.github.can019.core.data.chunk.Chunk;
import com.github.can019.core.data.chunk.header.FrameHeader;
import com.github.can019.core.data.chunk.header.Header;


public class EthernetLayer extends Layer<FrameHeader> {

    public EthernetLayer(Layer<? extends Header<?>> upperLayer) {
        super(upperLayer);
    }

    @Override
    protected boolean isSupportedHeader(Header<?> header) {
        return false;
    }

    @Override
    protected void handle(Chunk<FrameHeader> chunk) {

    }
}
