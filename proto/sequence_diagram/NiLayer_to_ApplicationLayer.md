# NiLayer to Application Layer
## Medtadata
- 작성자: 정유성
- 버전: v0.1
- 최초 작성: 2025-04-25 12:45
- 마지막 수정: 2025-04-25 12:45

## Description
- 이 다이어그램은 NiLayer에서 ApplicationLayer로 신호가 전달되는 전체 프로세스를 시각화한 것임.
- 각 레이어의 send() 호출 관계를 중심으로 TCP/IP 4 Layer 구조를 설명함

## Legend
- → : 호출(동기)
- alt : 이벤트 대기/조건 분기 표현

## Reference
- [NiLayer.listend() 호출 과정](./EntryPoint.md)

## Diagram
```mermaid
sequenceDiagram
    participant Thread1
    participant NiLayer
    participant ChildThread
    participant EthernetLayer
    participant NetworkLayer
    participant TransportLayer
    participant ApplicationLayer

    Thread1->>NiLayer: listen() 호출
    alt interrupt 또는 spin wait(while)
        NiLayer-->>Thread1: 이벤트 대기
        NiLayer->>NiLayer: (이벤트 발생)
        NiLayer->>ChildThread: spawn() 새로운 Thread 생성
        ChildThread->>EthernetLayer: send()
        EthernetLayer->>NetworkLayer: send()
        NetworkLayer->>TransportLayer: send()
        TransportLayer->>ApplicationLayer: send()
    end

```

