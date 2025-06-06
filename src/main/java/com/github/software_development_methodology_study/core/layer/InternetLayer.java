package com.github.software_development_methodology_study.core.layer;

import com.github.software_development_methodology_study.core.data.chunk.Chunk;
import com.github.software_development_methodology_study.core.data.chunk.header.Header;
import com.github.software_development_methodology_study.core.data.chunk.header.PacketHeader;
import com.github.software_development_methodology_study.core.data.chunk.payload.Payload;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

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
            throw new IllegalArgumentException("Payload가 비어있습니다.");

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

        FragmentKey key = getFragmentKey(packetHeader);
        storeFragment(key, packetHeader, chunk.getPayload());

        if(isLastFragment(packetHeader.getFlags())) {
            Byte[] mergedFragment = mergeFragments(key);
            chunk.setPayload(new Payload(mergedFragment));
            fragmentBuffer.remove(key);
            upperLayer.receive(chunk);
        }
    }

    //TODO: send():refactoring 이후 진행할 것.
    @Override
   public void send(Chunk<Header> chunk) {
    //        PacketHeader ipHeader = new PacketHeader();
    //        // 헤더 설정
    //        chunk.setHeader(ipHeader);
    //        lowerLayer.send(chunk);s
  }

    /** extractChunkAndSetNewHeader() 리팩토링 진행
     * processBasicHeader: 기존 헤더만 받아오는 메서드
     * processHeaderWithOptions: 헤더와 옵션을 둘 다 받아오는 메서드
     * getIpFromPayload: IP 헤더 파싱 전용 유틸리티 메서드
     */
    private Byte[] extractChunkAndSetNewHeader(Chunk<Header> chunk, PacketHeader packetHeader) {
        Byte[] lowerPayload = chunk.getPayload().getBytes();
        int ihl = Byte.toUnsignedInt(lowerPayload[0]) & 0x0F;

        Byte[] ipHeaderBytes;
        int ipHeaderLength = ihl * 4;

        // ihl < 5 경우, 예외처리
        if (ihl < 5) {
            throw new IllegalArgumentException("Invalid IHL value: " + ihl);
        }
        if (ihl == 5) {
            // 옵션 없는 경우: 기본 헤더 20바이트만 추출
            processBasicHeader(lowerPayload, packetHeader, chunk);
        } else {
            // 옵션 + 패딩 있는 경우의 헤더 처리
            processHeaderWithOptions(lowerPayload, ihl, packetHeader, chunk);
        }
        ipHeaderBytes = Arrays.copyOf(lowerPayload, ipHeaderLength); //주입 받은 chunk
        System.out.println("ipHeaderBytes : " + Arrays.toString(ipHeaderBytes));
        return ipHeaderBytes;
    }

    /**메서드 역할 분리
     * processBasicHeader
     * 기존 헤더만 받아오는 메서드
     */
    private void processBasicHeader(Byte[] lowerPayload, PacketHeader packetHeader, Chunk<Header> chunk) {
        int versionAndIHL = Byte.toUnsignedInt(lowerPayload[0]);
        int ihl = versionAndIHL & 0x0F;
        int version = (versionAndIHL >> 4) & 0x0F;
        int totalLength = (Byte.toUnsignedInt(lowerPayload[2]) << 8) | Byte.toUnsignedInt(lowerPayload[3]);
        int identification = (Byte.toUnsignedInt(lowerPayload[4]) << 8) | Byte.toUnsignedInt(lowerPayload[5]);
        int flagsAndOffset = (Byte.toUnsignedInt(lowerPayload[6]) << 8) | Byte.toUnsignedInt(lowerPayload[7]);
        int flags = (flagsAndOffset >> 13) & 0x07;
        int fragmentOffset = flagsAndOffset & 0x1FFF;
        int ttl = Byte.toUnsignedInt(lowerPayload[8]);
        int protocol = Byte.toUnsignedInt(lowerPayload[9]);
        // Source + Destination IP 주소
        int srcIpAdress = getIpFromPayload(lowerPayload, 12);
        int dstIpAdress = getIpFromPayload(lowerPayload, 16);

        // packetHeader에 주입
        packetHeader.setVersion(new Byte[]{(byte) version});
        packetHeader.setIHL(new Byte[]{(byte) ihl});
        packetHeader.setTotal_Length(new Byte[]{(byte) totalLength});
        packetHeader.setIdentification(new Byte[]{(byte) identification});
        packetHeader.setFlags(new Byte[]{(byte) flags});
        packetHeader.setFragment_Offset(new Byte[]{(byte) fragmentOffset});
        packetHeader.setTTL(new Byte[]{(byte) ttl});
        packetHeader.setProtocol(new Byte[]{(byte) protocol});
        packetHeader.setSource_Address(new Byte[]{(byte) srcIpAdress});
        packetHeader.setDestination_Address(new Byte[]{(byte) dstIpAdress});

        chunk.setHeader(packetHeader);
    }
    /**
     * processHeaderWithOptions
     * 헤더와 옵션을 둘 다 받아오는 메서드
     * 네트워크에서 받은 패킷 데이터 byte[]로 주고받음.
     * Byte[] 경우 메모리 낭비 + 불필요한 오토박싱이 발생합니다.
     */
    private void processHeaderWithOptions(Byte[] lowerPayload, int ihl, PacketHeader packetHeader, Chunk<Header> chunk) {
        processBasicHeader(lowerPayload, packetHeader,chunk); // 먼저 공통 헤더 처리 (packetHeader에 주입)

        int ipHeaderLength = ihl * 4;
        int optionLength = ipHeaderLength - 20;
        Byte[] optionsAndPadding = Arrays.copyOfRange(lowerPayload, 20, ipHeaderLength);

        /**
         * 각 옵션의 구조  Type | Length | Data
         * Type : 0 : End of Option List (옵션 끝)
         * Type : 1 : No Operation (1바이트짜리 옵션, 주로 정렬용 패딩)
         */
        int offset = 0;
        int totalOptionLength = 0;

        while (offset < optionsAndPadding.length) {
            int type = Byte.toUnsignedInt(optionsAndPadding[offset]);

            //옵션 처리 부분
            if (type == 0) { //옵션 끝
                break;
            } else if (type == 1) { //정렬 용 패딩
                offset += 1;
                totalOptionLength += 1;
            } else {
                if (offset + 1 >= optionsAndPadding.length) {
                    throw new IllegalArgumentException("Invalid option structure: truncated option");
                }
                int length = Byte.toUnsignedInt(optionsAndPadding[offset + 1]);

                if (length < 2 || offset + length > optionsAndPadding.length) {
                    throw new IllegalArgumentException("Invalid option length: " + length);
                }
                offset += length;
                totalOptionLength += length;
            }
        }
        //패딩 길이 계산 부분
        int paddingLength = (4 - (totalOptionLength % 4)) % 4;

        //옵션+ 패딩 추가 주입 부분
        packetHeader.setOptions(new Byte[]{(byte) totalOptionLength});
        packetHeader.setPadding(new Byte[]{(byte) paddingLength});
    }

    /**
     * getIpFromPayload
     * IP 헤더 파싱 전용 유틸리티 메서드
     * Source + Destination IP 주소에 사용
     */
    private int getIpFromPayload(Byte[] payload, int startIndex) {
        return (Byte.toUnsignedInt(payload[startIndex]) << 24) |
                (Byte.toUnsignedInt(payload[startIndex + 1]) << 16) |
                (Byte.toUnsignedInt(payload[startIndex + 2]) << 8) |
                Byte.toUnsignedInt(payload[startIndex + 3]);
    }

    // TODO: 아이피 확인 메서드 구현
    private boolean isLocalIPAddress(Byte[] ipAddress) {
        // 아이피 확인
        return true;
    }

    private boolean isLastFragment(Byte[] flags) {
        return (flags[0] & 0x1) == 0;
    }

    /**
     * fragment를 fragmentBuffer에 저장하는 메서드 <br>
     * 기존에 fragmentKey가 있는지 확인 후 없으면 생성 <br>
     * InternetLayer의 Header로 fragmentKey를 생성한다. <br>
     * @param key key
     * @param header 패킷의 Header
     * @param payload InternetLayer 페이로드
     */
    private void storeFragment(FragmentKey key, PacketHeader header, Payload payload) {
        int rawOffset = (Byte.toUnsignedInt(header.getFragment_Offset()[0]) << 8)
                | Byte.toUnsignedInt(header.getFragment_Offset()[1]);
        int offset = rawOffset & 0x1FFF;

        fragmentBuffer.putIfAbsent(key, new ConcurrentHashMap<>());
        fragmentBuffer.get(key).put(offset, payload.getBytes());
    }

    /**
     * 패킷을 구별하기 위한 key <br>
     * srcIp, dstIp, protocol, identification 4가지를 가지고 key를 만듬
     * @param packetHeader 현재 레이어의 Header
     * @return key
     * @author Seungmin-shin
     */
    private FragmentKey getFragmentKey(PacketHeader packetHeader) {
        return new FragmentKey(
                packetHeader.getSource_Address(),
                packetHeader.getDestination_Address(),
                packetHeader.getProtocol(),
                packetHeader.getIdentification());
    }

    /**
     * 프래그먼트 합치는 메서드
     * @param key 패킷 식별키
     * @return 식별된 패킷들을 합친 패킷(세그먼트)
     * @author Hoyeong-jeong
     */
    private Byte[] mergeFragments(FragmentKey key) {
        Map<Integer, Byte[]> fragmentMap = fragmentBuffer.get(key);
        if (fragmentMap == null || fragmentMap.isEmpty())
            throw new IllegalStateException("병합할 조각이 없습니다.");

        List<Integer> offsets = new ArrayList<>(fragmentMap.keySet());
        Collections.sort(offsets);

        List<Byte> merged = new ArrayList<>();
        for (int offset : offsets) {
            Byte[] piece = fragmentMap.get(offset);
            if (piece != null) merged.addAll(Arrays.asList(piece));
        }

        return merged.toArray(new Byte[0]);
    }
}


