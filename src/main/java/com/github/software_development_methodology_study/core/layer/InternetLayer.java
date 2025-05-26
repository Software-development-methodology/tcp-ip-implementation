package com.github.software_development_methodology_study.core.layer;

import com.github.software_development_methodology_study.core.data.chunk.Chunk;
import com.github.software_development_methodology_study.core.data.chunk.header.Header;
import com.github.software_development_methodology_study.core.data.chunk.header.PacketHeader;

import java.util.Arrays;

public class InternetLayer extends Layer<PacketHeader> {
    //String IP 주소 iv
    //static 변수로 패킷(Map으로 저장? - TCP 순서 번호를 key로)을 cv로 만드는거랑 그냥 iv로 만드는거랑 차이가 뭐지? 나중에 비교
    @Override
    public void receive(Chunk<Header> chunk) {
        PacketHeader ipHeader = new PacketHeader();

        //가져온 이더넷 페이로드를 헤더를 헤더와 페이로드로 분리

        //목적지 주소를 확인해서 맞으면 보내고 아니면 캔슬
       chunk.setHeader(ipHeader);

       chunk = extractChunk(chunk);

       upperLayer.receive(chunk);

    }

    @Override
    public void send(Chunk<Header> chunk) {
        PacketHeader ipHeader = new PacketHeader();
        //헤더 설정

        chunk.setHeader(ipHeader);

        lowerLayer.send(chunk);

    }

    private Chunk<Header> extractChunk(Chunk<Header> ethernetChunk) {
       Byte[] Data = ethernetChunk.getPayload().getBytes();
        //여기 분리 로직은 GPT 참고
       // Byte[] headerData = Arrays.copyOfRange()



    }
}
