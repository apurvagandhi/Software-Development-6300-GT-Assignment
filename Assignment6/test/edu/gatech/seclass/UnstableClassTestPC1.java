package edu.gatech.seclass;

import org.junit.jupiter.api.Test;

public class UnstableClassTestPC1 {
    @Test
    public void testPath1() {
        UnstableClass.unstableMethod1(1, 5);
    }

    @Test
    public void testPath2() {
        UnstableClass.unstableMethod1(-1, 1);
    }
}
