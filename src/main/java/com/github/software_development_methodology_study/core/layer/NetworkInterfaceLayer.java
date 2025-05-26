package com.github.software_development_methodology_study.core.layer;

import com.github.software_development_methodology_study.core.data.chunk.Chunk;
import org.pcap4j.core.*;
import org.pcap4j.packet.Packet;

import java.io.EOFException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeoutException;

public class NetworkInterfaceLayer {
    private final ExecutorService threadPool;
    private final static NetworkInterfaceLayer instance = new NetworkInterfaceLayer();
    private List<PcapHandle> pcapHandleList = new ArrayList<>();

    public NetworkInterfaceLayer() {
        try {
            pcapHandleList = getNICHandleList();
            this.threadPool = Executors.newFixedThreadPool(pcapHandleList.size());
        } catch (PcapNativeException e) {
            throw new RuntimeException(e);
        }
    }

    public static NetworkInterfaceLayer getInstance() {
        return Objects.requireNonNullElseGet(instance, NetworkInterfaceLayer::new);
    }

    public void run() {
        for (PcapHandle handle : pcapHandleList) {
            threadPool.submit(() -> receive(handle));
        }
    }

    public void receive(PcapHandle handle) {
        try {
            while (!Thread.currentThread().isInterrupted()) {
                Packet packet = handle.getNextPacketEx();  // blocking
                if (packet != null) {
                    byte[] rawData = packet.getRawData();
                    System.out.println(Arrays.toString(rawData));
//                    upperLayer.receive(rawData);  // 상위 계층으로 전달
                }
            }
        }
//        catch (InterruptedException e) {
//            Thread.currentThread().interrupt();
//        }
        catch (PcapNativeException | NotOpenException | TimeoutException | EOFException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        } finally {
            if (handle != null && handle.isOpen()) {
                handle.close();
            }
        }
    }

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

    public void stopNICThreads() {
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
