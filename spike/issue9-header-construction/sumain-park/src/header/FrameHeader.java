package header;

public class FrameHeader implements Header {
    private String type;
    private int payloadLength;
    private String sourceMac;
    private String destinationMac;

    public FrameHeader(String type, int payloadLength, String sourceMac, String destinationMac) {
        this.type = type;
        this.payloadLength = payloadLength;
        this.sourceMac = sourceMac;
        this.destinationMac = destinationMac;
    }

    @Override
    public byte[] toBytes() {
        String headerStr = type + String.format("%04d", payloadLength) + sourceMac + destinationMac;
        return headerStr.getBytes();
    }

    @Override
    public String getType() {
        return type;
    }

}
