package org.solovyev.android.views.dragbutton;

import android.graphics.PointF;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

final class Drag {

    public static final String TAG = "Drag";

    private Drag() {
    }

    /**
     * @param l    first number
     * @param sign sign
     * @param r    second number
     * @return sum or difference of two numbers (supposed: null = 0)
     */
    public static double sumUp(Double l, int sign, Double r) {
        double result = 0d;
        if (l != null && r != null) {
            result = l + sign * r;
        } else if (l != null) {
            result = l;
        } else if (r != null) {
            result = sign * r;
        }
        return result;
    }

    /**
     * @param l fist number
     * @param r second number
     * @return difference of two numbers (supposed: null = 0)
     */
    public static double subtract(Double l, Double r) {
        return sumUp(l, -1, r);
    }

    /**
     * Method tests if first value is more than second with specified precision
     *
     * @param d1        first value to compare
     * @param d2        second value for compare
     * @param precision number of digits after dot
     * @return 'true' if first value is more than second with specified precision
     */
    public static boolean more(double d1, double d2, int precision) {
        return d1 > d2 + getMaxPreciseAmount(precision);
    }

    private static double getMaxPreciseAmount(int precision) {
        return Math.pow(0.1d, precision) / 2;
    }

    public static double round(@NonNull Double value, int precision) {
        double factor = Math.pow(10, precision);
        return ((double) Math.round(value * factor)) / factor;
    }

    public static float getDistance(@NonNull PointF startPoint,
                                    @NonNull PointF endPoint) {
        return getNorm(subtract(endPoint, startPoint));
    }

    public static PointF subtract(@NonNull PointF p1, @NonNull PointF p2) {
        return new PointF(p1.x - p2.x, p1.y - p2.y);
    }

    public static PointF sum(@NonNull PointF p1, @NonNull PointF p2) {
        return new PointF(p1.x + p2.x, p1.y + p2.y);
    }

    public static float getNorm(@NonNull PointF point) {
        //noinspection SuspiciousNameCombination
        return (float) Math.pow(Math.pow(point.x, 2) + Math.pow(point.y, 2), 0.5);
    }

    public static float getAngle(@NonNull PointF startPoint,
                                 @NonNull PointF axisEndPoint,
                                 @NonNull PointF endPoint,
                                 @Nullable MutableBoolean left) {
        final PointF axisVector = subtract(axisEndPoint, startPoint);
        final PointF vector = subtract(endPoint, startPoint);

        double a_2 = Math.pow(getDistance(vector, axisVector), 2);
        double b = getNorm(vector);
        double b_2 = Math.pow(b, 2);
        double c = getNorm(axisVector);
        double c_2 = Math.pow(c, 2);

        if (left != null) {
            left.value = axisVector.x * vector.y - axisVector.y * vector.x < 0;
        }

        return (float) Math.acos((-a_2 + b_2 + c_2) / (2 * b * c));
    }

    @NonNull
    public static String emptyIfNull(@Nullable String s) {
        return defaultIfNull(s, "");
    }

    @NonNull
    public static String defaultIfNull(@Nullable String s, @NonNull String def) {
        return s == null ? def : s;
    }
}