package com.github.can019.core.layer;

import com.github.can019.core.data.Chunk;

public abstract class Layer {
    protected Layer upperLayer;
    protected Layer lowerLayer;

    //public abstract void send(Chunk chunk);
    public final void receive(Chunk chunk) {
        Chunk newChunk = castToSupportedChunk(chunk);
        process(newChunk);
        this.upperLayer.receive(process(newChunk));
    }

    protected abstract Chunk castToSupportedChunk(Chunk chunk);

    protected abstract Chunk process(Chunk chunk);

    public void setLowerLayer(Layer lowerLayer) {
        this.lowerLayer = lowerLayer;
    }

    public void setUpperLayer(Layer upperLayer) {
        this.upperLayer = upperLayer;
    }

    public Layer getLowerLayer() {
        return lowerLayer;
    }

    public Layer getUpperLayer() {
        return upperLayer;
    }
}