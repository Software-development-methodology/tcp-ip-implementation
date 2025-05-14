public class Main {
    public static void main(String[] args) {

        System.out.println("Test");

    }
}

class ChunkFactory {

    public static Chunk wrap(Chunk chunk, String protocol) {
        switch (protocol) {
            case "TCP":
                return new Segment(chunk);
            case "IP":
                return new Packet(chunk);
            case "Ethernet":
                return new Frame(chunk);
        }
    }
}

public class TransportLayer extends Layer {
    @Override
    public void send(Chunk chunk) {
        System.out.println("[TransportLayer] send");
        Chunk next = ChunkFactory.wrap(chunk, "IP");
        lowerLayer.send(next);
    }

    @Override
    public void receive(Chunk chunk) {
        System.out.println("[TransportLayer] receive");
        upperLayer.receive(chunk);
    }
}
