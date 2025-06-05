package com.github.software_development_methodology_study.core.layer;

import com.github.software_development_methodology_study.core.data.chunk.Chunk;
import com.github.software_development_methodology_study.core.data.chunk.header.EmptyHeader;
import com.github.software_development_methodology_study.core.data.chunk.header.Header;
import com.github.software_development_methodology_study.core.data.chunk.payload.Payload;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.pcap4j.core.PcapNetworkInterface;
import org.pcap4j.util.LinkLayerAddress;

import java.util.ArrayList;
import java.util.List;

import static com.github.software_development_methodology_study.common.util.TypeConverter.toPrimitive;
import static java.util.concurrent.ThreadLocalRandom.current;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class EthernetLayerTest {
    private EthernetLayer ethernetLayer;
    private PcapNetworkInterface nic;
    private Layer<EmptyHeader> mockUpperLayer;
    private Layer<EmptyHeader> mockLowerLayer;
    private LinkLayerAddress mockAddress;

    @BeforeEach
    void setUp() {
        ethernetLayer = spy(new EthernetLayer());

        mockUpperLayer = mock(Layer.class);
        mockLowerLayer = mock(Layer.class);
        ethernetLayer.setUpperLayer(mockUpperLayer);
        ethernetLayer.setLowerLayer(mockLowerLayer);

        nic = mock(PcapNetworkInterface.class);
        mockAddress = mock(LinkLayerAddress.class);
    }


    @Disabled
    void send_호출_시_Chunk의_Payload가_1500byte_보다_크면_false가_반환된다() {
        Byte[] bytes = new Byte[(int) (Math.random() * 100) + 1501];
        Chunk<Header> chunk = new Chunk<>();
        chunk.setPayload(new Payload(bytes));

        assertFalse(() -> ethernetLayer.send(chunk, nic));
    }

    @Test
    void receive_에서_헤더_제거_후_payload_크기가_46byte_미만이면_false가_반환된다() {
        Chunk<EmptyHeader> chunk = new Chunk<>();
        chunk.setHeader(new EmptyHeader());
        Byte[] bytes = new Byte[30];
        chunk.setPayload(new Payload(bytes));

        assertFalse(()-> ethernetLayer.receive(chunk, nic));
    }

    /**
     * Header 제거하는 로직 작성 후 테스트 활성화 할 것 `@can019`
     */
    @Disabled
    void receive_에서_헤더_제거_후_payload_크기가_정확히_46byte면_예외가_발생하지_않는다() {
        Chunk<EmptyHeader> chunk = new Chunk<>();
        chunk.setHeader(new EmptyHeader());
        Byte[] bytes = new Byte[46]; // 경계값
        chunk.setPayload(new Payload(bytes));

        assertDoesNotThrow(() -> ethernetLayer.receive(chunk, nic));
    }

    @Test
    void receive_에서_헤더_제거_후_payload_크기가_1500byte_초과하면_false가_반환된다() {
        Byte[] bytes = new Byte[1501];
        Byte[] randomDstMac = generateRandomMacAddress();
        Byte[] frameHeader = generateFrameHeader(randomDstMac, generateRandomMacAddress(), generateRandomType());

        System.arraycopy(frameHeader, 0, bytes, 0, frameHeader.length);

        Chunk<EmptyHeader> chunk = new Chunk<>();
        chunk.setHeader(new EmptyHeader());
        chunk.setPayload(new Payload(bytes));

        when(mockAddress.getAddress())
                .thenReturn(toPrimitive(randomDstMac));

        when(nic.getLinkLayerAddresses())
                .thenReturn(new ArrayList<>(List.of(mockAddress)));

        assertFalse(()-> ethernetLayer.receive(chunk, nic));
    }

    @Test
    void receive_에서_헤더_제거_후_payload_크기가_정확히_1500byte면_upperLayer의_receive가_호출되어야한다() {
        Byte[] bytes = new Byte[1500];
        Byte[] randomDstMac = generateRandomMacAddress();
        Byte[] frameHeader = generateFrameHeader(randomDstMac, generateRandomMacAddress(), generateRandomType());

        System.arraycopy(frameHeader, 0, bytes, 0, frameHeader.length);

        Chunk<EmptyHeader> chunk = new Chunk<>();
        chunk.setHeader(new EmptyHeader());
        chunk.setPayload(new Payload(bytes));

        when(mockAddress.getAddress())
                .thenReturn(toPrimitive(randomDstMac));

        when(nic.getLinkLayerAddresses())
                .thenReturn(new ArrayList<>(List.of(mockAddress)));

        when(mockUpperLayer.receive(any(), any())).thenReturn(true);

        assertTrue(()-> ethernetLayer.receive(chunk, nic));
    }

    @Test
    void receive_호출_시_Chunk_크기가_60byte를_미만이면_false를_리턴해야한다() {
        Chunk<EmptyHeader> chunk = new Chunk<>();
        chunk.setHeader(new EmptyHeader());
        Byte[] bytes = new Byte[57];
        chunk.setPayload(new Payload(bytes));

        assertFalse(()-> ethernetLayer.receive(chunk, nic));
    }

    @Test
    void receive_호출_시_Chunk_크기가_정확히_60byte면_정상수행되어야한다() {
        Byte[] bytes = new Byte[60];
        Byte[] randomDstMac = generateRandomMacAddress();
        Byte[] frameHeader = generateFrameHeader(randomDstMac, generateRandomMacAddress(), generateRandomType());

        System.arraycopy(frameHeader, 0, bytes, 0, frameHeader.length);

        Chunk<EmptyHeader> chunk = new Chunk<>();
        chunk.setHeader(new EmptyHeader());
        chunk.setPayload(new Payload(bytes));

        when(mockAddress.getAddress())
                .thenReturn(toPrimitive(randomDstMac));

        when(nic.getLinkLayerAddresses())
                .thenReturn(new ArrayList<>(List.of(mockAddress)));

        when(mockUpperLayer.receive(any(), any())).thenReturn(true);

        assertTrue(()-> ethernetLayer.receive(chunk, nic));
    }


    @Test
    void receive_호출_시_Chunk_크기가_1514byte_초과하면_false가_리턴되어야한다() {
        Chunk<EmptyHeader> chunk = new Chunk<>();
        chunk.setHeader(new EmptyHeader());
        Byte[] bytes = new Byte[1518];
        chunk.setPayload(new Payload(bytes));

        assertFalse(()-> ethernetLayer.receive(chunk, null));
    }

    /**
     * Header 제거하는 로직 작성 후 테스트 활성화 할 것 `@can019`
     */
    @Disabled
    void receive_호출_시_Chunk_크기가_정확히_1514byte면_illigalArgumentException이_발생하지_않는다() {
        Chunk<EmptyHeader> chunk = new Chunk<>();
        chunk.setHeader(new EmptyHeader());
        Byte[] bytes = new Byte[1514]; // 경계값
        chunk.setPayload(new Payload(bytes));

        assertDoesNotThrow(()-> ethernetLayer.receive(chunk, null));
    }

    @Test
    void receive시_나와_다른_mac이_제공되면_false를_리턴해야한다() {
        Byte[] bytes = new Byte[1500];
        Byte[] frameHeader = generateRandomFrameHeader();

        System.arraycopy(frameHeader, 0, bytes, 0, frameHeader.length);

        Chunk<EmptyHeader> chunk = new Chunk<>();
        chunk.setHeader(new EmptyHeader());
        chunk.setPayload(new Payload(bytes));

        when(mockAddress.getAddress())
                .thenReturn(toPrimitive(generateRandomMacAddress()));

        when(nic.getLinkLayerAddresses())
                .thenReturn(new ArrayList<>(List.of(mockAddress)));

        assertFalse(()-> ethernetLayer.receive(chunk, nic));
    }


    private Byte[] generateRandomMacAddress() {
        byte[] mac = new byte[6];
        current().nextBytes(mac);

        // 유효한 Unicast MAC으로 만들기 (multicast 비트, locally administered 비트 설정)
        mac[0] = (byte)(mac[0] & (byte)0xFE); // LSB 비트 0으로 설정 → unicast
        mac[0] = (byte)(mac[0] | (byte)0x02); // 두 번째 비트 1 → locally administered

        Byte[] result = new Byte[6];
        for (int i = 0; i < 6; i++) {
            result[i] = mac[i];
        }

        return result;
    }

    private Byte[] generateRandomType() {
        Byte[][] knownTypes = {
                {(byte) 0x08, (byte) 0x00}, // IPv4
                {(byte) 0x08, (byte) 0x06}, // ARP
                {(byte) 0x86, (byte) 0xDD}  // IPv6
        };

        int index = current().nextInt(knownTypes.length);
        return knownTypes[index];
    }

    private Byte[] generateRandomFrameHeader() {
        return generateFrameHeader(
                generateRandomMacAddress(),
                generateRandomMacAddress(),
                generateRandomType()
        );
    }

    private Byte[] generateFrameHeader(Byte[] randomDstMac, Byte[] randomSrcMac, Byte[] randomType) {
        int totalLength = randomSrcMac.length + randomDstMac.length + randomType.length;
        Byte[] candidateHeader = new Byte[totalLength];

        int pos = 0;
        System.arraycopy(randomDstMac, 0, candidateHeader, pos, randomDstMac.length);
        pos += randomDstMac.length;

        System.arraycopy(randomSrcMac, 0, candidateHeader, pos, randomSrcMac.length);
        pos += randomSrcMac.length;

        System.arraycopy(randomType, 0, candidateHeader, pos, randomType.length);

        return candidateHeader;
    }
}