package com.github.software_development_methodology_study;

import com.github.software_development_methodology_study.core.layer.NetworkInterfaceLayer;

public class Main {
    public static void main(String[] args) {
        NetworkInterfaceLayer niLayer = NetworkInterfaceLayer.getInstance();
        niLayer.run();
    }
}