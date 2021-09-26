package gmaths;

/**
 * Linear interpolation between two points
 */
public class Lerp {

    /**
     * Returns value between two points given the fraction
     * @param a starting value
     * @param b end value
     * @param f fraction
     * @return number between a and b
     */
    public static float lerp(float a, float b, float f) {
        return (a * (1.0f - f)) + (b * f);
    }
}
