public class Creator {
    public static Byte[] createEthernetHeader() {   // by GPT
        return new Byte[] {
            // Header (14 bytes)
            (byte) 0xAA, (byte) 0xBB, (byte) 0xCC, (byte) 0xDD, (byte) 0xEE, (byte) 0xFF,
            (byte) 0x11, (byte) 0x22, (byte) 0x33, (byte) 0x44, (byte) 0x55, (byte) 0x66,
            (byte) 0x08, (byte) 0x00
        };
    }

    public static Byte[] createFullEthernetFrame() {
        return new Byte[] {
            // Header (14 bytes)
            (byte) 0xAA, (byte) 0xBB, (byte) 0xCC, (byte) 0xDD, (byte) 0xEE, (byte) 0xFF,
            (byte) 0x11, (byte) 0x22, (byte) 0x33, (byte) 0x44, (byte) 0x55, (byte) 0x66,
            (byte) 0x08, (byte) 0x00,

            // Payload (20 bytes)
            (byte) 0x01, (byte) 0x02, (byte) 0x03, (byte) 0x04, (byte) 0x05,
            (byte) 0x06, (byte) 0x07, (byte) 0x08, (byte) 0x09, (byte) 0x0A,
            (byte) 0x0B, (byte) 0x0C, (byte) 0x0D, (byte) 0x0E, (byte) 0x0F,
            (byte) 0x10, (byte) 0x11, (byte) 0x12, (byte) 0x13, (byte) 0x14
        };
    }

    public static Byte[] createEthernetPayload() {
        return new Byte[] {
            // Payload (20 bytes)
            (byte) 0x01, (byte) 0x02, (byte) 0x03, (byte) 0x04, (byte) 0x05,
            (byte) 0x06, (byte) 0x07, (byte) 0x08, (byte) 0x09, (byte) 0x0A,
            (byte) 0x0B, (byte) 0x0C, (byte) 0x0D, (byte) 0x0E, (byte) 0x0F,
            (byte) 0x10, (byte) 0x11, (byte) 0x12, (byte) 0x13, (byte) 0x14
        };
    }
}
