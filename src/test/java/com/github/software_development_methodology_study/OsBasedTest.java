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
}