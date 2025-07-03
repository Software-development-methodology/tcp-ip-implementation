package com.github.software_development_methodology_study.core.data.chunk.header;


public enum IPHeaderField {
    VERSION_IHL(0, 1),           // 버전(4비트) + 헤더 길이(4비트)
    TOS(1, 1),                   // 서비스 유형(Type of Service)
    TOTAL_LENGTH(2, 2),          // 전체 패킷 길이
    IDENTIFICATION(4, 2),        // 식별자
    FLAGS_FRAGMENT_OFFSET(6, 2), // 플래그(3비트) + 단편화 오프셋(13비트)
    TTL(8, 1),                   // 생존 시간(Time To Live)
    PROTOCOL(9, 1),              // 상위 계층 프로토콜
    HEADER_CHECKSUM(10, 2),      // 헤더 체크섬
    SOURCE_IP(12, 4),            // 출발지 IP 주소
    DESTINATION_IP(16, 4);       // 목적지 IP 주소

    private final int startIndex;
    private final int length;

    IPHeaderField(int startIndex, int length) {
        this.startIndex = startIndex;
        this.length = length;
    }

    public int getStartIndex(int totalLength) {
        return startIndex >= 0 ? startIndex : (totalLength + startIndex);
    }

    public int getEndIndex() {
        return (startIndex + length) - 1;
    }

    public int getLength() {
        return length;
    }
}