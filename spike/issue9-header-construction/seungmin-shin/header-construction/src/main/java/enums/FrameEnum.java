package enums;

public enum FrameEnum {
    DESTINATION_MAC_ADDRESS(0,48),
    SOURCE_MAC_ADDRESS(48,48),
    TYPE(96,16)
    ;


    public final int startOffset;
    public final int length;

    FrameEnum(int startOffset, int length) {
        this.startOffset = startOffset;
        this.length = length;
    }
}
