package com.github.software_development_methodology_study.core.dto;

import com.github.software_development_methodology_study.core.context.NicContext;

import java.util.concurrent.atomic.AtomicReference;

public record ActivateNic(AtomicReference<NicContext> nic) {
}
