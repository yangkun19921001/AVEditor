package com.devyk.aveditor.video.filter

import android.content.Context
import android.opengl.GLES20
import com.devyk.aveditor.utils.OpenGLUtils
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer

/**
 * <pre>
 *     author  : devyk on 2020-08-08 13:14
 *     blog    : https://juejin.im/user/578259398ac2470061f3a3fb/posts
 *     github  : https://github.com/yangkun19921001
 *     mailbox : yang1001yk@gmail.com
 *     desc    : This is BaseFilter
 * </pre>
 */
public open class BaseFilter(context: Context?, vertexShaderID: Int, fragmentShaderId: Int) : IFilter {


    /**
     * 顶点 Buffer
     */
    protected var mGLVertexBuffer: FloatBuffer? = null
    /**
     * 物体纹理 Buffer
     */
    protected var mGLTextureBuffer: FloatBuffer? = null

    //顶点着色
    protected var mVertexShaderId: Int = 0
    //片段着色
    protected var mFragmentShaderId: Int = 0


    protected var mGLProgramId: Int = 0
    /**
     * 顶点着色器
     * attribute vec4 position;
     * 赋值给gl_Position(顶点)
     */
    protected var vPosition: Int = 0
    /**
     * varying vec2 textureCoordinate;
     */
    protected var vCoord: Int = 0


    /**
     * uniform mat4 vMatrix;
     */
    protected var vMatrix: Int = 0

    /**
     * 片元着色器
     * Samlpe2D 扩展 samplerExternalOES
     */
    protected var vTexture: Int = 0


    /**
     * 控件渲染宽高
     */
    protected var mSurfaceWidth: Int = 0
    protected var mSurfaceHeight: Int = 0


    init {
        this.mVertexShaderId = vertexShaderID
        this.mFragmentShaderId = fragmentShaderId
        // 4个点 x，y = 4*2 float 4字节 所以 4*2*4
        mGLVertexBuffer = ByteBuffer.allocateDirect(4 * 2 * 4)
            .order(ByteOrder.nativeOrder())
            .asFloatBuffer()
        mGLVertexBuffer?.clear()

        /**
         * 顶点坐标
         */
        val VERTEX = floatArrayOf(-1.0f, -1.0f, 1.0f, -1.0f, -1.0f, 1.0f, 1.0f, 1.0f)
        mGLVertexBuffer?.put(VERTEX)


        mGLTextureBuffer = ByteBuffer.allocateDirect(4 * 2 * 4)
            .order(ByteOrder.nativeOrder())
            .asFloatBuffer()
        mGLTextureBuffer?.clear()
        /**
         * 片元坐标
         */
        val TEXTURE = floatArrayOf(0.0f, 1.0f, 1.0f, 1.0f, 0.0f, 0.0f, 1.0f, 0.0f)
        mGLTextureBuffer?.put(TEXTURE)


        initilize(context)
        changeCoordinate()
    }

    /**
     * 提供子类修改坐标
     */
    protected open fun changeCoordinate() {


    }

    protected fun changeCoordinate(floatArray: FloatArray) {
        floatArray?.let {
            mGLTextureBuffer?.clear()
            mGLTextureBuffer?.put(floatArray)
        }
    }

    protected open fun initilize(context: Context?) {
        val vertexSharder = OpenGLUtils.readRawTextFile(context, mVertexShaderId)
        val framentShader = OpenGLUtils.readRawTextFile(context, mFragmentShaderId)
        mGLProgramId = OpenGLUtils.loadProgram(vertexSharder, framentShader)
        // 获得着色器中的 attribute 变量 position 的索引值
        vPosition = GLES20.glGetAttribLocation(mGLProgramId, "vPosition")
        vCoord = GLES20.glGetAttribLocation(
            mGLProgramId,
            "vCoord"
        )
        vMatrix = GLES20.glGetUniformLocation(
            mGLProgramId,
            "vMatrix"
        )
        // 获得Uniform变量的索引值
        vTexture = GLES20.glGetUniformLocation(
            mGLProgramId,
            "vTexture"
        )

    }


    override fun onReady(width: Int, height: Int) {
        this.mSurfaceWidth = width;
        this.mSurfaceHeight = height

    }

    override open fun onDrawFrame(textureId: Int): Int {
        //设置显示窗口
        GLES20.glViewport(0, 0, mSurfaceWidth, mSurfaceHeight)

        //使用着色器
        GLES20.glUseProgram(mGLProgramId)

        //传递坐标
        mGLVertexBuffer?.position(0)
        GLES20.glVertexAttribPointer(vPosition, 2, GLES20.GL_FLOAT, false, 0, mGLVertexBuffer)
        GLES20.glEnableVertexAttribArray(vPosition)

        mGLTextureBuffer?.position(0)
        GLES20.glVertexAttribPointer(vCoord, 2, GLES20.GL_FLOAT, false, 0, mGLTextureBuffer)
        GLES20.glEnableVertexAttribArray(vCoord)


        GLES20.glActiveTexture(GLES20.GL_TEXTURE0)
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureId)
        GLES20.glUniform1i(vTexture, 0)
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4)

        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0)
        return textureId
    }

    override open fun release() {
        GLES20.glDeleteProgram(mGLProgramId)
    }

    override fun getFData(): FloatBuffer? {
        return mGLTextureBuffer
    }

    override fun getVData(): FloatBuffer? {
        return mGLVertexBuffer
    }
}