import chunk.Chunk;
import header.FrameHeader;
import header.Header;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

public class ChunkTest {
    private Byte[] header;
    private Byte[] frame;
    private Byte[] payload;
    private Header frameHeader;

    @BeforeEach
    void createHeader() {
        header = Creator.createEthernetHeader();
        frame = Creator.createFullEthernetFrame();
        payload = Creator.createEthernetPayload();
        frameHeader = new FrameHeader();
    }

    @Test
    void chunkHeaderSeparateTest() {
        Chunk chunk = new Chunk(frame);
        chunk.setHeader(frameHeader);
        chunk.separateHeader();
        Header header1 = chunk.getHeader();

        Assertions.assertArrayEquals(header, header1.getHeaderByteArray());
    }

    @Test
    void chunkHeaderMergeTest() {
        Chunk chunk = new Chunk(payload);
        Header header1 = new FrameHeader(header);
        chunk.setHeader(header1);

        chunk.mergeHeader();
        Assertions.assertArrayEquals(chunk.getPayload(), frame);
    }

}
