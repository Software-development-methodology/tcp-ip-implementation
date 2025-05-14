package header;

public class PacketHeader implements Header {
    private String type;
    private int payloadLength;
    private String sourceAddress;
    private String destinationAddress;

    public PacketHeader(String type, int payloadLength, String sourceAddress, String destinationAddress) {
        this.type = type;
        this.payloadLength = payloadLength;
        this.sourceAddress = sourceAddress;
        this.destinationAddress = destinationAddress;
    }

    @Override
    public byte[] toBytes() {
        String headerStr = type + String.format("%04d", payloadLength) + sourceAddress + destinationAddress;
        return headerStr.getBytes();
    }
    @Override
    public String getType() {
        return type;
    }
}