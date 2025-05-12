package header;

public class SegmentHeader implements Header {
    private String type;
    private int payloadLength;
    private int sequenceNumber;

    public SegmentHeader(String type, int payloadLength, int sequenceNumber) {
        this.type = type;
        this.payloadLength = payloadLength;
        this.sequenceNumber = sequenceNumber;
    }

    @Override
    public byte[] toBytes() {
        String headerStr = type + String.format("%04d", payloadLength) + String.format("%08d", sequenceNumber);
        return headerStr.getBytes();
    }

    @Override
    public String getType() {
        return type;
    }

    @Override
    public String toString() {
        return "SegmentHeader { type=" + type + ", payloadLength=" + payloadLength + ", sequenceNumber=" + sequenceNumber + " }";
    }

}

