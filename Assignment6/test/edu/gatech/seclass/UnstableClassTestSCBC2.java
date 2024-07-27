package edu.gatech.seclass;

import org.junit.jupiter.api.Test;

public class UnstableClassTestSCBC2 {

    @Test
    public void testRevealFault() {
        UnstableClass.unstableMethod2(0, 0);
    }
}