package com.github.software_development_methodology_study.core.layer;

import com.github.software_development_methodology_study.core.data.chunk.Chunk;
import com.github.software_development_methodology_study.core.data.chunk.header.Header;
import com.github.software_development_methodology_study.core.data.chunk.header.PacketHeader;
import com.github.software_development_methodology_study.core.data.chunk.payload.Payload;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class InternetLayer2 extends Layer<PacketHeader> {

    private static final int HEADER_SIZE = 20;
    private static final int MTU = 1500;
    private static final int FRAGMENT_SIZE = MTU - HEADER_SIZE;

    private record FragmentKey(String srcIp, String dstIp, int protocol, int identification) {}

    public final ConcurrentHashMap<FragmentKey, ConcurrentHashMap<Integer, Byte[]>> fragmentBuffer = new ConcurrentHashMap<>();

    @Override
    public void receive(Chunk<Header> chunk) {
        if (chunk.getPayload() == null)
            throw new NullPointerException("Chunk is null");

        PacketHeader header = parseHeader(chunk.getPayload().getBytes());
        chunk.setHeader(header);

        if (!isLocalIPAddress(header.getDestination_Address())) return;

        FragmentKey key = getFragmentKey(header);
        storeFragment(key, header, chunk.getPayload());

        if (isLastFragment(header.getFlags(), header.getFragment_Offset())) {
            Byte[] merged = mergeFragments(key);
            chunk.setPayload(new Payload(merged));
            fragmentBuffer.remove(key);
            upperLayer.receive(chunk);
        }
    }

    @Override
    public void send(Chunk<Header> chunk) {
        Byte[] payload = chunk.getPayload().getBytes();
        int totalFragments = (int) Math.ceil((double) payload.length / FRAGMENT_SIZE);
        int identification = new Random().nextInt(65536);

        for (int i = 0; i < totalFragments; i++) {
            int offset = i * FRAGMENT_SIZE;
            boolean isLast = (i == totalFragments - 1);
            int fragmentPayloadSize = Math.min(FRAGMENT_SIZE, payload.length - offset);
            Byte[] fragmentData = Arrays.copyOfRange(payload, offset, offset + fragmentPayloadSize);

            PacketHeader header = buildHeader(identification, offset / 8, isLast, HEADER_SIZE + fragmentPayloadSize);
            Chunk<Header> fragmentChunk = new Chunk<>();
            fragmentChunk.setHeader(header);
            fragmentChunk.setPayload(new Payload(fragmentData));

            lowerLayer.send(fragmentChunk);
        }
    }

    private PacketHeader parseHeader(Byte[] rawBytes) {
        PacketHeader header = new PacketHeader();
        int versionAndIHL = Byte.toUnsignedInt(rawBytes[0]);
        int ihl = versionAndIHL & 0x0F;
        int version = versionAndIHL >> 4;

        if (ihl < 5) throw new IllegalArgumentException("Invalid IHL: " + ihl);

        int totalLength = (Byte.toUnsignedInt(rawBytes[2]) << 8) | Byte.toUnsignedInt(rawBytes[3]);
        int identification = (Byte.toUnsignedInt(rawBytes[4]) << 8) | Byte.toUnsignedInt(rawBytes[5]);
        int flagsAndOffset = (Byte.toUnsignedInt(rawBytes[6]) << 8) | Byte.toUnsignedInt(rawBytes[7]);
        int flags = (flagsAndOffset >> 13) & 0x07;
        int fragmentOffset = flagsAndOffset & 0x1FFF;
        int ttl = Byte.toUnsignedInt(rawBytes[8]);
        int protocol = Byte.toUnsignedInt(rawBytes[9]);

        Byte[] srcAddr = Arrays.copyOfRange(rawBytes, 12, 16);
        Byte[] dstAddr = Arrays.copyOfRange(rawBytes, 16, 20);

        header.setVersion(new Byte[]{(byte) version});
        header.setIHL(new Byte[]{(byte) ihl});
        header.setTotal_Length(new Byte[]{(byte) (totalLength >> 8), (byte) totalLength});
        header.setIdentification(new Byte[]{(byte) (identification >> 8), (byte) identification});
        header.setFlags(new Byte[]{(byte) ((flags << 5) & 0xE0)});
        header.setFragment_Offset(new Byte[]{
                (byte) ((fragmentOffset >> 8) & 0x1F), (byte) fragmentOffset
        });
        header.setTTL(new Byte[]{(byte) ttl});
        header.setProtocol(new Byte[]{(byte) protocol});
        header.setSource_Address(srcAddr);
        header.setDestination_Address(dstAddr);
        return header;
    }

    private PacketHeader buildHeader(int identification, int offsetValue, boolean isLast, int totalLength) {
        PacketHeader header = new PacketHeader();
        header.setVersion(new Byte[]{(byte) 4});
        header.setIHL(new Byte[]{(byte) 5});
        header.setTotal_Length(new Byte[]{(byte) (totalLength >> 8), (byte) totalLength});
        header.setIdentification(new Byte[]{(byte) (identification >> 8), (byte) identification});

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
        return header;
    }

    private FragmentKey getFragmentKey(PacketHeader header) {
        return new FragmentKey(
                ipBytesToString(header.getSource_Address()),
                ipBytesToString(header.getDestination_Address()),
                Byte.toUnsignedInt(header.getProtocol()[0]),
                (Byte.toUnsignedInt(header.getIdentification()[0]) << 8) |
                        Byte.toUnsignedInt(header.getIdentification()[1])
        );
    }

    private void storeFragment(FragmentKey key, PacketHeader header, Payload payload) {
        int rawOffset = (Byte.toUnsignedInt(header.getFragment_Offset()[0]) << 8)
                | Byte.toUnsignedInt(header.getFragment_Offset()[1]);
        int offset = rawOffset & 0x1FFF;

        fragmentBuffer.putIfAbsent(key, new ConcurrentHashMap<>());
        fragmentBuffer.get(key).put(offset, payload.getBytes());
    }

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

    private boolean isLocalIPAddress(Byte[] ipAddress) {
        // 나중에 실제 로컬 IP와 비교하도록 확장
        return true;
    }

    private boolean isLastFragment(Byte[] flagsBytes, Byte[] offsetBytes) {
        if (flagsBytes == null || offsetBytes == null || flagsBytes.length < 1 || offsetBytes.length < 2)
            throw new IllegalArgumentException("Flags 또는 Offset이 유효하지 않음");
        int flags = Byte.toUnsignedInt(flagsBytes[0]) >> 5;
        return (flags & 0b1) == 0;
    }

    private String ipBytesToString(Byte[] ip) {
        if (ip == null || ip.length != 4) return "0.0.0.0";
        return String.format("%d.%d.%d.%d",
                Byte.toUnsignedInt(ip[0]),
                Byte.toUnsignedInt(ip[1]),
                Byte.toUnsignedInt(ip[2]),
                Byte.toUnsignedInt(ip[3]));
    }
}