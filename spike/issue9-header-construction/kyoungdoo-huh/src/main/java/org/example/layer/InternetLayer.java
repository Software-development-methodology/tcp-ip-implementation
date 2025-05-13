package org.example.layer;

import org.example.dto.Chunk;
import org.example.dto.Header;
import org.example.dto.IPHeader;

import java.util.Arrays;

public class InternetLayer extends Layer {
    private static InternetLayer instance = new InternetLayer();

    public static InternetLayer getInstance() {
        if (instance == null) {
            instance = new InternetLayer();
        }
        return instance;
    }

    private InternetLayer() {
    }

    @Override
    public void setUpperLayer(Layer upperLayer) {

    }

    @Override
    public void setLowerLayer(Layer lowerLayer) {
        super.lowerLayer = lowerLayer;
    }

    public Chunk send(Chunk chunk) {
        // 정보 받아오기 chunk.get(...)
        Header ipHeader = new IPHeader();
        Chunk ipPacket = new Chunk(ipHeader, chunk.concat());

        return super.lowerLayer.send(ipPacket);
    }

    public Chunk receive(Chunk chunk) {
        // 정보 받아오기 chunk.get(...)
        Chunk ipPacket = parseHeader(chunk.getData());

        // super.upperLayer.receive(ipPacket);

        return ipPacket;
    }

    public Chunk parseHeader(byte[] payload) {
        // parsing logic
        byte[] data = Arrays.copyOfRange(payload, 0, 4);

        return new Chunk(new IPHeader(), data);
    }
}
