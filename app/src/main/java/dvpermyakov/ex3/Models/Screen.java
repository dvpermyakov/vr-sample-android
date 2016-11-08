package dvpermyakov.ex3.Models;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.opengl.GLUtils;
import android.opengl.Matrix;
import android.util.Log;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import dvpermyakov.ex3.Geometry;
import dvpermyakov.ex3.R;
import dvpermyakov.ex3.Shaders.ShaderProgram;

/**
 * Created by dvpermyakov on 07.11.2016.
 */
public class Screen {
    private static final float BETWEEN = 0.2f;

    private static ShaderProgram shaderProgram;
    private FloatBuffer vertexBuffer;
    private FloatBuffer texBuffer;
    private float[] modelMatrix;
    private int texture;
    private float length;
    private float radius;
    private int partitionAmount;
    private double zMax;

    public Screen(Context context, float length, float radius) {
        this.length = length;
        this.radius = radius;
        partitionAmount = 100;
        float[] vertices = new float[Geometry.VERTICES.length * partitionAmount];
        zMax = radius - Math.sqrt(radius * radius - length * length / 4);
        float currentX = Geometry.VERTICES[0] * length * (1.0f - BETWEEN);  // first float is x coordinate of left point
        float stepX = length * (1.0f - BETWEEN) / partitionAmount;  //  1.0 is length of geometry by x
        for (int k = 0; k < partitionAmount; k++) {
            for (int i = 0; i < Geometry.VERTICES.length; i++) {
                vertices[k * Geometry.VERTICES.length + i] = Geometry.VERTICES[i] * length * (1.0f - BETWEEN);
                if (i == 0 || i == 3 || i == 9) {
                    vertices[k * Geometry.VERTICES.length + i] = currentX;
                }
                if (i == 6 || i == 12 || i == 15) {
                    vertices[k * Geometry.VERTICES.length + i] = currentX + stepX;
                }
                if (i == 2 || i == 5 || i == 11) {
                    double currentAngle = currentX / length * Math.PI;
                    vertices[k * Geometry.VERTICES.length + i] = (float)(zMax * Math.cos(currentAngle));
                }
                if (i == 8 || i == 14 || i == 17) {
                    double currentAngle = (currentX + stepX) / length * Math.PI;
                    vertices[k * Geometry.VERTICES.length + i] = (float)(zMax * Math.cos(currentAngle));
                }
            }
            currentX += stepX;
        }

        float[] texCoordinates = new float[Geometry.TEX_COORDINATES.length * partitionAmount];
        currentX = 0.0f;
        stepX = 1.0f / partitionAmount;
        for (int k = 0; k < partitionAmount; k++) {
            for (int i = 0; i < Geometry.TEX_COORDINATES.length; i++) {
                texCoordinates[k * Geometry.TEX_COORDINATES.length + i] = Geometry.TEX_COORDINATES[i];
                if (i == 0 || i == 2 || i == 6) {
                    texCoordinates[k * Geometry.TEX_COORDINATES.length + i] = currentX;
                }
                if (i == 4 || i == 8 || i == 10) {
                    texCoordinates[k * Geometry.TEX_COORDINATES.length + i] = currentX + stepX;
                }
            }
            currentX += stepX;
        }
        modelMatrix = new float[16];

        ByteBuffer bbVertex = ByteBuffer.allocateDirect(vertices.length * partitionAmount * 4);
        bbVertex.order(ByteOrder.nativeOrder());
        vertexBuffer = bbVertex.asFloatBuffer();
        vertexBuffer.put(vertices);
        vertexBuffer.position(0);

        ByteBuffer bbTex = ByteBuffer.allocateDirect(texCoordinates.length * partitionAmount * 4);
        bbTex.order(ByteOrder.nativeOrder());
        texBuffer = bbTex.asFloatBuffer();
        texBuffer.put(texCoordinates);
        texBuffer.position(0);

        texture = loadTexture(context, R.drawable.lemur);
    }

    public void render(float[] projectionMatrix, float[] viewMatrix) {
        shaderProgram.useProgram();
        int positionParam = shaderProgram.getAttributeLocation("position");
        GLES20.glEnableVertexAttribArray(positionParam);
        GLES20.glVertexAttribPointer(positionParam, 3, GLES20.GL_FLOAT, false, 0, vertexBuffer);

        int texCoordinateParam = shaderProgram.getAttributeLocation("texCoordinate");
        GLES20.glEnableVertexAttribArray(texCoordinateParam);
        GLES20.glVertexAttribPointer(texCoordinateParam, 2, GLES20.GL_FLOAT, false, 0, texBuffer);

        int projectionMatrixParam = shaderProgram.getUniformLocation("projectionMatrix");
        GLES20.glUniformMatrix4fv(projectionMatrixParam, 1, false, projectionMatrix, 0);

        int viewMatrixParam = shaderProgram.getUniformLocation("viewMatrix");
        GLES20.glUniformMatrix4fv(viewMatrixParam, 1, false, viewMatrix, 0);

        int modelMatrixParam = shaderProgram.getUniformLocation("modelMatrix");
        GLES20.glUniformMatrix4fv(modelMatrixParam, 1, false, modelMatrix, 0);

        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, texture);
        int textureParam = shaderProgram.getUniformLocation("texture");
        GLES20.glUniform1i(textureParam, 0);

        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, 6 * partitionAmount);
    }

    public void place(int index) {
        Matrix.setIdentityM(modelMatrix, 0);

        double angle = 2.0 * Math.asin(length / (radius * 2.0f)) * index;
        angle *= 180.0 / Math.PI;
        angle -= 90.0;
        Matrix.rotateM(modelMatrix, 0, (float) angle, 0.0f, 1.0f, 0.0f);
        Matrix.translateM(modelMatrix, 0, (float) (radius - zMax), 0.0f, 0.0f);
        Matrix.rotateM(modelMatrix, 0, 90.0f, 0.0f, 1.0f, 0.0f);
    }

    public static void loadShaders(Context context) {
        shaderProgram = new ShaderProgram(context);
        shaderProgram.attachShader(GLES20.GL_VERTEX_SHADER, R.raw.vertex);
        shaderProgram.attachShader( GLES20.GL_FRAGMENT_SHADER, R.raw.fragment);
        shaderProgram.linkProgram();
    }

    private static int loadTexture(final Context context, final int resourceId) {
        final int[] textureHandle = new int[1];
        GLES20.glGenTextures(1, textureHandle, 0);

        if (textureHandle[0] != 0) {
            final BitmapFactory.Options options = new BitmapFactory.Options();
            options.inScaled = false;

            final Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), resourceId, options);

            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureHandle[0]);
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
            GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap, 0);

            bitmap.recycle();
        }
        if (textureHandle[0] == 0) {
            throw new RuntimeException("Error loading texture.");
        }
        return textureHandle[0];
    }
}
