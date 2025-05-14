package com.github.software_development_methodology_study.core.layer;

import com.github.software_development_methodology_study.core.data.chunk.Chunk;
import com.github.software_development_methodology_study.core.data.chunk.header.Header;
import com.github.software_development_methodology_study.core.data.chunk.payload.Payload;

public abstract class Layer<T extends Header> {
    protected Layer<? extends Header> lowerLayer;
    protected Layer<? extends Header> upperLayer;
    protected T header;
    protected Payload payload;

    public void setUpperLayer(Layer<? extends Header> upperLayer) {
        this.upperLayer = upperLayer;
    }

    public Layer<? extends Header> getUpperLayer() {
        return upperLayer;
    }

    public void setLowerLayer(Layer<? extends Header> lowerLayer) {
        this.lowerLayer = lowerLayer;
    }

    public Layer<? extends Header> getLowerLayer() {
        return lowerLayer;
    }

    public Payload getPayload() {
        return payload;
    }

    public void setPayload(Payload payload) {
        this.payload = payload;
    }

    public void setHeader(T header) {
        this.header = header;
    }

    public T getHeader() {
        return header;
    }

    public abstract void receive(Chunk<Header> chunk);

    public abstract void send(Chunk<Header> chunk);
}
