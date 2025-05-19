package com.github.software_development_methodology_study;

import org.pcap4j.core.*;
import org.pcap4j.packet.Packet;

import java.io.EOFException;
import java.net.*;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;
import java.util.concurrent.TimeoutException;

public class Main {
    public static void main(String[] args) throws Exception {

        Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();

        while (interfaces.hasMoreElements()) {
            NetworkInterface ni = interfaces.nextElement();
            if (ni.isLoopback() || !ni.isUp()) continue;

            for (InterfaceAddress ia : ni.getInterfaceAddresses()) {
                InetAddress addr = ia.getAddress();
                if (addr instanceof Inet4Address) {
                    System.out.println("Local IP: " + addr.getHostAddress());
                }
            }
        }



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

        // 패킷 캡처
        Packet packet = handle.getNextPacketEx();
        System.out.println(packet);


// 바이트 배열 형태의 로우 패킷 데이터
        byte[] rawData = packet.getRawData();

        System.out.println(Arrays.toString(rawData));

        handle.sendPacket(rawData);

        handle.close();
    }
}