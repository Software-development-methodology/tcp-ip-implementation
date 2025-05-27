package com.github.software_development_methodology_study.core.data.chunk.header;

import lombok.Builder;

@Builder
public class FrameHeader extends Header{
    private final Byte[] destinationMac;
    private final Byte[] srcMac;
    private final Byte[] type;
    private final Byte[] fcs;

    public FrameHeader(Byte[] rawHeader, Byte[] destinationMac, Byte[] srcMac, Byte[] type, Byte[] fcs) {
        super(rawHeader);
        this.destinationMac = destinationMac;
        this.srcMac = srcMac;
        this.type = type;
        this.fcs = fcs;
    }

    public Byte[] getDestinationMac() {
        return destinationMac;
    }

    public Byte[] getSrcMac() {
        return srcMac;
    }

    public Byte[] getType() {
        return type;
    }
    public Byte[] getFcs() {
        return fcs;
    }
}