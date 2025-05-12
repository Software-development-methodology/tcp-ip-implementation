package mockLayer;

import chunk.Chunk;
import header.FrameHeader;
import header.Header;

public class MockLayer {
    private MockLayer upperLayer;
    private MockLayer lowerLayer;

    public void send(Chunk chunk) {
        chunk.mergeHeader();
        Header header = new FrameHeader();
        chunk.setHeader(header);
        /*
        헤더 세팅
         */
        lowerLayer.send(chunk);
    }

    public void receive(Chunk chunk) {
        Header header = new FrameHeader();
        chunk.setHeader(header);
        chunk.separateHeader();
        /*
        분리 된 헤더 처리
         */
        upperLayer.receive(chunk);
    }
}
