package edu.gatech.seclass;

import org.junit.Test;

public class UnstableClassTestBC1 {
    @Test
    public void testBranchWithoutException() {
        UnstableClass.unstableMethod1(5, 2);
    }

    @Test
    public void testBranchWithException() {
        UnstableClass.unstableMethod1(-1, 0);
    }
}
