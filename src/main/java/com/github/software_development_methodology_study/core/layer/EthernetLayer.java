package com.github.software_development_methodology_study.core.layer;

import com.github.software_development_methodology_study.core.data.chunk.Chunk;
import com.github.software_development_methodology_study.core.data.chunk.header.EmptyHeader;
import com.github.software_development_methodology_study.core.data.chunk.header.FrameHeader;
import com.github.software_development_methodology_study.core.data.chunk.header.Header;
import com.github.software_development_methodology_study.core.data.chunk.payload.Payload;
import org.pcap4j.core.PcapNetworkInterface;

import static com.github.software_development_methodology_study.common.util.TypeConverter.StringToByteArray;

public class EthernetLayer extends Layer<FrameHeader> {
    @Override
    public boolean receive(Chunk<EmptyHeader> chunk, PcapNetworkInterface nic) {
        checkValidFrameSize(chunk);
        // @TODO:: FrameHeader 검증 및 제거
        checkValidFrameHeaderLessSize(chunk);
        return this.upperLayer.receive(chunk, nic);
    }

    @Override
    public boolean send(Chunk<Header> chunk, PcapNetworkInterface nic) {
        validateIncomingPayloadSize(chunk.getPayload());
        // @TODO:: do something
        return this.lowerLayer.send(chunk, nic);
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

        if(chunkSize > 1514) throw new IllegalArgumentException("Frame size too large");
        if(chunkSize < 60) throw new IllegalArgumentException("Frame size too small");
    }

    private void checkValidFrameHeaderLessSize(Chunk chunk) {
        int chunkSize = chunk.getPayload().getBytes().length + chunk.getHeader().getBytes().length;

        if(chunkSize > 1500) throw new IllegalArgumentException("FrameHeaderless chunk size too large");
        if(chunkSize < 46) throw new IllegalArgumentException("FrameHeaderless chunk size too small");
    }
}
