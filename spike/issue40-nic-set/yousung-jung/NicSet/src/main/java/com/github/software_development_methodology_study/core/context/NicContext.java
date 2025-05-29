package com.github.software_development_methodology_study.core.context;

import org.pcap4j.core.PcapNetworkInterface;

import java.net.Inet4Address;
import java.net.InetAddress;

public class NicContext {

    private final PcapNetworkInterface device;
    private final byte[] macAddress;
    private final InetAddress ipv4;

    public NicContext(PcapNetworkInterface device) {
        this.device = device;

        byte[] mac;
        try {
            mac = device.getLinkLayerAddresses()
                    .stream()
                    .filter(addr -> addr.getAddress().length == 6)
                    .findFirst()
                    .map(addr -> addr.getAddress())
                    .orElse(null);
        } catch (Exception e) {
            mac = null;
        }
        this.macAddress = mac;

        this.ipv4 = device.getAddresses()
                .stream()
                .map(a -> a.getAddress())
                .filter(addr -> addr instanceof Inet4Address)
                .findFirst()
                .orElse(null);
    }

    public PcapNetworkInterface getDevice() {
        return device;
    }

    public byte[] getMacAddress() {
        return macAddress;
    }

    public InetAddress getIpv4Address() {
        return ipv4;
    }

    public String getDisplayName() {
        return device.getName() + " - " + device.getDescription();
    }
}

