package com.github.software_development_methodology_study.core.data.chunk.header;

public enum FrameHeaderField {
    DESTINATION_MAC(0, 6),
    SRC_MAC(6, 6),
    TYPE(12, 2);

    private final int startIndex;
    private final int length;

    FrameHeaderField(int startIndex, int length) {
        this.startIndex = startIndex;
        this.length = length;
    }

    public int getStartIndex(int totalLength) {
        return startIndex >= 0 ? startIndex : (totalLength + startIndex);
    }

    public int getEndIndex() {
        return (startIndex + length) -1;
    }

    public int getLength() {
        return length;
    }
}
