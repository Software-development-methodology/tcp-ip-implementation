package com.github.can019.core.data.chunk.header;

public final class FrameHeader implements Header<FrameHeaderData> {
    private final FrameHeaderData data;

    public FrameHeader(FrameHeaderData data) {
        this.data = data;
    }

    @Override
    public FrameHeaderData getData() {
        return data;
    }
}