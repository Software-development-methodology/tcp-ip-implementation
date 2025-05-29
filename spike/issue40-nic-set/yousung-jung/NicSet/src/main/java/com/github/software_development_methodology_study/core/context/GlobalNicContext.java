package com.github.software_development_methodology_study.core.context;

import org.pcap4j.core.PcapNetworkInterface;
import org.pcap4j.core.Pcaps;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public class GlobalNicContext {

    private static final List<PcapNetworkInterface> nicList;
    private static final AtomicReference<NicContext> currentContext = new AtomicReference<>();

    static {
        List<PcapNetworkInterface> interfaces;
        try {
            interfaces = Pcaps.findAllDevs();
        } catch (Exception e) {
            throw new RuntimeException("NIC 목록을 불러올 수 없습니다", e);
        }

        nicList = Collections.unmodifiableList(interfaces);
        if (!nicList.isEmpty()) {
            setCurrentContext(new NicContext(nicList.get(0)));
        }
    }

    public static List<PcapNetworkInterface> getNicList() {
        return nicList;
    }

    public static NicContext getCurrentContext() {
        return currentContext.get();
    }

    public static void setCurrentContext(NicContext context) {
        if (context == null) throw new IllegalArgumentException("NIC context is null");
        currentContext.set(context);
    }

    public static void setCurrentContextByIndex(int index) {
        if (index < 0 || index >= nicList.size()) throw new IndexOutOfBoundsException("NIC index out of range");
        setCurrentContext(new NicContext(nicList.get(index)));
    }
}

