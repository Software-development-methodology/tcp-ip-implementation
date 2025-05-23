package com.github.software_development_methodology_study.core.layer;

import com.github.software_development_methodology_study.core.data.chunk.Chunk;
import com.github.software_development_methodology_study.core.data.chunk.header.EmptyHeader;
import com.github.software_development_methodology_study.core.data.chunk.payload.Payload;
import org.pcap4j.core.*;

import java.util.List;

public class NetworkInterfaceLayer extends Layer<EmptyHeader> {
    private List<PcapHandle> pcapHandleList;

    public NetworkInterfaceLayer() throws PcapNativeException {
        pcapHandleList = getNICHandleList();
    }

    @Override
    public void receive(Chunk chunk) {

    }

    // Byte를 쓰면 unboxing 작업은 어디선가 해야 됨
    // 일단 모든 nic에서 패킷 발송하도록 함
    @Override
    public void send(Chunk chunk) {
        Byte[] payload = chunk.getPayload().getBytes();
        Byte[] header = chunk.getHeader().getBytes();

        Byte[] packet = new Byte[header.length + payload.length];
        System.arraycopy(header, 0, packet, 0, header.length);
        System.arraycopy(payload, 0, packet, header.length, payload.length);

        byte[] rawPacket = new byte[packet.length];
        for(int i = 0; i < packet.length; i++)
            rawPacket[i] = packet[i];

        pcapHandleList.stream().parallel().forEach(t -> {
            try {
                t.sendPacket(rawPacket);
            } catch (NotOpenException | PcapNativeException e) {
                throw new RuntimeException(e);
            }
        });
    }

    public void startNICThread(PcapHandle handle) {
        Thread thread = new Thread(() -> {
            try {
                handle.loop(-1, new RawPacketListener() {
                    @Override
                    public void gotPacket(byte[] packet) {
                        EmptyHeader emptyHeader = new EmptyHeader();
                        Chunk<EmptyHeader> chunk = new Chunk<>();
                        chunk.setHeader(emptyHeader);
//                        chunk.setPayload(new Payload(packet));  // Byte unboxing 귀찮아서 일단 주석처리..

                        // 타입 불일치 문제
                        // receive 메서드로 보내고 receive 메서드에서 upper를 호출할까?
//                        upperLayer.send(chunk);
                    }
                });
            } catch (PcapNativeException | NotOpenException | InterruptedException e) {
                throw new RuntimeException(e);
            }

        });
        thread.start();
    }

    // 루프를 명시적으로 종료하지 않으면 계속 돌 수 있다고 함
    public void stopNICThreads() throws PcapNativeException {
        pcapHandleList.forEach(t -> {
            try {
                t.breakLoop();
                t.close();
            } catch (NotOpenException e) {
                System.err.println("이미 닫힌 핸들입니다." + e);
            }
        });
    }



    private List<PcapHandle> getNICHandleList() throws PcapNativeException {
        List<PcapNetworkInterface> interfaces = Pcaps.findAllDevs();
        return interfaces.stream().map(t -> {
            try {
                return t.openLive(65535, PcapNetworkInterface.PromiscuousMode.PROMISCUOUS, 10);
            } catch (PcapNativeException e) {
                throw new RuntimeException(e);
            }
        }).toList();
    }
}
