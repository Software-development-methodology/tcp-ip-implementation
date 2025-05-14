package com.github.software_development_methodology_study.core.layer;

import com.github.software_development_methodology_study.core.data.chunk.Chunk;
import com.github.software_development_methodology_study.core.data.chunk.header.EmptyHeader;
import com.github.software_development_methodology_study.core.data.chunk.header.Header;

public class TestLayer2 extends Layer<EmptyHeader>{
    public Header receivedHeader;
    public Header newHeader;

    @Override
    public void receive(Chunk<Header> chunk) {
        this.upperLayer.receive(chunk);
    }

    @Override
    public void send(Chunk<Header> chunk) {

    }


    public void process(Chunk<TestHeader> chunk) {

    }
}
