package com.devyk.aveditor.video.filter.gpuimage.base

import android.content.Context
import android.graphics.PointF
import android.opengl.GLES20
import com.devyk.aveditor.utils.OpenGLUtils
import com.devyk.aveditor.utils.Rotation
import com.devyk.aveditor.utils.TextureRotationUtil
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import java.util.*

/**
 * <pre>
 *     author  : devyk on 2020-08-10 20:09
 *     blog    : https://juejin.im/user/578259398ac2470061f3a3fb/posts
 *     github  : https://github.com/yangkun19921001
 *     mailbox : yang1001yk@gmail.com
 *     desc    : This is GPUImageFilter
 * </pre>
 */

open class GPUImageFilter @JvmOverloads constructor(
    public var context: Context,
    private val mVertexShader: String = NO_FILTER_VERTEX_SHADER,
    private val mFragmentShader: String = NO_FILTER_FRAGMENT_SHADER
) : GPUImageFBOFilter() {

    private val mRunOnDraw: LinkedList<Runnable>
    public var program: Int = 0
    var attribPosition: Int = 0
        protected set
    var uniformTexture: Int = 0
        protected set
    var attribTextureCoordinate: Int = 0
        protected set

    var intputWidth: Int = 0
        protected set
    var intputHeight: Int = 0
        protected set
    var isInitialized: Boolean = false
        protected set
    protected var mGLCubeBuffer: FloatBuffer
    protected var mGLTextureBuffer: FloatBuffer
    protected var mOutputWidth: Int = 0
    protected var mOutputHeight: Int = 0

    var beautyLevel = 3

    private var mContext: Context? = null


//    //FBO id
//    protected var mFrameBuffers: IntArray? = null
//    //fbo 纹理id
//    protected var mFrameBufferTextures: IntArray? = null

    init {
        mContext = context
        mRunOnDraw = LinkedList()
        mGLCubeBuffer = ByteBuffer.allocateDirect(TextureRotationUtil.CUBE.size * 4)
            .order(ByteOrder.nativeOrder())
            .asFloatBuffer()
        mGLCubeBuffer.put(TextureRotationUtil.CUBE).position(0)

        mGLTextureBuffer = ByteBuffer.allocateDirect(TextureRotationUtil.TEXTURE_NO_ROTATION.size * 4)
            .order(ByteOrder.nativeOrder())
            .asFloatBuffer()
        mGLTextureBuffer.put(TextureRotationUtil.getRotation(Rotation.ROTATION_180, false, true)).position(0)
    }

    open fun init() {
        onInit()
        isInitialized = true
        onInitialized()
    }

    protected open fun onInit() {
        program = OpenGLUtils.loadProgram(mVertexShader, mFragmentShader)
        attribPosition = GLES20.glGetAttribLocation(program, "position")
        uniformTexture = GLES20.glGetUniformLocation(program, "inputImageTexture")
        attribTextureCoordinate = GLES20.glGetAttribLocation(
            program,
            "inputTextureCoordinate"
        )
        isInitialized = true
    }

    protected open fun onInitialized() {

    }

    public fun isNeedInit(){
        if (!isInitialized)
            init()
    }

    fun destroy() {
        isInitialized = false
        GLES20.glDeleteProgram(program)
        destroyFrameBuffers()
        onDestroy()
    }

    protected open fun onDestroy() {
    }

    open fun onInputSizeChanged(width: Int, height: Int) {
        intputWidth = width
        intputHeight = height
        super.onReady(width, height)

    }

    open fun onDrawFrame(
        textureId: Int, cubeBuffer: FloatBuffer,
        textureBuffer: FloatBuffer
    ): Int? {
        super.onDraw()
        GLES20.glUseProgram(program)
        runPendingOnDrawTasks()
        if (!isInitialized) {
            return textureId
        }

        cubeBuffer.position(0)
        GLES20.glVertexAttribPointer(attribPosition, 2, GLES20.GL_FLOAT, false, 0, cubeBuffer)
        GLES20.glEnableVertexAttribArray(attribPosition)
        textureBuffer.position(0)
        GLES20.glVertexAttribPointer(
            attribTextureCoordinate, 2, GLES20.GL_FLOAT, false, 0,
            textureBuffer
        )
        GLES20.glEnableVertexAttribArray(attribTextureCoordinate)
        if (textureId != -1) {
            GLES20.glActiveTexture(GLES20.GL_TEXTURE0)
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureId)
            GLES20.glUniform1i(uniformTexture, 0)
        }
        onDrawArraysPre()
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4)
        GLES20.glDisableVertexAttribArray(attribPosition)
        GLES20.glDisableVertexAttribArray(attribTextureCoordinate)
        onDrawArraysAfter()
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0)
        //返回fbo的纹理id
        return getFBOTextureId()
    }

    open fun onDrawFrame(textureId: Int): Int? {
        super.onDraw()
//        mFrameBuffers?.get(0)?.let { GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, it) }

        GLES20.glUseProgram(program)
        runPendingOnDrawTasks()
        if (!isInitialized)
            return OpenGLUtils.NOT_INIT

        mGLCubeBuffer.position(0)
        GLES20.glVertexAttribPointer(attribPosition, 2, GLES20.GL_FLOAT, false, 0, mGLCubeBuffer)
        GLES20.glEnableVertexAttribArray(attribPosition)
        mGLTextureBuffer.position(0)
        GLES20.glVertexAttribPointer(
            attribTextureCoordinate, 2, GLES20.GL_FLOAT, false, 0,
            mGLTextureBuffer
        )
        GLES20.glEnableVertexAttribArray(attribTextureCoordinate)

        if (textureId != OpenGLUtils.NO_TEXTURE) {
            GLES20.glActiveTexture(GLES20.GL_TEXTURE0)
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureId)
            GLES20.glUniform1i(uniformTexture, 0)
        }
        onDrawArraysPre()
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4)
        GLES20.glDisableVertexAttribArray(attribPosition)
        GLES20.glDisableVertexAttribArray(attribTextureCoordinate)
        onDrawArraysAfter()
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0)
        //返回fbo的纹理id
        return getFBOTextureId()
    }

    protected open fun onDrawArraysPre() {}
    protected open fun onDrawArraysAfter() {}

    protected fun runPendingOnDrawTasks() {
        while (!mRunOnDraw.isEmpty()) {
            mRunOnDraw.removeFirst().run()
        }
    }


    protected fun setInteger(location: Int, intValue: Int) {
        runOnDraw(Runnable { GLES20.glUniform1i(location, intValue) })
    }

    open fun setFloat(location: Int, floatValue: Float) {
        runOnDraw(Runnable { GLES20.glUniform1f(location, floatValue) })
    }

    protected fun setFloatVec2(location: Int, arrayValue: FloatArray) {
        runOnDraw(Runnable { GLES20.glUniform2fv(location, 1, FloatBuffer.wrap(arrayValue)) })
    }

    protected fun setFloatVec3(location: Int, arrayValue: FloatArray) {
        runOnDraw(Runnable { GLES20.glUniform3fv(location, 1, FloatBuffer.wrap(arrayValue)) })
    }

    protected fun setFloatVec4(location: Int, arrayValue: FloatArray) {
        runOnDraw(Runnable { GLES20.glUniform4fv(location, 1, FloatBuffer.wrap(arrayValue)) })
    }

    protected fun setFloatArray(location: Int, arrayValue: FloatArray) {
        runOnDraw(Runnable { GLES20.glUniform1fv(location, arrayValue.size, FloatBuffer.wrap(arrayValue)) })
    }

    protected fun setPoint(location: Int, point: PointF) {
        runOnDraw(Runnable {
            val vec2 = FloatArray(2)
            vec2[0] = point.x
            vec2[1] = point.y
            GLES20.glUniform2fv(location, 1, vec2, 0)
        })
    }

    protected fun setUniformMatrix3f(location: Int, matrix: FloatArray) {
        runOnDraw(Runnable { GLES20.glUniformMatrix3fv(location, 1, false, matrix, 0) })
    }

    protected fun setUniformMatrix4f(location: Int, matrix: FloatArray) {
        runOnDraw(Runnable { GLES20.glUniformMatrix4fv(location, 1, false, matrix, 0) })
    }

    protected fun runOnDraw(runnable: Runnable) {
        synchronized(mRunOnDraw) {
            mRunOnDraw.addLast(runnable)
        }
    }

    fun onDisplaySizeChanged(width: Int, height: Int) {
        mOutputWidth = width
        mOutputHeight = height
    }

    companion object {
        val NO_FILTER_VERTEX_SHADER = "" +
                "attribute vec4 position;\n" +
                "attribute vec4 inputTextureCoordinate;\n" +
                " \n" +
                "varying vec2 textureCoordinate;\n" +
                " \n" +
                "void main()\n" +
                "{\n" +
                "    gl_Position = position;\n" +
                "    textureCoordinate = inputTextureCoordinate.xy;\n" +
                "}"
        val NO_FILTER_FRAGMENT_SHADER = "" +
                "varying highp vec2 textureCoordinate;\n" +
                " \n" +
                "uniform sampler2D inputImageTexture;\n" +
                " \n" +
                "void main()\n" +
                "{\n" +
                "     gl_FragColor = texture2D(inputImageTexture, textureCoordinate);\n" +
                "}"
    }
}