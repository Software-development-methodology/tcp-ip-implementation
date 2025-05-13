package org.example.dto;

import org.example.util.Util;

public class IPHeader implements Header{
    private byte[] ipVersion;
    private byte[] headerLength;
    private byte[] tos;
    private byte[] totalLength;
    private byte[] identification;
    private byte[] flags;
    private byte[] fragmentOffset;
    private byte[] timeToLive;
    private byte[] protocol;
    private byte[] headerChecksum;
    private byte[] srcIPAddr;
    private byte[] destIPAddr;
    private byte[] options;
    private byte[] padding;

    public IPHeader() {
        ipVersion = new byte[] {0, 1, 1};
        headerLength = new byte[] {0, 1, 1};
    }

    @Override
    public byte[] toByteArr() {
        return Util.mergeByteArrays(ipVersion, headerLength);
    }
}
