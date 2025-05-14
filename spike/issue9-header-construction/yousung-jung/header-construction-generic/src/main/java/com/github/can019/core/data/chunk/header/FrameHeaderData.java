package com.github.can019.core.data.chunk.header;

import java.util.Arrays;
import java.util.Objects;

public class FrameHeaderData implements HeaderData {
    private final byte[] destMac;
    private final byte[] srcMac;
    private final int type;

    private FrameHeaderData(Builder builder) {
        this.destMac = Arrays.copyOf(builder.destMac, builder.destMac.length);
        this.srcMac = Arrays.copyOf(builder.srcMac, builder.srcMac.length);
        this.type = builder.type;
    }

    public byte[] getDestMac() {
        return Arrays.copyOf(destMac, destMac.length);
    }

    public byte[] getSrcMac() {
        return Arrays.copyOf(srcMac, srcMac.length);
    }

    public int getType() {
        return type;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private byte[] destMac;
        private byte[] srcMac;
        private int type;

        public Builder destMac(byte[] destMac) {
            this.destMac = Arrays.copyOf(destMac, destMac.length);
            return this;
        }

        public Builder srcMac(byte[] srcMac) {
            this.srcMac = Arrays.copyOf(srcMac, srcMac.length);
            return this;
        }

        public Builder type(int type) {
            this.type = type;
            return this;
        }

        public FrameHeaderData build() {
            Objects.requireNonNull(destMac, "destMac must not be null");
            Objects.requireNonNull(srcMac, "srcMac must not be null");
            return new FrameHeaderData(this);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof FrameHeaderData)) return false;
        FrameHeaderData that = (FrameHeaderData) o;
        return type == that.type &&
                Arrays.equals(destMac, that.destMac) &&
                Arrays.equals(srcMac, that.srcMac);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(type);
        result = 31 * result + Arrays.hashCode(destMac);
        result = 31 * result + Arrays.hashCode(srcMac);
        return result;
    }

    @Override
    public String toString() {
        return "FrameHeaderData{" +
                "destMac=" + bytesToHex(destMac) +
                ", srcMac=" + bytesToHex(srcMac) +
                ", type=0x" + Integer.toHexString(type) +
                '}';
    }

    private String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < bytes.length; i++) {
            sb.append(String.format("%02X", bytes[i]));
            if (i < bytes.length - 1) sb.append(":");
        }
        return sb.toString();
    }
}
