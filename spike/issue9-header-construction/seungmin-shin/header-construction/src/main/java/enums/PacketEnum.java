package enums;

public enum PacketEnum {
    VERSION(0, 4, 4),
    IHL(4, 8, 4),
    TYPE_OF_SERVICE(8, 16, 8),
    TOTAL_LENGTH(16,32, 16),
    IDENTIFICATION(32, 48, 16),
    FLAGS(48, 51, 3),
    FRAGMENT_OFFSET(51,64, 13),
    TIME_TO_LIVE(64, 72, 8),
    PROTOCOL(72, 80, 8),
    HEADER_CHECKSUM(80, 96, 16),
    SOURCE_IP_ADDRESS(96, 128, 32),
    DESTINATION_IP_ADDRESS(128,160, 32);

    public final int startOffset;
    public final int endOffset;
    public final int length;

    PacketEnum(int startOffset, int endOffset, int length) {
        this.startOffset = startOffset;
        this.endOffset = endOffset;
        this.length = length;
    }
}
