package edu.gatech.seclass;

import org.junit.jupiter.api.Test;

public class UnstableClassTestBC2 {

    @Test
    public void testPositiveX() {
        UnstableClass.unstableMethod2(1, 10);
    }

    @Test
    public void testNegativeX() {
        UnstableClass.unstableMethod2(-1, 1);
    }

    @Test
    public void testZeroX() {
        UnstableClass.unstableMethod2(0, 1);
    }
}