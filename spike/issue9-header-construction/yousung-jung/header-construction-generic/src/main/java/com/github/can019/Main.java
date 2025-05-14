package com.github.can019;

import com.github.can019.core.data.chunk.Chunk;
import com.github.can019.core.data.chunk.header.EmptyHeader;
import com.github.can019.core.data.chunk.payload.Payload;
import com.github.can019.core.layer.EthernetLayer;

import java.nio.charset.StandardCharsets;

public class Main {
    public static void main(String[] args) {
        Payload payload = new Payload("b".getBytes(StandardCharsets.UTF_8));
        Chunk<EmptyHeader> niChunk = new Chunk<>(EmptyHeader.getInstance(), payload);

        EthernetLayer layer = new EthernetLayer(null);
        layer.receive(niChunk);
    }
}