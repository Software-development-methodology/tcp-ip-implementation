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
    private void extractChunkAndSetNewHeader(Chunk<Header> chunk, PacketHeader packetHeader) {
        // IP 패킷 바이트 배열
        Byte[] lowerPayload = chunk.getPayload().getBytes();

        packetHeader = new PacketHeader();
        int versionAndIHL = Byte.toUnsignedInt(lowerPayload[0]);    // Version과 IHL (상위 4비트씩) 비트 마스크 사용
        //TODO: ihl은 4바이트가 되어야 함.
        int ihl = versionAndIHL & 0x0F; //헤더의 8비트에서 하위 4비트만 추출
        int version = (versionAndIHL >> 4) & 0x0F;
    /*
    (if 를 2번만 쓰자~)
    if 예외처리
    헤더만 세팅 -> 5이상으로 들어올 경우
    if(ihl > 5) 옵션세팅
     */
        if (ihl < 5) {
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
            packetHeader.setVersion(new Byte[]{(byte) version}); //int -> Byte[]
            packetHeader.setIHL(new Byte[]{(byte) ihl});
            packetHeader.setTotal_Length(new Byte[]{(byte) totalLength});
            packetHeader.setIdentification(new Byte[]{(byte) identification});
            packetHeader.setFlags(new Byte[]{(byte) flags});
            packetHeader.setFragment_Offset(new Byte[]{(byte) fragmentOffset});
            packetHeader.setTTL(new Byte[]{(byte) ttl});
            packetHeader.setProtocol(new Byte[]{(byte) protocol});
            packetHeader.setSource_Address(new Byte[]{(byte) srcIpAdress});
            packetHeader.setDestination_Address(new Byte[]{(byte) dstIpAdress});
        }
        else if (ihl > 5 && ihl <= 15) {//옵션 + 패딩 처리
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

            //옵션 작업
            // ihl > 5 이면 옵션 처리(옵션 내부에 패딩)
            int ipHeaderLength = ihl * 4; //20
            int optionsAndPaddingLength = lowerPayload.length - ipHeaderLength;  //페이로드 전체에서 iPHeader를 뺀 나머지
            Byte[] OptionsAndPadding = Arrays.copyOf(lowerPayload, optionsAndPaddingLength);   //옵션+패딩이 포함된 IP 헤더 부분만 자름
            // 하나의 옵션 Option Type(1 byte) + Option Length(1 byte) + Option Data(가변)
            //바이트랑 int 계산 때문에 혼란옴..

            int offset = 0; //옵션을 처음부터 읽는 index
            int totalOptionLength = 0;
            //옵션 타입을 체크 -> 옵션 길이를 체크 하는 식으로 반복문으로 option만의 길이를 읽어옴.
            byte optionType = OptionsAndPadding[offset];    //일단 옵션 하나 읽어옴.
            //option 전체를 읽어오는 반복문
            while (offset < OptionsAndPadding.length) { //offset++ -> offset >= OptionsAndPadding.length -> 종료
                int type = Byte.toUnsignedInt(OptionsAndPadding[offset]);  //첫 번째 type
                int length = Byte.toUnsignedInt(OptionsAndPadding[offset + 1]); //첫번째 Option의 길이

                if (type == 0 || length < 2) { // EOL 옵션 영역 전체 순회 완료
                    throw new IllegalArgumentException("Invalid option type: " + type);
                    //빠져나왔을 경우 offset이 끝가지 가지 못함.
                } else if (type == 1) { // NOP
                    offset += 1;
                    totalOptionLength += 1;
                } else if (offset + 1 >= OptionsAndPadding.length) { // NOP
                    break;
                } else {
                    // 옵션 데이터는 [offset + 2]부터 [offset + length - 1]까지
                    offset += length; //다음 옵션 읽어오기 옵션을 나누는 기준
                    totalOptionLength += length;
                }
                //이거 나머지   = 4가되는 조건으로 + 1
                int Padding = (4-(totalOptionLength %  4)) % 4;
                System.out.println("offset 길이 : " + optionType);
                System.out.println("Option 길이 : " + totalOptionLength);
                System.out.println("Padding 길이: " + Padding);
            }
            packetHeader.setVersion(new Byte[]{(byte) version}); //int -> Byte[]
            packetHeader.setIHL(new Byte[]{(byte) ihl});
            packetHeader.setTotal_Length(new Byte[]{(byte) totalLength});
            packetHeader.setIdentification(new Byte[]{(byte) identification});
            packetHeader.setFlags(new Byte[]{(byte) flags});
            packetHeader.setFragment_Offset(new Byte[]{(byte) fragmentOffset});
            packetHeader.setTTL(new Byte[]{(byte) ttl});
            packetHeader.setProtocol(new Byte[]{(byte) protocol});
            packetHeader.setSource_Address(new Byte[]{(byte) srcIpAdress});
            packetHeader.setDestination_Address(new Byte[]{(byte) dstIpAdress});
            packetHeader.setOptions(new Byte[]{(byte) totalOptionLength});
            packetHeader.setPadding(new Byte[]{(byte) totalOptionLength});
        }
        // Header에 필드 주입
        chunk.setHeader(packetHeader); //PacketHeader
        Byte[] bytes = new Byte[]{};
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
