package com.github.software_development_methodology_study.core.layer;

import com.github.software_development_methodology_study.core.data.chunk.Chunk;
import com.github.software_development_methodology_study.core.data.chunk.header.Header;
import com.github.software_development_methodology_study.core.data.chunk.header.PacketHeader;

public class InternetLayer extends Layer<PacketHeader> {
    @Override
    public void receive(Chunk<Header> chunk) {

    }

    @Override
    public void send(Chunk<Header> chunk) {

    }
}
