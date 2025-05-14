package org.example.dto;

import org.example.util.Util;

public class EthernetHeader implements Header {
    private byte[] destMACAddr;
    private byte[] srcMACAddr;
    private byte[] ethernetType;

    public EthernetHeader() {
        destMACAddr = new byte[] {1, 0, 0};
        srcMACAddr = new byte[] {1, 0, 0};
        ethernetType = new byte[] {1, 0};
    }

    public byte[] getDestMACAddr() {
        return destMACAddr;
    }

    @Override
    public byte[] toByteArr() {
        return Util.mergeByteArrays(destMACAddr, srcMACAddr, ethernetType);
    }
}
