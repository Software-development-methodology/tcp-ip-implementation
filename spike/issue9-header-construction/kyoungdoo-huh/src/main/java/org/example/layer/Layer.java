package org.example.layer;

import org.example.dto.Chunk;

public abstract class Layer {
    Layer upperLayer;
    Layer lowerLayer;

    public abstract void setUpperLayer(Layer upperLayer);
    public abstract void setLowerLayer(Layer lowerLayer);
    public abstract Chunk send(Chunk chunk);
    public abstract Chunk receive(Chunk chunk);
    protected abstract Chunk parseHeader(byte[] payload);
}
