package com.github.can019.core.data.chunk.header;

public final class EmptyHeader implements Header<EmptyHeaderData> {
    private static final EmptyHeader INSTANCE = new EmptyHeader();

    private EmptyHeader() {}

    public static EmptyHeader getInstance() {
        return INSTANCE;
    }

    @Override
    public EmptyHeaderData getData() {
        return new EmptyHeaderData(); // 아무 필드도 없음
    }
}
