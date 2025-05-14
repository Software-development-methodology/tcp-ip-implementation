package header;

// 팩토리 메서드
public class HeaderCreator {
    public static Header createHeader(String headerType, int payloadLength, String extraInfo) {
        switch (headerType) {
            case "FRAME":
                String macAddresses[] = extraInfo.split(":");
                return new FrameHeader(headerType, payloadLength, macAddresses[0], macAddresses[1]);

            case "PACKET":
                String[] addresses = extraInfo.split(":");
                return new PacketHeader(headerType, payloadLength, addresses[0], addresses[1]);

            case "SEGMENT":
                int sequenceNumber = Integer.parseInt(extraInfo);
                return new SegmentHeader(headerType, payloadLength, sequenceNumber);

            case "HTTPMESSAGE":
                return new HttpMessageHeader(headerType, payloadLength, extraInfo);

            default:
                throw new IllegalArgumentException("Unknown header type: " + headerType);

        }
    }
}
