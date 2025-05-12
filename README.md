### 🧪 Spike 목표
> TCP/IP 4계층 구현에서 Chunk 구조 잡기

### 🔨 작업 내용
- 각 Layer는 자신이 사용하는 Header만 Chunk에 주입
- Chunk 내에서 Payload, Header 핸들링
    - 수신 시 Chunk 내 Payload, Header 분리 작업 및 테스트
    - 송신 시 Chunk 내 Payload, Header 병합 작업 및 테스트
- Byte[] <-> long 구현 및 테스트

### 🤔 고민/이슈
- Layer에서 Header 구체 클래스의 메서드 사용 불가
    - Generic을 사용하면 될 것 같기두

### 🧹 기타
```java
public class MockLayer {
    private MockLayer upperLayer;
    private MockLayer lowerLayer;

    public void send(Chunk chunk) {
        chunk.mergeHeader();
        Header header = new FrameHeader();
        chunk.setHeader(header);
        /*
        헤더 세팅
         */
        lowerLayer.send(chunk);
    }

    public void receive(Chunk chunk) {
        Header header = new FrameHeader();
        chunk.setHeader(header);
        chunk.separateHeader();
        /*
        분리 된 헤더 처리
         */
        upperLayer.receive(chunk);
    }
}
```
