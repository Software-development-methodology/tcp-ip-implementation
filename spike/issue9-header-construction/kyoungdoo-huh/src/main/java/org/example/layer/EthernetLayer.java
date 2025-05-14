package org.example.layer;

import org.example.dto.Chunk;
import org.example.dto.EthernetHeader;
import org.example.dto.Header;

import java.util.Arrays;

public class EthernetLayer extends Layer {
    private static EthernetLayer instance = new EthernetLayer();

    public static EthernetLayer getInstance() {
        if (instance == null) {
            instance = new EthernetLayer();
        }
        return instance;
    }

    private EthernetLayer() {
    }

    @Override
    public void setUpperLayer(Layer upperLayer) {
        super.upperLayer = upperLayer;
    }

    @Override
    public void setLowerLayer(Layer lowerLayer) {

    }

    public Chunk send(Chunk chunk) {
        // 정보 받아오기 chunk.get(...)
        Header ethernetHeader = new EthernetHeader();
        Chunk<EthernetHeader> ethernetFrame = new Chunk(ethernetHeader, chunk.concat());

        // super.lowerLayer.send(ethernetFrame);

        return ethernetFrame;
    }

    public Chunk receive(Chunk chunk) {
        // 정보 받아오기 chunk.get(...)
        Chunk<EthernetHeader> ethernetFrame = parseHeader(chunk.getData());
        EthernetHeader ethernetHeader = ethernetFrame.getHeader();
        System.out.println("Test Frame : " + Arrays.toString(ethernetHeader.getDestMACAddr()));

        return super.upperLayer.receive(ethernetFrame);
    }

    public Chunk parseHeader(byte[] payload) {
        // parsing logic
        byte[] data = Arrays.copyOfRange(payload, 0, 4);

        return new Chunk<>(new EthernetHeader(), data);
    }
}
