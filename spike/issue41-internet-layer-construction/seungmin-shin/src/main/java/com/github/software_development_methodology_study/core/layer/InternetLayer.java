package com.github.software_development_methodology_study.core.layer;

import com.github.software_development_methodology_study.core.data.chunk.Chunk;
import com.github.software_development_methodology_study.core.data.chunk.header.Header;
import com.github.software_development_methodology_study.core.data.chunk.header.PacketHeader;
import com.github.software_development_methodology_study.core.data.chunk.payload.Payload;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.IntStream;

public class InternetLayer extends Layer<PacketHeader> {

    /**
     * 패킷 식별용 클래스 <br>
     * 패킷은 srcIp, dstIp, protocol, identification으로 식별한다. <br>
     * 필드들은 Byte[] 로 저장 <br>
     * fragmentBuffer에 [FragmentKey, [Offset, Payload]] 형태로 저장
     */
    private class FragmentKey {
        private final Byte[] srcIp;
        private final Byte[] dstIp;
        private final Byte[] protocol;
        private final Byte[] identification;

        public FragmentKey(Byte[] srcIp, Byte[] dstIp, Byte[] protocol, Byte[] identification) {
            this.srcIp = srcIp;
            this.dstIp = dstIp;
            this.protocol = protocol;
            this.identification = identification;
        }

        @Override
        public boolean equals(Object obj) {
            if (this ==  obj) return true;
            if (obj == null || this.getClass() != obj.getClass()) return false;
            FragmentKey key = (FragmentKey) obj;
            return (Arrays.equals(this.srcIp, key.srcIp)
                    && Arrays.equals(this.dstIp, key.dstIp)
                    && Arrays.equals(this.protocol, key.protocol)
                    && Arrays.equals(this.identification, key.identification));
        }

        @Override
        public int hashCode() {
            return Objects.hash(
                    Arrays.hashCode(srcIp),
                    Arrays.hashCode(dstIp),
                    Arrays.hashCode(protocol),
                    Arrays.hashCode(identification)
            );
        }
    }

    /**
     * 들어온 패킷들을 저장하는 Map
     * [FragmentKey, [Offset, Payload]] 형태로 저장
     * Offset은 Map에서 꺼낼 때 편의를 위해 저장할 때 Integer로 저장
     */
    private final ConcurrentHashMap<FragmentKey, ConcurrentHashMap<Integer, Byte[]>> fragmentBuffer = new ConcurrentHashMap<>();


    @Override
    public void receive(Chunk<Header> chunk) {
        if(chunk.getPayload() == null)
            throw new NullPointerException("Chunk is null");

        PacketHeader packetHeader = new PacketHeader();

        // MEETING: 더 좋은 방법이 있는지 -> 지금 방법이 좋은지
        extractChunkAndSetNewHeader(chunk, packetHeader);

        // FIXME: Github Discussion 참고
        // 목적지 아이피 주소 확인
        if(!isLocalIPAddress(packetHeader.getDestination_Address()))
            return;

//        프로토콜 별 처리
//        -> tcp는 마지막 프래그먼트면 바로 합치고 올릴 수 있음<신뢰성>
//        -> UDP는 비신뢰성이라 다 오는 것 확인해야함
//        근데 굳이 나눠야 하나? UDP 안할 것 같은디
//        Protocol protocol = getProtocol(null);

        // TODO: fragmentBuffer에 헤더 넣기..
        // fragmentBuffer에 fragment 저장
        storeFragmentToFragmentBuffer(packetHeader, chunk.getPayload());

        // TODO: isLastFragment 매개변수 수정,
        //  getFragmentKey 메서드 작성,
        //  mergeFragments 매개변수 수정
        if(isLastFragment(null)) {
            Byte[] mergedFragment = mergeFragments(null);
            chunk.setPayload(new Payload(mergedFragment));
            upperLayer.receive(chunk);
        }
    }

    @Override
    public void send(Chunk<Header> chunk) {
        Byte[] payload = chunk.getPayload().getBytes();
        int payloadLength = payload.length;
        final int HEADER_SIZE = 20;
        final int MTU = 1500;
        final int FRAGMENT_SIZE = MTU - HEADER_SIZE;
        int totalFragments = (int) Math.ceil((double) payloadLength / FRAGMENT_SIZE);
        int identification = (int) (Math.random() * 65535);
        for (int i = 0; i < totalFragments; i++) {
            int offset = i * FRAGMENT_SIZE;
            boolean isLast = (i == totalFragments - 1);
            // 새로운 헤더 생성 (payload는 그대로)
            PacketHeader header = new PacketHeader();
            header.setVersion(new Byte[]{(byte) 4});
            header.setIHL(new Byte[]{(byte) 5});
            int fragmentPayloadSize = Math.min(FRAGMENT_SIZE, payloadLength - offset);
            int totalLength = HEADER_SIZE + fragmentPayloadSize;
            header.setTotal_Length(new Byte[]{
                    (byte) ((totalLength >> 8) & 0xFF),
                    (byte) (totalLength & 0xFF)
            });
            header.setIdentification(new Byte[]{
                    (byte) ((identification >> 8) & 0xFF),
                    (byte) (identification & 0xFF)
            });
            int offsetValue = offset / 8;
            int flags = isLast ? 0b000 : 0b001;
            int flagOffset = (flags << 13) | offsetValue;
            header.setFlags(new Byte[]{(byte) ((flagOffset >> 8) & 0xE0)});
            header.setFragment_Offset(new Byte[]{
                    (byte) ((flagOffset >> 8) & 0x1F),
                    (byte) (flagOffset & 0xFF)
            });
            header.setTTL(new Byte[]{(byte) 64});
            header.setProtocol(new Byte[]{(byte) 6});
            header.setHeader_Checksum(new Byte[]{0x00, 0x00});
            header.setSource_Address(new Byte[]{127, 0, 0, 1});
            header.setDestination_Address(new Byte[]{127, 0, 0, 1});
            // chunk 내부 갱신 (payload는 동일)
            chunk.setHeader(header);
            lowerLayer.send(chunk); // 같은 payload, 다른 header로 여러 번 전송
        }
    }

    /**
     * Ethernet Layer에서 받은 청크의 페이로드에서 IP 헤더 정보를 파싱 <br>
     * 새로운 PacketHeader에 세팅한 뒤, 해당 헤더를 청크에 주입. <br>
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

    // FIXME: 아이피 확인 메서드 구현
    private boolean isLocalIPAddress(Byte[] ipAddress) {
        try {
            byte[] localIp = InetAddress.getLocalHost().getAddress();
            byte[] ipAddressBytes = new byte[ipAddress.length];
            for(int i = 0; i < ipAddress.length; i++)
                ipAddressBytes[i] = ipAddress[i];
            return Arrays.equals(localIp, ipAddressBytes);
        } catch (UnknownHostException e) {
            return false;
        }
    }

//    tcp, udp 확인용 / 추후 udp 확장 가능성 고려
//    private Protocol getProtocol(Byte[] protocol) {}

    // TODO: 마지막 프래그먼트 확인 메서드 구현
    private boolean isLastFragment(Byte[] MF) {
        // More Fragments
        return true;
    }

    /**
     * fragment를 fragmentBuffer에 저장하는 메서드 <br>
     * 기존에 fragmentKey가 있는지 확인 후 없으면 생성 <br>
     * InternetLayer의 Header로 fragmentKey를 생성한다. <br>
     * @param packetHeader InternetLayer 헤더
     * @param payload InternetLayer 페이로드
     */
    private void storeFragmentToFragmentBuffer(PacketHeader packetHeader, Payload payload) {
        // 들어온 패킷 정보로 key 생성
        FragmentKey fragmentKey = new FragmentKey(
                packetHeader.getSource_Address(),
                packetHeader.getDestination_Address(),
                packetHeader.getProtocol(),
                packetHeader.getIdentification());

        if(fragmentBuffer.containsKey(fragmentKey)) {
            // 기존에 key가 있으면 기존 key에 <offset, payload> 저장
            fragmentBuffer.get(fragmentKey).put(byteArrayToInt(packetHeader.getFragment_Offset()), payload.getBytes());
        } else {
            // 새로운 key 생성 후 <offset, payload> 저장
            ConcurrentHashMap<Integer, Byte[]> newMap = new ConcurrentHashMap<>();
            newMap.put(byteArrayToInt(packetHeader.getFragment_Offset()), payload.getBytes());
            fragmentBuffer.put(fragmentKey, newMap);
        }

    }



    /**
     * 해당 패킷 키와 같은 종류의 패킷들을 합침
     * @param fragmentKey 패킷 식별키
     * @return 식별된 패킷들을 합친 패킷(세그먼트)
     * @author SeungminShin97
     */
    private Byte[] mergeFragments(FragmentKey fragmentKey) {
        Map<Integer, Byte[]> fragmentMap = fragmentBuffer.get(fragmentKey);
        ArrayList<Byte> fragmentList = new ArrayList<>(fragmentMap.size());

        for(int i = 0; i < fragmentMap.size(); ++i)
            fragmentList.addAll(Arrays.asList(fragmentMap.get(i)));

        return fragmentList.toArray(new Byte[0]);
    }

    private int byteArrayToInt(Byte[] byteArray) {
        return Byte.toUnsignedInt(byteArray[0]);
    }
}
