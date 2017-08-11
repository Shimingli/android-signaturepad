package com.github.gcacace.signaturepad;

import android.opengl.GLES20;
import android.opengl.GLSurfaceView;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * Created by shiming on 2017/8/8.
 */

public class MyGL implements GLSurfaceView.Renderer {
    /**
     * 当创建 GLSurfaceView时,系统调用这个方法.使用这个方法去执行只需要发生一次的动作,
     * 例如设置OpenGL环境参数或者初始化OpenGL graphic 对象.
     * @param gl10
     * @param eglConfig
     */
    @Override
    public void onSurfaceCreated(GL10 gl10, EGLConfig eglConfig) {
        GLES20.glClearColor(1.0f, 1.0f, 1.0f, 1.0f);
    }

    /**
     * 改变布局
     * 当 GLSurfaceView  几何学发生改变时系统调用这个方法.包括 GLSurfaceView
     * 的大小发生改变或者横竖屏发生改变.使用这个方法去响应GLSurfaceView 容器的改变.
     * @param gl10
     * @param width
     * @param height
     */
    @Override
    public void onSurfaceChanged(GL10 gl10, int width, int height) {
        GLES20.glViewport(0, 0, width, height);
        float ratio = (float) width / height;
    }

    /**
     * 当系统每一次重画 GLSurfaceView 时调用.使用这个方法去作为主要的绘制和重新绘制graphic  对象的执行点.
     * @param gl10
     */
    @Override
    public void onDrawFrame(GL10 gl10) {
        System.out.println("shiming gllo");
        DrawScene(gl10);

    }
    float vertexArray[] = {
            -0.8f, -0.4f * 1.732f, 0.0f,
            -0.4f, 0.4f * 1.732f, 0.0f,
            0.0f, -0.4f * 1.732f, 0.0f,
            0.4f, 0.4f * 1.732f, 0.0f,
    };
    int index=0;
    public void DrawScene(GL10 gl) {
//        super.DrawScene(gl);
        ByteBuffer vbb
                = ByteBuffer.allocateDirect(vertexArray.length*4);
        vbb.order(ByteOrder.nativeOrder());
        FloatBuffer vertex = vbb.asFloatBuffer();
        vertex.put(vertexArray);
        vertex.position(0);
        gl.glLoadIdentity();
        gl.glTranslatef(0, 0, -4);
        gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
        gl.glVertexPointer(3, GL10.GL_FLOAT, 0, vertex);
        index++;
        index%=10;
        switch(index){
            case 0:
            case 1:
            case 2:
                gl.glColor4f(1.0f, 0.0f, 0.0f, 1.0f);
                gl.glDrawArrays(GL10.GL_LINES, 0, 4);
                break;
            case 3:
            case 4:
            case 5:
                gl.glColor4f(0.0f, 1.0f, 0.0f, 1.0f);
                gl.glDrawArrays(GL10.GL_LINE_STRIP, 0, 4);
                break;
            case 6:
            case 7:
            case 8:
            case 9:
                gl.glColor4f(0.0f, 0.0f, 1.0f, 1.0f);
                gl.glDrawArrays(GL10.GL_LINE_LOOP, 0, 4);
                break;
        }
        gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
    }

}
