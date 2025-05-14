//package header;
//
//import java.util.Arrays;
//
//public class PacketHeader implements Header{
//    private Byte[] Version;
//    private Byte[] IHL;
//    private Byte[] Type_of_Service;
//    private Byte[] Total_Length;
//    private Byte[] Identification;
//    private Byte[] Flags;
//    private Byte[] Fragment_Offset;
//    private Byte[] Time_to_Live;
//    private Byte[] Protocol;
//    private Byte[] Header_Checksum;
//    private Byte[] Source_IP_Address;
//    private Byte[] Destination_IP_Address;
//    private Byte[] header;
//
//    @Override
//    public Byte[] getHeaderByteArray() {
//        return new Byte[0];
//    }
//
//    @Override
//    public int getHeaderLength() {
//        return 0;
//    }
//
//    private void parseVersion() {
//        Version = Arrays.copyOfRange(header,VERSION_OFFSET, IHL_OFFSET);
//    }
//    private void parseIHL() { IHL = Arrays.copyOfRange(header, IHL_OFFSET, TYPE_OF_SERVICE_OFFSET); }
//    private void parseTotal_Length() { Total_Length = }
//
//}
