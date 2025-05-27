package com.github.software_development_methodology_study.core.layer;

import com.github.software_development_methodology_study.core.data.chunk.Chunk;
import com.github.software_development_methodology_study.core.data.chunk.header.Header;
import com.github.software_development_methodology_study.core.data.chunk.header.PacketHeader;
import com.github.software_development_methodology_study.core.data.chunk.payload.Payload;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.IntStream;

public class InternetLayer extends Layer<PacketHeader> {

    /**
     * 패킷 식별용 클래스 <br>
     * 패킷은 srcIp, dstIp, protocol, identification으로 식별한다.
     * @author SeungminShin97
     */
    private class FragmentKey {
        private final String srcIp;
        private final String dstIp;
        private final int protocol;
        private final int identification;

        public FragmentKey(String srcIp, String dstIp, int protocol, int identification) {
            this.srcIp = srcIp;
            this.dstIp = dstIp;
            this.protocol = protocol;
            this.identification = identification;
        }
    }

    /**
     * 들어온 패킷을 모아두는 Map
     * FragmentKey로 패킷 종류를 나누고, [Offset, Payload] 형태로 저장
     */
    private final ConcurrentHashMap<FragmentKey, ConcurrentHashMap<Byte, Byte[]>> fragmentBuffer = new ConcurrentHashMap<>();


    @Override
    public void receive(Chunk<Header> chunk) {
        if(chunk.getPayload() == null)
            throw new NullPointerException("Chunk is null");

        PacketHeader packetHeader = new PacketHeader();
//        chunk.setHeader(packetHeader);

        extractChunkAndSetNewHeader(chunk, packetHeader);

        // TODO: isLocalIPAddress 매개변수 수정
        // 아이피 주소 확인
        if(!isLocalIPAddress(null))
            return;

//        프로토콜 별 처리
//        -> tcp는 마지막 프래그먼트면 바로 합치고 올릴 수 있음<신뢰성>
//        -> UDP는 비신뢰성이라 다 오는 것 확인해야함
//        근데 굳이 나눠야 하나? UDP 안할 것 같은디
//        Protocol protocol = getProtocol(null);

        // TODO: fragmentBuffer에 헤더 넣기..

        // TODO: isLastFragment 매개변수 수정,
        //  getFragmentKey 메서드 작성,
        //  mergeFragments 매개변수 수정
        if(isLastFragment(null)) {
//            FragmentKey fragmentKey = getFragmentKey();
            Byte[] mergedFragment = mergeFragments(null, fragmentBuffer.get(null).size());
            chunk.setPayload(new Payload(mergedFragment));
            upperLayer.receive(chunk);
        }
    }

    @Override
    public void send(Chunk<Header> chunk) {
        PacketHeader ipHeader = new PacketHeader();
        // 헤더 설정
        chunk.setHeader(ipHeader);
        lowerLayer.send(chunk);
    }

    /**
     * Ethernet Layer에서 받은 청크의 페이로드에서 IP 헤더 정보를 파싱하여
     * 새로운 PacketHeader에 세팅한 뒤, 해당 헤더를 청크에 주입.
     * 버전, 길이, 식별자, 플래그, IP 주소 등 주요 필드를 바이트 배열로 변환해 설정.
     * @author judy78799
     */
    private void extractChunkAndSetNewHeader(Chunk<Header> chunk, PacketHeader header) {
        Byte[] lowerPayload = chunk.getPayload().getBytes();
        // 페이로드 에서 헤더 추출
        if(lowerPayload.length < 20){
            throw new IllegalArgumentException("유효하지 않은 헤더");
        }
        PacketHeader packetHeader = new PacketHeader();

        // Version과 IHL (상위 4비트씩) 비트 마스크 사용
        int versionAndIHL = Byte.toUnsignedInt(lowerPayload[0]);
        int version = (versionAndIHL >> 4) & 0x0F;
        int ihl = versionAndIHL & 0x0F;

        int totalLength = (Byte.toUnsignedInt(lowerPayload[2]) << 8) | Byte.toUnsignedInt(lowerPayload[3]);
        int identification = (Byte.toUnsignedInt(lowerPayload[4]) << 8) | Byte.toUnsignedInt(lowerPayload[5]);

        // Flags (3비트) + Fragment Offset (13비트)
        int flagsAndOffset = (Byte.toUnsignedInt(lowerPayload[6]) << 8) | Byte.toUnsignedInt(lowerPayload[7]);
        int flags = (flagsAndOffset >> 13) & 0x07;
        int fragmentOffset = flagsAndOffset & 0x1FFF;

        int ttl = Byte.toUnsignedInt(lowerPayload[8]);
        int protocol = Byte.toUnsignedInt(lowerPayload[9]);

        // 출발지 주소 Source IP (12~15)
        int srcIp =
                (Byte.toUnsignedInt(lowerPayload[12]) << 24) |
                        (Byte.toUnsignedInt(lowerPayload[13]) << 16) |
                        (Byte.toUnsignedInt(lowerPayload[14]) << 8) |
                        Byte.toUnsignedInt(lowerPayload[15]);

        // 목적지 주소 Destination IP (16~19)
        int dstIp =
                (Byte.toUnsignedInt(lowerPayload[16]) << 24) |
                        (Byte.toUnsignedInt(lowerPayload[17]) << 16) |
                        (Byte.toUnsignedInt(lowerPayload[18]) << 8) |
                        Byte.toUnsignedInt(lowerPayload[19]);

        // PacketHeader에 값 주입
        packetHeader.setVersion(new Byte[]{(byte)version}); //int -> Byte[]
        packetHeader.setIHL(new Byte[]{(byte)ihl});
        packetHeader.setTotal_Length(new Byte[]{(byte)totalLength});
        packetHeader.setIdentification(new Byte[]{(byte)identification});
        packetHeader.setFlags(new Byte[]{(byte)flags});
        packetHeader.setFragment_Offset(new Byte[]{(byte)fragmentOffset});
        packetHeader.setTTL(new Byte[]{(byte)ttl});
        packetHeader.setProtocol(new Byte[]{(byte)protocol});
        packetHeader.setSource_Address(new Byte[]{(byte)srcIp});
        packetHeader.setDestination_Address(new Byte[]{(byte)dstIp});

        // Header에 필드 주입
        chunk.setHeader(packetHeader); //PacketHeader
    }

    // TODO: 아이피 확인 메서드 구현
    private boolean isLocalIPAddress(Byte[] ipAddress) {
        // 아이피 확인
        return true;
    }

//    tcp, udp 확인용 / 추후 udp 확장 가능성 고려
//    private Protocol getProtocol(Byte[] protocol) {}

    // TODO: 마지막 프래그먼트 확인 메서드 구현
    private boolean isLastFragment(Byte[] MF) {
        // More Fragments
        return true;
    }

    /**
     * 프래그먼트 합치는 메서드
     * @param fragmentKey 패킷 식별키
     * @param fragmentCnt 식별된 패킷들 개수
     * @return 식별된 패킷들을 합친 패킷(세그먼트)
     * @author SeungminShin97
     */
    private Byte[] mergeFragments(FragmentKey fragmentKey, int fragmentCnt) {
        Map<Byte, Byte[]> fragmentMap = fragmentBuffer.get(fragmentKey);
        ArrayList<Byte> fragmentList = new ArrayList<>(fragmentMap.size());

        for(int i = 0; i < fragmentCnt; ++i)
            fragmentList.addAll(Arrays.asList(fragmentMap.get(i)));

        return fragmentList.toArray(new Byte[0]);
    }
}
