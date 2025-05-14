package layer;

import chunk.Chunk;

public class MockNetworkInterfaceLayer extends Layer {
    @Override
    public void send(Chunk chunk) {
        byte[] chunkBytes = chunk.toBytes();

        System.out.println("[MockLayer] 보내는 Chunk:");
        System.out.println("- Header: " + chunk.getHeader().toString());
        System.out.println("- Payload: " + new String(chunk.getPayload()));
    }

    @Override
    public void receive(Chunk chunk) {
        // 필요시 구현
    }
}
