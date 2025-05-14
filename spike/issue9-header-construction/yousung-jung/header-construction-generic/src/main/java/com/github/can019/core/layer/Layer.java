package com.github.can019.core.layer;

import com.github.can019.core.data.chunk.Chunk;
import com.github.can019.core.data.chunk.header.FrameHeader;
import com.github.can019.core.data.chunk.header.Header;

public abstract class Layer<H extends Header<?>> {
    protected final Layer<? extends Header<?>> upperLayer;

    public final void receive(Chunk<? extends Header<?>> chunk) {
//        if (!isSupportedHeader(chunk.getHeader())) {
//            throw new IllegalArgumentException("Unsupported header type: " + chunk.getHeader().getClass());
//        }

        // 안전하게 캐스팅해서 처리
//        Chunk<H> typedChunk = (Chunk<H>) chunk;

        handle(cast(chunk));
    }

    public Layer(Layer<? extends Header<?>> upperLayer) {
        this.upperLayer = upperLayer;
    }

    protected abstract Chunk cast(Chunk<? extends Header<?>> chunk);

    protected abstract boolean isSupportedHeader(Header<?> header);
    protected abstract void handle(Chunk<H> chunk);
}
