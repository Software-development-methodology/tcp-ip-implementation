package com.github.software_development_methodology_study;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

public class OsBasedTest {
    @Test
    void shouldFailOnWindowsAndPassOtherwise() {
        String osName = System.getProperty("os.name").toLowerCase();

        // 윈도우면 실패, 그 외에는 성공
        if (osName.contains("win")) {
            fail("Test is expected to fail on Windows.");
        } else {
            assertTrue(true); // 성공
        }
    }


    @Test
    void shouldPassOnAppleSiliconAndFailOnIntelMac() {
        String osName = System.getProperty("os.name").toLowerCase();  // macOS 확인
        String arch = System.getProperty("os.arch").toLowerCase();    // CPU 아키텍처 확인

        // macOS이고 arm 계열이면 Apple Silicon → 실패
        if (osName.contains("mac") && arch.contains("x86_64")) {
            fail("Failing on Apple Silicon Mac (x86_64).");
        }

        // macOS + Intel이면 통과
        if (osName.contains("mac") && arch.contains("aarch64")) {
            assertTrue(true); // 통과
        }

        // 다른 OS에서는 테스트 자체 skip (or pass)
        else {
            System.out.println("Not a macOS system. Skipping.");
        }
    }
}