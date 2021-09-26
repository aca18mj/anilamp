/**
 * A rectangle with a hole in the middle (window)
 * It's built using 12 triangles
 */

public class SquareWithHole {
    public static final float[] vertices = {      // position, colour, tex coords
            -0.5f, 0.0f, -0.5f,   0.0f, 1.0f, 0.0f,  0.0f, 1.0f, //1st vertical line
            -0.5f, 0.0f, -0.2f,   0.0f, 1.0f, 0.0f,  0.0f, 0.7f,
            -0.5f, 0.0f,  0.5f,   0.0f, 1.0f, 0.0f,  0.0f, 0.0f,
            -0.3f, 0.0f, -0.5f,   0.0f, 1.0f, 0.0f,  0.2f, 1.0f, //2nd vertical line
            -0.3f, 0.0f, -0.2f,   0.0f, 1.0f, 0.0f,  0.2f, 0.7f,
            -0.3f, 0.0f,  0.2f,   0.0f, 1.0f, 0.0f,  0.2f, 0.3f,
            -0.3f, 0.0f,  0.5f,   0.0f, 1.0f, 0.0f,  0.2f, 0.0f,
             0.3f, 0.0f, -0.5f,   0.0f, 1.0f, 0.0f,  0.8f, 1.0f, //3rd vertical line
             0.3f, 0.0f, -0.2f,   0.0f, 1.0f, 0.0f,  0.8f, 0.7f,
             0.3f, 0.0f,  0.2f,   0.0f, 1.0f, 0.0f,  0.8f, 0.3f,
             0.3f, 0.0f,  0.5f,   0.0f, 1.0f, 0.0f,  0.8f, 0.0f,
             0.5f, 0.0f, -0.5f,   0.0f, 1.0f, 0.0f,  1.0f, 1.0f, //4th vertical line
             0.5f, 0.0f, -0.2f,   0.0f, 1.0f, 0.0f,  1.0f, 0.7f,
             0.5f, 0.0f,  0.5f,   0.0f, 1.0f, 0.0f,  1.0f, 0.0f
    };

    public static final int[] indices = {
            0, 1, 4,
            0, 4, 3,
            1, 2, 4,
            2, 6, 4,
            3, 4, 7,
            4, 8, 7,
            5, 6, 9,
            6, 10, 9,
            7, 8, 11,
            8, 12, 11,
            8, 13, 12,
            8, 10, 13
    };
}
