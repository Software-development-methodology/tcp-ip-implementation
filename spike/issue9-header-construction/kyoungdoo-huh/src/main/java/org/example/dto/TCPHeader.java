package org.example.dto;

public class TCPHeader implements Header {
    private byte[] srcPortNum;
    private byte[] destPortNum;
    private byte[] seqNum;
    private byte[] ackNum;
    private byte[] dataOffset;
    private byte[] reserved;
    private byte[] flags;
    private byte[] windowSize;
    private byte[] checkSum;
    private byte[] urgentPointer;
    private byte[] options;

    public TCPHeader() {
        srcPortNum = new byte[]{1, 0};
    }

    @Override
    public byte[] toByteArr() {
        return new byte[0];
    }
}
