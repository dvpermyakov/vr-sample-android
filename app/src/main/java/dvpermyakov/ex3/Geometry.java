package dvpermyakov.ex3;

/**
 * Created by dvpermyakov on 07.11.2016.
 */
public class Geometry {
    public static final float[] VERTICES = {
        // top triangle
        -0.5f, -0.5f, 0.0f,
        -0.5f, 0.5f, 0.0f,
        0.5f, 0.5f, 0.0f,

        // bottom triangle
        -0.5f, -0.5f, 0.0f,
        0.5f, 0.5f, 0.0f,
        0.5f, -0.5f, 0.0f,
    };

    public static final float[] TEX_COORDINATES = {
        // top triangle
        0.0f, 0.0f,
        0.0f, 1.0f,
        1.0f, 1.0f,

        // bottom triangle
        0.0f, 0.0f,
        1.0f, 1.0f,
        1.0f, 0.0f,
    };
}
