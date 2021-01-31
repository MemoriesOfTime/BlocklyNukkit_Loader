package com.blocklynukkit.loader.utils;

public class MathUtils {
    public static double toRadians(double x) {
        if (!Double.isInfinite(x) && x != 0.0D) {
            double xa = doubleHighPart(x);
            double xb = x - xa;
            double result = xb * 1.997844754509471E-9D + xb * 0.01745329052209854D + xa * 1.997844754509471E-9D + xa * 0.01745329052209854D;
            if (result == 0.0D) {
                result *= x;
            }

            return result;
        } else {
            return x;
        }
    }
    private static double doubleHighPart(double d) {
        if (d > -Double.longBitsToDouble(4503599627370496L) && d < Double.longBitsToDouble(4503599627370496L)) {
            return d;
        } else {
            long xl = Double.doubleToRawLongBits(d);
            xl &= -1073741824L;
            return Double.longBitsToDouble(xl);
        }
    }
}
