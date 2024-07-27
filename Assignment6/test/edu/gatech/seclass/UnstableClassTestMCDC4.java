package edu.gatech.seclass;

import org.junit.jupiter.api.Test;

public class UnstableClassTestMCDC4 {
    @Test
    public void testUnstableMethod4Case1() {
        UnstableClass.unstableMethod4(true, 0, 0, 0);
    }

    @Test
    public void testUnstableMethod4Case2() {
        UnstableClass.unstableMethod4(false, 0, 1, 1);
    }

    @Test
    public void testUnstableMethod4Case3() {
        UnstableClass.unstableMethod4(false, 1, 0, 0);
    }

    @Test
    public void testUnstableMethod4Case4() {
        UnstableClass.unstableMethod4(false, 0, -1, 1);
    }

    @Test
    public void testUnstableMethod4Case5() {
        UnstableClass.unstableMethod4(false, 0, 1, 0);
    }

    @Test
    public void testUnstableMethod4Case6() {
        UnstableClass.unstableMethod4(false, 2, 1, 5);
    }

}