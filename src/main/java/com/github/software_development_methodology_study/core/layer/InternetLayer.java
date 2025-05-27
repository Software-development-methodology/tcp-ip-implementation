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
    private final ConcurrentHashMap<FragmentKey, ConcurrentHashMap<Integer, Byte[]>> fragmentBuffer = new ConcurrentHashMap<>();


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
        // Header에 필드 주입
        chunk.setHeader(header);
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

    private Byte[] mergeFragments(FragmentKey fragmentKey, int fragmentCnt) {
        Map<Integer, Byte[]> fragmentMap = fragmentBuffer.get(fragmentKey);
        ArrayList<Byte> fragmentList = new ArrayList<>(fragmentMap.size());

        for(int i = 0; i < fragmentCnt; ++i)
            fragmentList.addAll(Arrays.asList(fragmentMap.get(i)));

//        IntStream.range(0, fragmentCnt).forEach(i -> {fragmentList.add(fragmentMap.get(i))});

        return fragmentList.toArray(new Byte[0]);
    }
}
