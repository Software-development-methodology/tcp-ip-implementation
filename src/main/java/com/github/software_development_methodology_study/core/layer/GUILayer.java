package com.github.software_development_methodology_study.core.layer;

import com.github.software_development_methodology_study.core.data.chunk.Chunk;
import com.github.software_development_methodology_study.core.data.chunk.header.EmptyHeader;
import com.github.software_development_methodology_study.core.data.chunk.header.Header;

import java.util.Arrays;

public class GUILayer extends Layer<EmptyHeader> {
    @Override
    public void receive(Chunk<Header> chunk) {

    }

    @Override
    public void send(Chunk<Header> chunk) {
        System.out.println(Arrays.toString(chunk.getPayload().getBytes()));
    }
}
