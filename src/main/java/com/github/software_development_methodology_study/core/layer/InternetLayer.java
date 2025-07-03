package com.github.software_development_methodology_study.core.layer;

import com.github.software_development_methodology_study.core.data.chunk.Chunk;
import com.github.software_development_methodology_study.core.data.chunk.header.EmptyHeader;
import com.github.software_development_methodology_study.core.data.chunk.header.Header;
import com.github.software_development_methodology_study.core.data.chunk.header.PacketHeader;
import com.github.software_development_methodology_study.core.data.chunk.payload.Payload;
import lombok.extern.slf4j.Slf4j;
import org.pcap4j.core.PcapNetworkInterface;

import java.util.Arrays;

import static com.github.software_development_methodology_study.core.data.chunk.header.IPHeaderField.*;


@Slf4j
public class InternetLayer extends Layer<PacketHeader> {

    @Override
    public boolean receive(Chunk<EmptyHeader> chunk, PcapNetworkInterface nic) {
        // IP 헤더 추출
        PacketHeader packetHeader = extractHeader(chunk.getPayload());

        // IP 헤더 검증
        if (!validateIPHeader(packetHeader)) {
            return false;
        }

        // 목적지 IP 주소 확인 (실제 구현에서는 호스트의 IP 주소와 비교해야 함)
        if (!isLocalIPAddress(packetHeader.getDestinationIP())) {
            return false;
        }

        // 상위 계층으로 전달
        log.debug("IP 패킷 수신: 프로토콜={}", Arrays.toString(packetHeader.getProtocol()));

        return this.upperLayer.receive(chunk, nic);
    }

    @Override
    public boolean send(Chunk<Header> chunk, PcapNetworkInterface nic) {
        // 상위 계층에서 받은 데이터 검증
        validateIncomingPayloadSize(chunk.getPayload());

        // IP 헤더 생성 및 설정


        // 하위 계층으로 전달
        return this.lowerLayer.send(chunk, nic);
    }


    private PacketHeader extractHeader(Payload payload) {
        Byte[] payloadArr = payload.getBytes();
        int totalLength = payloadArr.length;
        //payload에서 IP헤더 추출
        return PacketHeader.builder()
                .versionAndIHL(Arrays.copyOfRange(payloadArr, VERSION_IHL.getStartIndex(totalLength), VERSION_IHL.getEndIndex() + 1))
                .tos(Arrays.copyOfRange(payloadArr, TOS.getStartIndex(totalLength), TOS.getEndIndex() + 1))
                .totalLength(Arrays.copyOfRange(payloadArr, TOTAL_LENGTH.getStartIndex(totalLength), TOTAL_LENGTH.getEndIndex() + 1))
                .identification(Arrays.copyOfRange(payloadArr, IDENTIFICATION.getStartIndex(totalLength), IDENTIFICATION.getEndIndex() + 1))
                .flagsAndOffset(Arrays.copyOfRange(payloadArr, FLAGS_FRAGMENT_OFFSET.getStartIndex(totalLength), FLAGS_FRAGMENT_OFFSET.getEndIndex() + 1))
                .ttl(Arrays.copyOfRange(payloadArr, TTL.getStartIndex(totalLength), TTL.getEndIndex() + 1))
                .protocol(Arrays.copyOfRange(payloadArr, PROTOCOL.getStartIndex(totalLength), PROTOCOL.getEndIndex() + 1))
                .headerChecksum(Arrays.copyOfRange(payloadArr, HEADER_CHECKSUM.getStartIndex(totalLength), HEADER_CHECKSUM.getEndIndex() + 1))
                .sourceIP(Arrays.copyOfRange(payloadArr, SOURCE_IP.getStartIndex(totalLength), SOURCE_IP.getEndIndex() + 1))
                .destinationIP(Arrays.copyOfRange(payloadArr, DESTINATION_IP.getStartIndex(totalLength), DESTINATION_IP.getEndIndex() + 1))
                .options(new Byte[0])  // 옵션 필드는 기본적으로 비어있음
                .padding(new Byte[0])  // 패딩 필드는 기본적으로 비어있음
                .build();
    }


    private boolean validateIPHeader(PacketHeader ipHeader) {

        // 버전 확인 (IPv4만 지원)
        Byte versionAndIHL = ipHeader.getVersionAndIHL()[0];
        int version = (versionAndIHL & 0xF0) >> 4; //IPv4의 경우 0x40(01000000) & 0xF0(11110000) = 0x40(01000000) >> 4 = 0x4(00000100) = 4
        if (version != 4) {
            log.debug("지원하지 않는 IP 버전: {}", version);
            return false;
        }

        // 헤더 길이 확인
        int headerLength = (versionAndIHL & 0x0F) * 4; // 32비트 워드 단위로 표현됨
        if (headerLength < 20) {
            log.debug("잘못된 IP 헤더 길이: {}", headerLength);
            return false;
        }

        // 체크섬 검증 (실제 구현에서는 체크섬 계산 및 검증 로직 추가 필요)

        return true;
    }


    private void validateIncomingPayloadSize(Payload payload) {
        // IP 패킷의 최대 크기는 65535바이트 (헤더 포함)
        if (payload.getBytes().length > 65515) { // 65535 - 20(기본 헤더 크기)
            throw new IllegalArgumentException("페이로드 크기가 너무 큽니다");
        }
    }


    private boolean isLocalIPAddress(Byte[] ipAddress) {
        // 실제 구현에서는 호스트의 IP 주소와 비교해야 함
        return true;
    }
}
