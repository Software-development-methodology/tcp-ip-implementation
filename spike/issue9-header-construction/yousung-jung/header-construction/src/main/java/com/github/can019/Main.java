package com.github.can019;

import com.github.can019.core.data.Chunk;
import com.github.can019.core.layer.implement.EthernetLayer;
import com.github.can019.core.layer.implement.InternetLayer;

public class Main {
    public static void main(String[] args) {
        EthernetLayer ethernetLayer = new EthernetLayer();
        InternetLayer internetLayer = new InternetLayer();


        ethernetLayer.setUpperLayer(internetLayer);
        internetLayer.setLowerLayer(ethernetLayer);


        ethernetLayer.receive(new Chunk());
    }
}