package header;

public class HttpMessageHeader implements Header {
    private String type;
    private int payloadLength;
    private String httpVersion;

    public HttpMessageHeader(String type, int payloadLength, String httpVersion) {
        this.type = type;
        this.payloadLength = payloadLength;
        this.httpVersion = httpVersion;
    }

    @Override
    public byte[] toBytes() {
        String headerStr = type + String.format("%04d", payloadLength) + httpVersion;
        return headerStr.getBytes();
    }

    @Override
    public String getType() {
        return type;
    }
}
