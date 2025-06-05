package com.github.software_development_methodology_study.core.data.chunk.header;

public class FrameHeader extends Header{
    private final Byte[] destinationMac;
    private final Byte[] srcMac;
    private final Byte[] type;


    public FrameHeader(Byte[] rawHeader, Byte[] destinationMac, Byte[] srcMac, Byte[] type) {
        super(rawHeader);
        this.destinationMac = destinationMac;
        this.srcMac = srcMac;
        this.type = type;
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

    public static class FrameHeaderBuilder {
        private Byte[] rawHeader;
        private Byte[] destinationMac;
        private Byte[] srcMac;
        private Byte[] type;

        public FrameHeaderBuilder rawHeader(Byte[] rawHeader) {
            this.rawHeader = rawHeader;
            return this;
        }

        public FrameHeaderBuilder destinationMac(Byte[] destinationMac) {
            this.destinationMac = destinationMac;
            return this;
        }

        public FrameHeaderBuilder srcMac(Byte[] srcMac) {
            this.srcMac = srcMac;
            return this;
        }

        public FrameHeaderBuilder type(Byte[] type) {
            this.type = type;
            return this;
        }

        public FrameHeader build() {
            return new FrameHeader(rawHeader, destinationMac, srcMac, type);
        }
    }

    public static FrameHeaderBuilder builder() {
        return new FrameHeaderBuilder();
    }
}