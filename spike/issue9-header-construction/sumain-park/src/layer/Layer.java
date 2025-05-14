package layer;

import chunk.Chunk;

public abstract class Layer {
    protected Layer upperLayer;
    protected Layer lowerLayer;

    public void setUpperLayer(Layer upperLayer) {
        this.upperLayer = upperLayer;
    }

    public void setLowerLayer(Layer lowerLayer) {
        this.lowerLayer = lowerLayer;
    }

    public abstract void send(Chunk chunk);
    public abstract void receive(Chunk chunk);
}