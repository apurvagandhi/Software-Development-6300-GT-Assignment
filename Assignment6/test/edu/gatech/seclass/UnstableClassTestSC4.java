package edu.gatech.seclass;

import org.junit.Test;

public class UnstableClassTestSC4 {
    @Test
    public void testUnstableMethod4AsTrue() {
        UnstableClass.unstableMethod4(true, 0, 0, 0);
    }

    @Test
    public void testUnstableMethod4BsZero() {
        UnstableClass.unstableMethod4(false, 0, 1, 1);
    }

    @Test
    public void testUnstableMethod4CNonPositive() {
        UnstableClass.unstableMethod4(false, 1, -1, 0);
    }
}
