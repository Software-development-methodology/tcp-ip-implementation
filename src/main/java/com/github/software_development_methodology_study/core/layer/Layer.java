package com.github.software_development_methodology_study.core.layer;

import com.github.software_development_methodology_study.core.data.chunk.Chunk;
import com.github.software_development_methodology_study.core.data.chunk.header.Header;
import com.github.software_development_methodology_study.core.data.chunk.payload.Payload;

/**
 * Layer 추상 클래스입니다.
 * <p>
 * 모든 Layer는 해당 클래스를 상속
 *
 * @since 0.0.1
 * @version 1.0.0
 * @author jeong-yuseong
 * @param <T>
 */
public abstract class Layer<T extends Header> {
    protected Layer<? extends Header> lowerLayer;
    protected Layer<? extends Header> upperLayer;

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

    /**
     * 하위 레이어에서 Chunk를 전달받아 처리하는 메서드
     * @param chunk
     */
    public abstract void receive(Chunk<Header> chunk);

    /**
     * 상위 레이어에서 Chunk를 전달받아 처리하는 메서드
     * @param chunk
     */
    public abstract void send(Chunk<Header> chunk);
}
