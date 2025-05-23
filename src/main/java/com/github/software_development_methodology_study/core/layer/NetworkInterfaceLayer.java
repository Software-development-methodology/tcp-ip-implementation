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
    private final List<PcapHandle> handles = new ArrayList<>();

    public NetworkInterfaceLayer() {
        try {
            List<PcapNetworkInterface> interfaces = Pcaps.findAllDevs();
            this.threadPool = Executors.newFixedThreadPool(interfaces.size());

            for (PcapNetworkInterface nif : interfaces) {
                PcapHandle handle = nif.openLive(65536, PcapNetworkInterface.PromiscuousMode.PROMISCUOUS, 10);
                handles.add(handle);
            }
        } catch (PcapNativeException e) {
            throw new RuntimeException(e);
        }
    }

    public static NetworkInterfaceLayer getInstance() {
        return Objects.requireNonNullElseGet(instance, NetworkInterfaceLayer::new);
    }

    public void run() {
        for (PcapHandle handle : handles) {
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
            throw new RuntimeException(e);
        } finally {
            if (handle != null && handle.isOpen()) {
                handle.close();
            }
        }
    }

    public void send(Chunk chunk) {
        for (PcapHandle handle : handles) {
//            handle.sendPacket(chunk.toByteArr());
        }
    }
}
