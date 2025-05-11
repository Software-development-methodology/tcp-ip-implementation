package com.github.can019.core.layer;

import com.github.can019.core.data.Chunk;

public abstract class Layer {
    protected Layer upperLayer;
    protected Layer lowerLayer;

    public abstract void send(Chunk chunk);
    public abstract void receive(Chunk chunk);

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
