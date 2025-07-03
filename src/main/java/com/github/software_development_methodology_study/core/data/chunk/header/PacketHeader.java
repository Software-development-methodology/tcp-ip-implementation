package com.github.software_development_methodology_study.core.data.chunk.header;

import com.github.software_development_methodology_study.core.data.contract.FixedSize;


public class PacketHeader extends Header implements FixedSize {
    private final Byte[] versionAndIHL;      // 버전(4비트) + 헤더 길이(4비트)
    private final Byte[] tos;                // 서비스 유형(Type of Service)
    private final Byte[] totalLength;        // 전체 패킷 길이
    private final Byte[] identification;     // 식별자
    private final Byte[] flagsAndOffset;     // 플래그(3비트) + 단편화 오프셋(13비트)
    private final Byte[] ttl;                // 생존 시간(Time To Live)
    private final Byte[] protocol;           // 상위 계층 프로토콜
    private final Byte[] headerChecksum;     // 헤더 체크섬
    private final Byte[] sourceIP;           // 출발지 IP 주소
    private final Byte[] destinationIP;      // 목적지 IP 주소
    private final Byte[] options;            // 옵션 (가변 길이)
    private final Byte[] padding;            // 패딩 (가변 길이)

    public PacketHeader(Byte[] rawHeader, Byte[] versionAndIHL, Byte[] tos, Byte[] totalLength,
                        Byte[] identification, Byte[] flagsAndOffset, Byte[] ttl, Byte[] protocol,
                        Byte[] headerChecksum, Byte[] sourceIP, Byte[] destinationIP,
                        Byte[] options, Byte[] padding) {
        super(rawHeader);
        this.versionAndIHL = versionAndIHL;
        this.tos = tos;
        this.totalLength = totalLength;
        this.identification = identification;
        this.flagsAndOffset = flagsAndOffset;
        this.ttl = ttl;
        this.protocol = protocol;
        this.headerChecksum = headerChecksum;
        this.sourceIP = sourceIP;
        this.destinationIP = destinationIP;
        this.options = options;
        this.padding = padding;
    }

    @Override
    public int expectedLength() {
        return 20; // 기본 IPv4 헤더 길이는 20바이트 (옵션 필드 제외)
    }

    public Byte[] getVersionAndIHL() {
        return versionAndIHL;
    }

    public Byte[] getTos() {
        return tos;
    }

    public Byte[] getTotalLength() {
        return totalLength;
    }

    public Byte[] getIdentification() {
        return identification;
    }

    public Byte[] getFlagsAndOffset() {
        return flagsAndOffset;
    }

    public Byte[] getTtl() {
        return ttl;
    }

    public Byte[] getProtocol() {
        return protocol;
    }

    public Byte[] getHeaderChecksum() {
        return headerChecksum;
    }

    public Byte[] getSourceIP() {
        return sourceIP;
    }

    public Byte[] getDestinationIP() {
        return destinationIP;
    }

    public Byte[] getOptions() {
        return options;
    }

    public Byte[] getPadding() {
        return padding;
    }

    public static class PacketHeaderBuilder {
        private Byte[] rawHeader;
        private Byte[] versionAndIHL;
        private Byte[] tos;
        private Byte[] totalLength;
        private Byte[] identification;
        private Byte[] flagsAndOffset;
        private Byte[] ttl;
        private Byte[] protocol;
        private Byte[] headerChecksum;
        private Byte[] sourceIP;
        private Byte[] destinationIP;
        private Byte[] options;
        private Byte[] padding;

        public PacketHeaderBuilder rawHeader(Byte[] rawHeader) {
            this.rawHeader = rawHeader;
            return this;
        }

        public PacketHeaderBuilder versionAndIHL(Byte[] versionAndIHL) {
            this.versionAndIHL = versionAndIHL;
            return this;
        }

        public PacketHeaderBuilder tos(Byte[] tos) {
            this.tos = tos;
            return this;
        }

        public PacketHeaderBuilder totalLength(Byte[] totalLength) {
            this.totalLength = totalLength;
            return this;
        }

        public PacketHeaderBuilder identification(Byte[] identification) {
            this.identification = identification;
            return this;
        }

        public PacketHeaderBuilder flagsAndOffset(Byte[] flagsAndOffset) {
            this.flagsAndOffset = flagsAndOffset;
            return this;
        }

        public PacketHeaderBuilder ttl(Byte[] ttl) {
            this.ttl = ttl;
            return this;
        }

        public PacketHeaderBuilder protocol(Byte[] protocol) {
            this.protocol = protocol;
            return this;
        }

        public PacketHeaderBuilder headerChecksum(Byte[] headerChecksum) {
            this.headerChecksum = headerChecksum;
            return this;
        }

        public PacketHeaderBuilder sourceIP(Byte[] sourceIP) {
            this.sourceIP = sourceIP;
            return this;
        }

        public PacketHeaderBuilder destinationIP(Byte[] destinationIP) {
            this.destinationIP = destinationIP;
            return this;
        }

        public PacketHeaderBuilder options(Byte[] options) {
            this.options = options;
            return this;
        }

        public PacketHeaderBuilder padding(Byte[] padding) {
            this.padding = padding;
            return this;
        }

        public PacketHeader build() {
            return new PacketHeader(rawHeader, versionAndIHL, tos, totalLength, identification,
                    flagsAndOffset, ttl, protocol, headerChecksum, sourceIP, destinationIP,
                    options, padding);
        }
    }

    public static PacketHeaderBuilder builder() {
        return new PacketHeaderBuilder();
    }
}
