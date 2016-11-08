package dvpermyakov.ex3.Shaders;

import android.content.Context;
import android.opengl.GLES20;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by dvpermyakov on 07.11.2016.
 */
public class ShaderProgram {
    private Context context;
    private int program;

    public ShaderProgram(Context context) {
        this.context = context;
        program = GLES20.glCreateProgram();
    }

    public void attachShader(int type, int resId) {
        int shader = loadShader(type, resId);
        GLES20.glAttachShader(program, shader);
    }

    public void linkProgram() {
        GLES20.glLinkProgram(program);
    }

    public int getAttributeLocation(String name) {
        return GLES20.glGetAttribLocation(program, name);
    }

    public int getUniformLocation(String name) {
        return GLES20.glGetUniformLocation(program, name);
    }

    public void useProgram() {
        GLES20.glUseProgram(program);
    }

    private int loadShader(int type, int resId) {
        int shader = GLES20.glCreateShader(type);
        GLES20.glShaderSource(shader, readFile(resId));
        GLES20.glCompileShader(shader);

        final int[] compileStatus = new int[1];
        GLES20.glGetShaderiv(shader, GLES20.GL_COMPILE_STATUS, compileStatus, 0);

        if (compileStatus[0] == 0) {
            Log.e("Shader", "Error compiling shader: " + GLES20.glGetShaderInfoLog(shader));
            GLES20.glDeleteShader(shader);
            shader = 0;
        }
        if (shader == 0) {
            throw new RuntimeException("Error creating shader.");
        }

        return shader;
    }

    private String readFile(int resId) {
        InputStream inputStream = context.getResources().openRawResource(resId);
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line).append("\n");
            }
            reader.close();
            return sb.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
