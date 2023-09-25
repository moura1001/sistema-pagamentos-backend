package com.moura.sistemapagamentosbackend.util.math;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class DecimalOperations {
    private static final int SCALE = 2;
    private static final RoundingMode ROUNDING_MODE = RoundingMode.HALF_UP;

    public static BigDecimal subtract(BigDecimal minuend, BigDecimal subtrahend) {
        return minuend.subtract(subtrahend).setScale(SCALE, ROUNDING_MODE);
    }

    public static BigDecimal add(BigDecimal augend, BigDecimal addend) {
        return augend.add(addend).setScale(SCALE, ROUNDING_MODE);
    }
}
