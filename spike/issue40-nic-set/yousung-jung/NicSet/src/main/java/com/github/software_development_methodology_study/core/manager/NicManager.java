package com.github.software_development_methodology_study.core.manager;

import com.github.software_development_methodology_study.core.context.NicContext;
import org.pcap4j.core.PcapNetworkInterface;
import org.pcap4j.core.Pcaps;

import java.util.Collections;
import java.util.List;

public final class NicManager {
    private static final List<PcapNetworkInterface> nicList;
    private static PcapNetworkInterface currentContext;


    static {
        List<PcapNetworkInterface> interfaces;
        try {
            interfaces = Pcaps.findAllDevs();
        } catch (Exception e) {
            throw new RuntimeException("NIC 목록을 불러올 수 없습니다", e);
        }
        nicList = Collections.unmodifiableList(interfaces);

        if(!nicList.isEmpty()){
            currentContext = nicList.getFirst();
        }
    }

    private NicManager(){}


    public static void setCurrentContext(PcapNetworkInterface pcapNetworkInterface) {
        currentContext = pcapNetworkInterface;
    }

    public static PcapNetworkInterface getCurrentContext() {
        // currentContext.openLive()...
        return currentContext;
    }
}
