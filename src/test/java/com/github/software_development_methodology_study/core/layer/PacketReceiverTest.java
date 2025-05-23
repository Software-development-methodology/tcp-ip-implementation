package com.github.software_development_methodology_study.core.layer;

import org.junit.jupiter.api.Test;
import org.pcap4j.core.*;
import org.pcap4j.packet.Packet;

import java.io.EOFException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeoutException;

import static org.junit.jupiter.api.Assertions.*;

class NetworkInterfaceLayerTest {


    @Test
    void test() throws PcapNativeException, NotOpenException, EOFException, TimeoutException {
        List<PcapNetworkInterface> interfaces1 = Pcaps.findAllDevs();
        for (PcapNetworkInterface nif : interfaces1) {
            System.out.println(nif.getName() + " : " + nif.getDescription());
        }
        // NIF 가져오기
//        PcapNetworkInterface nif = Pcaps.getDevByName("\\Device\\NPF_{6F60B71D-1C9E-4C66-97E6-FBA81EBD2E6E}");
        PcapNetworkInterface nif = Pcaps.getDevByName("\\Device\\NPF_{83D8ABE6-6CA7-45BA-A361-335258177916}");

        if (nif == null) {
            System.out.println("인터페이스 못 찾음");
            return;
        }

        // 캡처 핸들러 열기
        PcapHandle handle = nif.openLive(65536, PcapNetworkInterface.PromiscuousMode.PROMISCUOUS, 10000);

        Packet packet = handle.getNextPacketEx();
            List<Packet> test = new ArrayList<>();
        // 패킷 캡처
        int i = 100;
        while (i-->1) {
            packet = handle.getNextPacketEx();
            System.out.println(packet);
        }
    }

}