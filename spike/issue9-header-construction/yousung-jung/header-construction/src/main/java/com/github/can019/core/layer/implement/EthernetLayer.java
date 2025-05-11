package com.github.can019.core.layer.implement;

import com.github.can019.core.data.Chunk;
import com.github.can019.core.data.header.implement.FrameHeader;
import com.github.can019.core.layer.Layer;

public class EthernetLayer extends Layer {

    @Override
    protected Chunk castToSupportedChunk(Chunk chunk) {
        chunk.setHeader(new FrameHeader(chunk.getHeader()));
        return chunk;
    }

    @Override
    protected Chunk process(Chunk chunk) {
        return null;
    }
}
