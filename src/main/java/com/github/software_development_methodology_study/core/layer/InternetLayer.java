package com.github.software_development_methodology_study.core.layer;

import com.github.software_development_methodology_study.core.data.chunk.Chunk;
import com.github.software_development_methodology_study.core.data.chunk.header.Header;
import com.github.software_development_methodology_study.core.data.chunk.header.PacketHeader;
import com.github.software_development_methodology_study.core.data.chunk.payload.Payload;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.IntStream;

public class InternetLayer extends Layer<PacketHeader> {

    /**
     * 패킷 구분용 클래스 <br>
     * 패킷은 srcIp, dstIp, protocol, identification 4가지 필드로 구분한다. <br>
     * 필드들은 Byte[] 로 저장 <br>
     * fragmentBuffer에 [FragmentKey, [Offset, Payload]] 형태로 저장
     */
    private record FragmentKey(
            Byte[] srcIp,
            Byte[] dstIp,
            Byte[] protocol,
            Byte[] identification
    ) {
        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
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
    private Byte[] extractChunkAndSetNewHeader(Chunk<Header> chunk, PacketHeader packetHeader) {
        // Ethernet Layer에서 받은 페이로드 (Byte[] 타입)
        Byte[] lowerPayload = chunk.getPayload().getBytes();

        packetHeader = new PacketHeader();
        int versionAndIHL = Byte.toUnsignedInt(lowerPayload[0]);
        int ihl = versionAndIHL & 0x0F;
        int version = (versionAndIHL >> 4) & 0x0F;

        // 예외처리
        if (ihl < 5) {
            throw new IllegalArgumentException("Invalid IHL value: " + ihl);
        }

        // 기본 헤더 필드 파싱 (ihl * 4 바이트 헤더)
        int totalLength = (Byte.toUnsignedInt(lowerPayload[2]) << 8) | Byte.toUnsignedInt(lowerPayload[3]);
        int identification = (Byte.toUnsignedInt(lowerPayload[4]) << 8) | Byte.toUnsignedInt(lowerPayload[5]);

        int flagsAndOffset = (Byte.toUnsignedInt(lowerPayload[6]) << 8) | Byte.toUnsignedInt(lowerPayload[7]);
        int flags = (flagsAndOffset >> 13) & 0x07;
        int fragmentOffset = flagsAndOffset & 0x1FFF;

        int ttl = Byte.toUnsignedInt(lowerPayload[8]);
        int protocol = Byte.toUnsignedInt(lowerPayload[9]);

        int srcIpAddress =
                (Byte.toUnsignedInt(lowerPayload[12]) << 24) |
                        (Byte.toUnsignedInt(lowerPayload[13]) << 16) |
                        (Byte.toUnsignedInt(lowerPayload[14]) << 8) |
                        Byte.toUnsignedInt(lowerPayload[15]);

        int dstIpAddress =
                (Byte.toUnsignedInt(lowerPayload[16]) << 24) |
                        (Byte.toUnsignedInt(lowerPayload[17]) << 16) |
                        (Byte.toUnsignedInt(lowerPayload[18]) << 8) |
                        Byte.toUnsignedInt(lowerPayload[19]);

        // packetHeader 필드 세팅 부분
        packetHeader.setVersion(new Byte[]{(byte) version});
        packetHeader.setIHL(new Byte[]{(byte) ihl});
        packetHeader.setTotal_Length(new Byte[]{(byte)(totalLength >> 8), (byte)(totalLength & 0xFF)});
        packetHeader.setIdentification(new Byte[]{(byte)(identification >> 8), (byte)(identification & 0xFF)});
        packetHeader.setFlags(new Byte[]{(byte) flags});
        packetHeader.setFragment_Offset(new Byte[]{(byte)(fragmentOffset >> 8), (byte)(fragmentOffset & 0xFF)});
        packetHeader.setTTL(new Byte[]{(byte) ttl});
        packetHeader.setProtocol(new Byte[]{(byte) protocol});
        packetHeader.setSource_Address(new Byte[]{
                (byte)(srcIpAddress >> 24), (byte)((srcIpAddress >> 16) & 0xFF),
                (byte)((srcIpAddress >> 8) & 0xFF), (byte)(srcIpAddress & 0xFF)});
        packetHeader.setDestination_Address(new Byte[]{
                (byte)(dstIpAddress >> 24), (byte)((dstIpAddress >> 16) & 0xFF),
                (byte)((dstIpAddress >> 8) & 0xFF), (byte)(dstIpAddress & 0xFF)});

        Byte[] ipHeaderBytes;

        if (ihl == 5) {
            // 옵션 없는 경우: 기본 헤더 20바이트만 추출
            ipHeaderBytes = Arrays.copyOf(lowerPayload, 20);
            packetHeader.setOptions(new Byte[0]);
            packetHeader.setPadding(new Byte[0]);
        } else {
            // 옵션 + 패딩 처리 경우
            int ipHeaderLength = ihl * 4;

            // 옵션+패딩 부분만 lowerPayload에서 추출 (20 ~ ipHeaderLength 까지)
            Byte[] optionsAndPadding = Arrays.copyOfRange(lowerPayload, 20, ipHeaderLength);

            // 옵션 파싱 부분 (EOL 나오면 break)
            int offset = 0;
            int totalOptionLength = 0;
            while (offset < optionsAndPadding.length) {
                int type = Byte.toUnsignedInt(optionsAndPadding[offset]);
                if (type == 0) { // EOL
                    totalOptionLength = offset + 1;
                    break;
                } else if (type == 1) { // NOP
                    offset += 1;
                    totalOptionLength += 1;
                } else {
                    if (offset + 1 >= optionsAndPadding.length) {
                        // 옵션 길이 못 읽으면 중단
                        break;
                    }
                    int length = Byte.toUnsignedInt(optionsAndPadding[offset + 1]);
                    if (length < 2) {
                        throw new IllegalArgumentException("Invalid option length: " + length);
                    }
                    offset += length;
                    totalOptionLength += length;
                }
            }

            // 패딩 계산 부분
            int paddingLength = (4 - (totalOptionLength % 4)) % 4;
            int fullOptionLength = totalOptionLength + paddingLength;

            // 옵션+패딩 전체 배열 부분
            Byte[] fullOptionsAndPadding = new Byte[fullOptionLength];
            // 옵션 데이터 복사
            System.arraycopy(optionsAndPadding, 0, fullOptionsAndPadding, 0, totalOptionLength);
            // 패딩은 기본 null

            packetHeader.setOptions(Arrays.copyOf(fullOptionsAndPadding, totalOptionLength));
            packetHeader.setPadding(Arrays.copyOfRange(fullOptionsAndPadding, totalOptionLength, fullOptionLength));

            // IP 헤더 전체 추출 (ihl*4 바이트)
            ipHeaderBytes = Arrays.copyOf(lowerPayload, ipHeaderLength);
        }

        // packetHeader 주입
        chunk.setHeader(packetHeader);

        // IP 헤더 전체 바이트 배열 반환
        return ipHeaderBytes;
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
