package com.github.software_development_methodology_study.core.data.chunk.header;

public class FrameHeader extends Header{
    private final Byte[] preamble;
    private final Byte[] destinationMac;
    private final Byte[] srcMac;
    private final Byte[] type;
    private final Byte[] fcs;


    public FrameHeader(Byte[] rawHeader, Byte[] preamble, Byte[] destinationMac, Byte[] srcMac, Byte[] type, Byte[] fcs) {
        super(rawHeader);
        this.preamble = preamble;
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

    public static class FrameHeaderBuilder {
        private Byte[] rawHeader;
        private Byte[] destinationMac;
        private Byte[] srcMac;
        private Byte[] type;
        private Byte[] fcs;
        private Byte[] preamble;

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

        public FrameHeaderBuilder fcs(Byte[] fcs) {
            this.fcs = fcs;
            return this;
        }

        public FrameHeaderBuilder preamble(Byte[] preamble) {
            this.preamble = preamble;
            return this;
        }

        public FrameHeader build() {
            return new FrameHeader(rawHeader, preamble, destinationMac, srcMac, type, fcs);
        }
    }

    public static FrameHeaderBuilder builder() {
        return new FrameHeaderBuilder();
    }
}