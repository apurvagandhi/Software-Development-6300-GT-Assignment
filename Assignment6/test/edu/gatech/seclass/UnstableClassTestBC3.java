package edu.gatech.seclass;

import org.junit.jupiter.api.Test;

public class UnstableClassTestBC3 {
    @Test
    public void testPositiveX() {
        UnstableClass.unstableMethod3(1, 1);
    }

    @Test
    public void testDivisionByZero() {
        UnstableClass.unstableMethod3(0, 0);
    }
}
