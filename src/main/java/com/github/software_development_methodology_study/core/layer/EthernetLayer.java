package com.github.software_development_methodology_study.core.layer;

import com.github.software_development_methodology_study.core.data.chunk.Chunk;
import com.github.software_development_methodology_study.core.data.chunk.payload.Payload;

public class EthernetLayer extends Layer {
    @Override
    public void receive(Chunk chunk) {

    }

    @Override
    public void send(Chunk chunk) {

    }

    private Byte[] addMacAddress(Byte[] macAddress) {
        return null;
    }

    private boolean compareMacAddress() {
        return false;
    }

    private boolean validatePayloadSize(Payload payload) {
        return false;
    }
}
