package com.github.software_development_methodology_study.core.layer;

import com.github.software_development_methodology_study.core.data.chunk.Chunk;
import com.github.software_development_methodology_study.core.data.chunk.header.EmptyHeader;
import com.github.software_development_methodology_study.core.data.chunk.header.Header;
import org.pcap4j.core.PcapNetworkInterface;

public class TestLayer2 extends Layer<EmptyHeader>{
    public Header receivedHeader;
    public Header newHeader;

    @Override
    public void receive(Chunk<Header> chunk, PcapNetworkInterface nic) {
        this.upperLayer.receive(chunk, nic);
    }

    @Override
    public void send(Chunk<Header> chunk, PcapNetworkInterface nic) {

    }
}
