package com.github.software_development_methodology_study.core.layer;

import com.github.software_development_methodology_study.core.data.chunk.Chunk;
import com.github.software_development_methodology_study.core.data.chunk.payload.Payload;

public class EthernetLayer extends Layer {
    @Override
    public void receive(Chunk chunk) {
        checkValidFrameSize(chunk);
        // @TODO:: FrameHeader 검증 및 제거
        checkValidFrameHeaderLessSize(chunk);
    }

    @Override
    public void send(Chunk chunk) {
        validateIncomingPayloadSize(chunk.getPayload());

    }

    private Byte[] addMacAddress(Byte[] macAddress) {
        return null;
    }

    private boolean compareMacAddress() {
        return false;
    }

    private void validateIncomingPayloadSize(Payload payload) {
        if(payload.getBytes().length > 1500) throw new IllegalArgumentException("Payload length too large");
    }

    private void checkValidFrameSize(Chunk chunk) {
        int chunkSize = chunk.getPayload().getBytes().length + chunk.getHeader().getBytes().length;

        System.out.println(chunkSize);
        if(chunkSize > 1514) throw new IllegalArgumentException("Frame size too large");
        if(chunkSize < 60) throw new IllegalArgumentException("Frame size too small");
    }

    private void checkValidFrameHeaderLessSize(Chunk chunk) {
        int chunkSize = chunk.getPayload().getBytes().length + chunk.getHeader().getBytes().length;

        System.out.println(chunkSize);
        if(chunkSize > 1500) throw new IllegalArgumentException("FrameHeaderless chunk size too large");
        if(chunkSize < 46) throw new IllegalArgumentException("FrameHeaderless chunk size too small");
    }
}
