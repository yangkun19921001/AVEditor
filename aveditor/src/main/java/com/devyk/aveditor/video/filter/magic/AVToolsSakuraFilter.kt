package com.devyk.aveditor.video.filter.magic

import android.content.Context
import android.opengl.GLES20
import com.devyk.aveditor.R
import com.devyk.aveditor.utils.OpenGLUtils
import com.devyk.aveditor.video.filter.gpuimage.base.GPUImageFilter


import java.nio.ByteBuffer

class AVToolsSakuraFilter(context: Context) : GPUImageFilter(context,NO_FILTER_VERTEX_SHADER, OpenGLUtils.readRawTextFile(context,
    R.raw.romance)) {
    private val mToneCurveTexture = intArrayOf(-1)
    private var mToneCurveTextureUniformLocation: Int = 0
    private var mTexelHeightUniformLocation: Int = 0
    private var mTexelWidthUniformLocation: Int = 0

    override fun onDestroy() {
        super.onDestroy()
        GLES20.glDeleteTextures(1, mToneCurveTexture, 0)
        mToneCurveTexture[0] = -1
    }

    protected override fun onDrawArraysAfter() {
        if (mToneCurveTexture[0] != -1) {
            GLES20.glActiveTexture(GLES20.GL_TEXTURE3)
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0)
            GLES20.glActiveTexture(GLES20.GL_TEXTURE0)
        }
    }

    protected override fun onDrawArraysPre() {
        if (mToneCurveTexture[0] != -1) {
            GLES20.glActiveTexture(GLES20.GL_TEXTURE3)
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mToneCurveTexture[0])
            GLES20.glUniform1i(mToneCurveTextureUniformLocation, 3)
        }
    }

    override fun onInit() {
        super.onInit()
        mToneCurveTextureUniformLocation = GLES20.glGetUniformLocation(program, "curve")
        mTexelWidthUniformLocation = GLES20.glGetUniformLocation(program, "texelWidthOffset")
        mTexelHeightUniformLocation = GLES20.glGetUniformLocation(program, "texelHeightOffset")
    }

    override fun onInitialized() {
        super.onInitialized()
        runOnDraw(Runnable {
            GLES20.glGenTextures(1, mToneCurveTexture, 0)
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mToneCurveTexture[0])
            GLES20.glTexParameterf(
                GLES20.GL_TEXTURE_2D,
                GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR.toFloat()
            )
            GLES20.glTexParameterf(
                GLES20.GL_TEXTURE_2D,
                GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR.toFloat()
            )
            GLES20.glTexParameterf(
                GLES20.GL_TEXTURE_2D,
                GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE.toFloat()
            )
            GLES20.glTexParameterf(
                GLES20.GL_TEXTURE_2D,
                GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE.toFloat()
            )
            val arrayOfByte = ByteArray(1024)
            val arrayOfInt = intArrayOf(
                95,
                95,
                96,
                97,
                97,
                98,
                99,
                99,
                100,
                101,
                101,
                102,
                103,
                104,
                104,
                105,
                106,
                106,
                107,
                108,
                108,
                109,
                110,
                111,
                111,
                112,
                113,
                113,
                114,
                115,
                115,
                116,
                117,
                117,
                118,
                119,
                120,
                120,
                121,
                122,
                122,
                123,
                124,
                124,
                125,
                126,
                127,
                127,
                128,
                129,
                129,
                130,
                131,
                131,
                132,
                133,
                133,
                134,
                135,
                136,
                136,
                137,
                138,
                138,
                139,
                140,
                140,
                141,
                142,
                143,
                143,
                144,
                145,
                145,
                146,
                147,
                147,
                148,
                149,
                149,
                150,
                151,
                152,
                152,
                153,
                154,
                154,
                155,
                156,
                156,
                157,
                158,
                159,
                159,
                160,
                161,
                161,
                162,
                163,
                163,
                164,
                165,
                165,
                166,
                167,
                168,
                168,
                169,
                170,
                170,
                171,
                172,
                172,
                173,
                174,
                175,
                175,
                176,
                177,
                177,
                178,
                179,
                179,
                180,
                181,
                181,
                182,
                183,
                184,
                184,
                185,
                186,
                186,
                187,
                188,
                188,
                189,
                190,
                191,
                191,
                192,
                193,
                193,
                194,
                195,
                195,
                196,
                197,
                197,
                198,
                199,
                200,
                200,
                201,
                202,
                202,
                203,
                204,
                204,
                205,
                206,
                207,
                207,
                208,
                209,
                209,
                210,
                211,
                211,
                212,
                213,
                213,
                214,
                215,
                216,
                216,
                217,
                218,
                218,
                219,
                220,
                220,
                221,
                222,
                223,
                223,
                224,
                225,
                225,
                226,
                227,
                227,
                228,
                229,
                229,
                230,
                231,
                232,
                232,
                233,
                234,
                234,
                235,
                236,
                236,
                237,
                238,
                239,
                239,
                240,
                241,
                241,
                242,
                243,
                243,
                244,
                245,
                245,
                246,
                247,
                248,
                248,
                249,
                250,
                250,
                251,
                252,
                252,
                253,
                254,
                255,
                255,
                255,
                255,
                255,
                255,
                255,
                255,
                255,
                255,
                255,
                255,
                255,
                255,
                255,
                255,
                255,
                255,
                255,
                255,
                255,
                255,
                255,
                255,
                255,
                255
            )
            for (i in 0..255) {
                arrayOfByte[i * 4] = arrayOfInt[i].toByte()
                arrayOfByte[1 + i * 4] = arrayOfInt[i].toByte()
                arrayOfByte[2 + i * 4] = arrayOfInt[i].toByte()
                arrayOfByte[3 + i * 4] = arrayOfInt[i].toByte()
            }
            GLES20.glTexImage2D(
                GLES20.GL_TEXTURE_2D,
                0,
                GLES20.GL_RGBA,
                256,
                1,
                0,
                GLES20.GL_RGBA,
                GLES20.GL_UNSIGNED_BYTE,
                ByteBuffer.wrap(arrayOfByte)
            )
        })
    }

    override fun onInputSizeChanged(width: Int, height: Int) {
        super.onInputSizeChanged(width, height)
        GLES20.glUniform1f(mTexelWidthUniformLocation, 1.0f / width.toFloat())
        GLES20.glUniform1f(mTexelHeightUniformLocation, 1.0f / height.toFloat())
    }
}
