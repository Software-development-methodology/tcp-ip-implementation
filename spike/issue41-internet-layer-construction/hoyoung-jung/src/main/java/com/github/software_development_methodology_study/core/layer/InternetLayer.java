package com.github.software_development_methodology_study.core.layer;

import com.github.software_development_methodology_study.core.data.chunk.Chunk;
import com.github.software_development_methodology_study.core.data.chunk.header.Header;
import com.github.software_development_methodology_study.core.data.chunk.header.PacketHeader;
import com.github.software_development_methodology_study.core.data.chunk.payload.Payload;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class InternetLayer extends Layer<PacketHeader> {

    /**
     * 패킷 식별용 클래스 <br>
     * 패킷은 srcIp, dstIp, protocol, identification으로 식별한다.
     * @author SeungminShin97
     */
    private  class FragmentKey {
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
    public final ConcurrentHashMap<FragmentKey, ConcurrentHashMap<Integer, Byte[]>> fragmentBuffer = new ConcurrentHashMap<>();


    @Override
    public void receive(Chunk<Header> chunk) {
        if(chunk.getPayload() == null)
            throw new NullPointerException("Chunk is null");

        PacketHeader packetHeader = new PacketHeader();
//        chunk.setHeader(packetHeader);
        PacketHeader header = (PacketHeader) chunk.getHeader();
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
        
        FragmentKey key = getFragmentKey(header);
        storeFragment(key, header, chunk.getPayload());

        if (isLastFragment(header.getFlags(), header.getFragment_Offset())) {
            Byte[] merged = mergeFragments(key, fragmentBuffer.get(key).size());
            chunk.setPayload(new Payload(merged));
            fragmentBuffer.remove(key);
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
     * Ethernet Layer에서 받은 청크의 페이로드에서 IP 헤더 정보를 파싱하여
     * 새로운 PacketHeader에 세팅한 뒤, 해당 헤더를 청크에 주입.
     * 버전, 길이, 식별자, 플래그, IP 주소 등 주요 필드를 바이트 배열로 변환해 설정.
     * @author judy78799
     */
    private void extractChunkAndSetNewHeader(Chunk<Header> chunk, PacketHeader packetHeader) {
        // IP 패킷 바이트 배열
        Byte[] lowerPayload = chunk.getPayload().getBytes();
        // 페이로드 에서 헤더 추출
//        if(lowerPayload.length < 20){
//            throw new IllegalArgumentException("여기서 부터 Options임.");
//        }
        packetHeader = new PacketHeader();
        // Version과 IHL (상위 4비트씩) 비트 마스크 사용
        int versionAndIHL = Byte.toUnsignedInt(lowerPayload[0]);
        int ihl = versionAndIHL & 0x0F; //헤더의 8비트에서 하위 4비트만 추출
        int version = (versionAndIHL >> 4) & 0x0F;

        if(ihl < 5) {
            // 비정상 패킷 처리 (IHL 값 오류)
            throw new IllegalArgumentException("Invalid IHL value: " + ihl);
        } else if (ihl == 5) {
            // 기본 헤더만 있음, 옵션과 패딩 없음
            int totalLength = (Byte.toUnsignedInt(lowerPayload[2]) << 8) | Byte.toUnsignedInt(lowerPayload[3]);
            int identification = (Byte.toUnsignedInt(lowerPayload[4]) << 8) | Byte.toUnsignedInt(lowerPayload[5]);

            // Flags (3비트) + Fragment Offset (13비트)
            int flagsAndOffset = (Byte.toUnsignedInt(lowerPayload[6]) << 8) | Byte.toUnsignedInt(lowerPayload[7]);
            int flags = (flagsAndOffset >> 13) & 0x07;
            int fragmentOffset = flagsAndOffset & 0x1FFF;

            int ttl = Byte.toUnsignedInt(lowerPayload[8]);
            int protocol = Byte.toUnsignedInt(lowerPayload[9]);

            // 출발지 주소 Source IP (12~15)
            int srcIpAdress =
                    (Byte.toUnsignedInt(lowerPayload[12]) << 24) |
                            (Byte.toUnsignedInt(lowerPayload[13]) << 16) |
                            (Byte.toUnsignedInt(lowerPayload[14]) << 8) |
                            Byte.toUnsignedInt(lowerPayload[15]);

            // 목적지 주소 Destination IP (16~19)
            int dstIpAdress =
                    (Byte.toUnsignedInt(lowerPayload[16]) << 24) |
                            (Byte.toUnsignedInt(lowerPayload[17]) << 16) |
                            (Byte.toUnsignedInt(lowerPayload[18]) << 8) |
                            Byte.toUnsignedInt(lowerPayload[19]);

            // PacketHeader에 각 헤더 속성값 주입
            packetHeader.setVersion(new Byte[]{(byte)version}); //int -> Byte[]
            packetHeader.setIHL(new Byte[]{(byte)ihl});
            packetHeader.setTotal_Length(new Byte[]{(byte)totalLength});
            packetHeader.setIdentification(new Byte[]{(byte)identification});
            packetHeader.setFlags(new Byte[]{(byte)flags});
            packetHeader.setFragment_Offset(new Byte[]{(byte)fragmentOffset});
            packetHeader.setTTL(new Byte[]{(byte)ttl});
            packetHeader.setProtocol(new Byte[]{(byte)protocol});
            packetHeader.setSource_Address(new Byte[]{(byte)srcIpAdress});
            packetHeader.setDestination_Address(new Byte[]{(byte)dstIpAdress});

        } else {
            // ihl > 5 이면 옵션 처리(옵션 내부에 패딩)
            int ipHeaderLength = ihl * 4; //20
            int optionsLength = ipHeaderLength - 20; // 페이로드에서 뺀 나머지 값
            Byte[] OptionsAndPadding = Arrays.copyOf(lowerPayload, ipHeaderLength);   //옵션+패딩이 포함된 IP 헤더 부분만 자름
            // 하나의 옵션 Option Type(1 byte) + Option Length(1 byte) + Option Data(가변)


            int offset = 0; //옵션을 처음부터 읽기 위함

            byte optionType = OptionsAndPadding[offset];    //일단 옵션 하나 읽어옴.
            //option 처리
            while (offset < OptionsAndPadding.length) { //offset++ -> offset >= OptionsAndPadding.length -> 종료
                int type = Byte.toUnsignedInt(OptionsAndPadding[offset]);  //첫 번째 type
                int length = Byte.toUnsignedInt(OptionsAndPadding[offset + 1]); //첫번째 Option의 길이

                if (type == 0 || length < 2) { // EOL 옵션 영역 전체 순회 완료
                    break;
                } else if (type == 1) { // NOP
                    offset += 1;
                } else if (offset + 1 >= OptionsAndPadding.length) { // NOP
                    break;
                } else {
                    // 옵션 데이터는 [offset + 2]부터 [offset + length - 1]까지
                    offset += length; //다음 옵션 읽어오기 옵션을 나누는 기준
                }
            }
        }
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

    // 마지막 프래그먼트 확인
    private boolean isLastFragment(Byte[] flagsBytes, Byte[] offsetBytes) {
        if (flagsBytes == null || offsetBytes == null
                || flagsBytes.length < 1 || offsetBytes.length < 2)
            throw new IllegalArgumentException("Flags 또는 Offset 필드가 유효하지 않습니다."); // 필드가 유효하지 않으면 예외 처리

        int flags = Byte.toUnsignedInt(flagsBytes[0]) >> 5; // Flags 필드의 상위 3비트 추출 (Reserved, DF, MF)
        int mfBit = flags & 0b1; // MF가 0이면 마지막 조각

        return mfBit == 0;
    }

    //조각 병합
    private Byte[] mergeFragments(FragmentKey fragmentKey, int fragmentCnt) {
        Map<Integer, Byte[]> fragmentMap = fragmentBuffer.get(fragmentKey); // 해당 조각 키로 저장된 조각 맵 가져오기

        if (fragmentMap == null || fragmentMap.isEmpty())
            throw new IllegalStateException("병합할 조각이 없습니다."); // 조각이 없으면 병합할 수 없음

        // offset 기준 정렬
        ArrayList<Integer> offsets = new ArrayList<>(fragmentMap.keySet()); // offset 리스트 수집
            Collections.sort(offsets); // offset 순으로 정렬

        ArrayList<Byte> merged = new ArrayList<>(); // 병합된 데이터를 담을 리스트

        for (Integer offset : offsets) {
            Byte[] piece = fragmentMap.get(offset); // 각 offset에 해당하는 조각 가져오기
            if (piece != null)
                merged.addAll(Arrays.asList(piece)); // 조각을 병합 리스트에 추가
        }

        return merged.toArray(new Byte[0]); // 최종 Byte[] 배열로 변환 후 반환
    }

    // 조각 구분 키 생성
    private FragmentKey getFragmentKey(PacketHeader header) {
        String srcIp = ipBytesToString(header.getSource_Address()); // 출발지 IP 문자열로 변환
        String dstIp = ipBytesToString(header.getDestination_Address()); // 목적지 IP 문자열로 변환

        int protocol = Byte.toUnsignedInt(header.getProtocol()[0]); // 프로토콜 값 (1바이트 → int)

        // Identification: 상위 1바이트 << 8 + 하위 1바이트 = 16비트 정수
        int identification = (Byte.toUnsignedInt(header.getIdentification()[0]) << 8)
                | Byte.toUnsignedInt(header.getIdentification()[1]);

        return new FragmentKey(srcIp, dstIp, protocol, identification); // FragmentKey 생성 후 반환
    }


    //조각 저장
    private void storeFragment(FragmentKey key, PacketHeader header, Payload payload) {
        int rawOffset = (Byte.toUnsignedInt(header.getFragment_Offset()[0]) << 8) // Fragment Offset: 2바이트 → 16비트 정수로 변환
                | Byte.toUnsignedInt(header.getFragment_Offset()[1]);
        int offset = rawOffset & 0x1FFF;  //상위 3비트 flags 제거

        System.out.println(">>> Storing offset: " + offset + " → " + Arrays.toString(payload.getBytes()));

        fragmentBuffer.putIfAbsent(key, new ConcurrentHashMap<>()); // 키가 없으면 조각 저장용 맵 초기화
        fragmentBuffer.get(key).put(offset, payload.getBytes()); // offset 위치에 조각 바이트 저장
    }

    //Byte[] → 문자열 IP 주소
    private String ipBytesToString(Byte[] ip) {
        if (ip == null || ip.length != 4) return "0.0.0.0"; // IP가 유효하지 않으면 기본값 반환

        // 각 바이트를 부호 없는 int로 변환하여 "."으로 연결된 문자열 반환
        return String.format("%d.%d.%d.%d",
                Byte.toUnsignedInt(ip[0]),
                Byte.toUnsignedInt(ip[1]),
                Byte.toUnsignedInt(ip[2]),
                Byte.toUnsignedInt(ip[3]));
    }

}
