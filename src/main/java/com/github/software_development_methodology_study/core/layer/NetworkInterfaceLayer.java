package com.github.software_development_methodology_study.core.layer;

import com.github.software_development_methodology_study.core.data.chunk.Chunk;
import org.pcap4j.core.*;
import org.pcap4j.packet.Packet;

import java.io.EOFException;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeoutException;

/**
 * 네트워크 인터페이스 계층을 담당하는 싱글톤 클래스.
 * 각 네트워크 인터페이스로부터 패킷을 수신하고 전송할 수 있습니다.
 */
public class NetworkInterfaceLayer {
    private final ExecutorService threadPool;
    private final static NetworkInterfaceLayer instance = new NetworkInterfaceLayer();
    private List<PcapHandle> pcapHandleList;

    /**
     * 생성자 - 네트워크 인터페이스를 열고, 스레드 풀을 초기화합니다.
     */
    private NetworkInterfaceLayer() {
        try {
            pcapHandleList = getNICHandleList();
            this.threadPool = Executors.newFixedThreadPool(pcapHandleList.size());
        } catch (PcapNativeException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 싱글톤 인스턴스를 반환합니다.
     *
     * @return NetworkInterfaceLayer 인스턴스
     */
    public static NetworkInterfaceLayer getInstance() {
        return Objects.requireNonNullElseGet(instance, NetworkInterfaceLayer::new);
    }

    /**
     * 각 네트워크 인터페이스에 대해 패킷 수신 스레드를 시작합니다.
     */
    public void run() {
        for (PcapHandle handle : pcapHandleList) {
            threadPool.submit(() -> receive(handle));
        }
    }

    /**
     * 주어진 PcapHandle로부터 패킷을 블로킹 방식으로 계속 수신합니다.
     * 패킷 수신 시, 해당 데이터를 byte 배열로 출력합니다.
     *
     * @param handle PcapHandle 객체
     */
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
        } catch (PcapNativeException | NotOpenException | TimeoutException | EOFException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        } finally {
            if (handle != null && handle.isOpen()) {
                handle.close();
            }
        }
    }

    /**
     * 상위 계층으로부터 전달받은 Chunk 데이터를 네트워크 인터페이스로 전송합니다.
     *
     * @param chunk 전송할 Chunk 데이터
     */
    public void send(Chunk chunk) {
        Byte[] payload = chunk.getPayload().getBytes();
        Byte[] header = chunk.getHeader().getBytes();

        Byte[] packet = new Byte[header.length + payload.length];
        System.arraycopy(header, 0, packet, 0, header.length);
        System.arraycopy(payload, 0, packet, header.length, payload.length);

        byte[] rawPacket = new byte[packet.length];
        for (int i = 0; i < packet.length; i++)
            rawPacket[i] = packet[i];

        pcapHandleList.stream().parallel().forEach(t -> {
            try {
                t.sendPacket(rawPacket);
            } catch (NotOpenException | PcapNativeException e) {
                throw new RuntimeException(e);
            }
        });
    }

    /**
     * 모든 NIC 핸들 스레드를 종료하고 리소스를 반환합니다.
     */
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

    /**
     * 시스템에 존재하는 모든 네트워크 인터페이스를 검색하고,
     * 각 인터페이스에 대해 {@link PcapHandle} 객체를 생성하여 반환합니다.
     * <p>
     * 각 인터페이스는 다음과 같은 설정으로 열립니다:
     * <ul>
     *   <li><b>snaplen = 65535</b>: 패킷을 최대 65535 바이트까지 캡처하여, 대부분의 패킷을 손실 없이 수신합니다.</li>
     *   <li><b>PromiscuousMode.PROMISCUOUS</b>: 프로미스큐어스 모드로, 자신의 MAC 주소가 아닌 패킷도 모두 수신합니다. 패킷 스니핑 등에 사용됩니다.</li>
     *   <li><b>timeout = 10ms</b>: 패킷 수신 시 최대 10밀리초 동안 대기합니다. 짧은 응답 지연을 위한 설정입니다.</li>
     * </ul>
     *
     * @return 각 네트워크 인터페이스에 대해 생성된 {@link PcapHandle} 객체 리스트
     * @throws PcapNativeException 네이티브 API 호출 중 오류가 발생한 경우
     */
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
