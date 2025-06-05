package com.github.software_development_methodology_study.core.layer;

import com.github.software_development_methodology_study.core.data.chunk.Chunk;
import com.github.software_development_methodology_study.core.data.chunk.header.EmptyHeader;
import com.github.software_development_methodology_study.core.data.chunk.header.FrameHeader;
import com.github.software_development_methodology_study.core.data.chunk.header.Header;
import com.github.software_development_methodology_study.core.data.chunk.payload.Payload;
import lombok.extern.slf4j.Slf4j;
import org.pcap4j.core.PcapNetworkInterface;

import java.util.Arrays;

import static com.github.software_development_methodology_study.common.util.TypeConverter.toObject;
import static com.github.software_development_methodology_study.core.data.chunk.header.FrameHeaderField.*;

@Slf4j
public class EthernetLayer extends Layer<FrameHeader> {
    @Override
    public boolean receive(Chunk<EmptyHeader> chunk, PcapNetworkInterface nic) {
        // framing
        if(isInvalidFrameSize(chunk)) {
            return false;
        }
        // header 추출
        FrameHeader frameHeader = extractHeader(chunk.getPayload());

        // 목적지 주소랑 호스트 주소 비교
        if (!Arrays.equals(frameHeader.getDestinationMac(), toObject(nic.getLinkLayerAddresses().get(0).getAddress()))) {
            return false;
        }

        // Type debugging log
        log.debug(Arrays.toString(frameHeader.getType()));

        isInvalidFrameHeaderLessSize(chunk);
        return this.upperLayer.receive(chunk, nic);
    }

    @Override
    public boolean send(Chunk<Header> chunk, PcapNetworkInterface nic) {
        validateIncomingPayloadSize(chunk.getPayload());
        // @TODO:: do something
        return this.lowerLayer.send(chunk, nic);
    }

    private FrameHeader extractHeader(Payload payload) {
        Byte[] payloadArr = payload.getBytes();
        int totalLength = payloadArr.length;

        return FrameHeader.builder()
                .destinationMac(Arrays.copyOfRange(payloadArr, DESTINATION_MAC.getStartIndex(totalLength), DESTINATION_MAC.getEndIndex() + 1))
                .srcMac(Arrays.copyOfRange(payloadArr, SRC_MAC.getStartIndex(totalLength), SRC_MAC.getEndIndex() + 1))
                .type(Arrays.copyOfRange(payloadArr, TYPE.getStartIndex(totalLength), TYPE.getEndIndex() + 1))
                .build();
    }

    private void validateIncomingPayloadSize(Payload payload) {
        if(payload.getBytes().length > 1500) throw new IllegalArgumentException("Payload length too large");
    }

    private boolean isInvalidFrameSize(Chunk chunk) {
        int chunkSize = chunk.getPayload().getBytes().length + chunk.getHeader().getBytes().length;

        return chunkSize > 1514 || chunkSize < 60;
    }

    private boolean isInvalidFrameHeaderLessSize(Chunk chunk) {
        int chunkSize = chunk.getPayload().getBytes().length + chunk.getHeader().getBytes().length;

        return chunkSize > 1500 || chunkSize < 46;
    }
}
