package com.github.software_development_methodology_study.core.data.chunk.header;

public class PacketHeader extends Header{
    private Byte[] Version;
    private Byte[] IHL;
    private Byte[] Type_of_Service;
    private Byte[] Total_Length;
    private Byte[] Identification;
    private Byte[] Flags;
    private Byte[] Fragment_Offset;
    private Byte[] TTL;                 // Time_To_Live
    private Byte[] Protocol;
    private Byte[] Header_Checksum;
    private Byte[] Source_Address;
    private Byte[] Destination_Address;
    private Byte[] Options;
    private Byte[] Padding;

    public Byte[] getVersion() {
        return Version;
    }

    public void setVersion(Byte[] version) {
        Version = version;
    }

    public Byte[] getIHL() {
        return IHL;
    }

    public void setIHL(Byte[] IHL) {
        this.IHL = IHL;
    }

    public Byte[] getType_of_Service() {
        return Type_of_Service;
    }

    public void setType_of_Service(Byte[] type_of_Service) {
        Type_of_Service = type_of_Service;
    }

    public Byte[] getTotal_Length() {
        return Total_Length;
    }

    public void setTotal_Length(Byte[] total_Length) {
        Total_Length = total_Length;
    }

    public Byte[] getIdentification() {
        return Identification;
    }

    public void setIdentification(Byte[] identification) {
        Identification = identification;
    }

    public Byte[] getFlags() {
        return Flags;
    }

    public void setFlags(Byte[] flags) {
        Flags = flags;
    }

    public Byte[] getFragment_Offset() {
        return Fragment_Offset;
    }

    public void setFragment_Offset(Byte[] fragment_Offset) {
        Fragment_Offset = fragment_Offset;
    }

    public Byte[] getTTL() {
        return TTL;
    }

    public void setTTL(Byte[] TTL) {
        this.TTL = TTL;
    }

    public Byte[] getProtocol() {
        return Protocol;
    }

    public void setProtocol(Byte[] protocol) {
        Protocol = protocol;
    }

    public Byte[] getHeader_Checksum() {
        return Header_Checksum;
    }

    public void setHeader_Checksum(Byte[] header_Checksum) {
        Header_Checksum = header_Checksum;
    }

    public Byte[] getSource_Address() {
        return Source_Address;
    }

    public void setSource_Address(Byte[] source_Address) {
        Source_Address = source_Address;
    }

    public Byte[] getDestination_Address() {
        return Destination_Address;
    }

    public void setDestination_Address(Byte[] destination_Address) {
        Destination_Address = destination_Address;
    }

    public Byte[] getOptions() {
        return Options;
    }

    public void setOptions(Byte[] options) {
        Options = options;
    }

    public Byte[] getPadding() {
        return Padding;
    }

    public void setPadding(Byte[] padding) {
        Padding = padding;
    }


}
