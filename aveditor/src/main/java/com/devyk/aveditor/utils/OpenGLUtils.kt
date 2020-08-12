package com.devyk.aveditor.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.opengl.GLES20
import android.opengl.GLUtils
import java.io.*

/**
 * <pre>
 *     author  : devyk on 2020-08-08 13:21
 *     blog    : https://juejin.im/user/578259398ac2470061f3a3fb/posts
 *     github  : https://github.com/yangkun19921001
 *     mailbox : yang1001yk@gmail.com
 *     desc    : This is OpenGLUtils
 * </pre>
 */
object OpenGLUtils {

    val NO_TEXTURE = -1
    val NOT_INIT = -1
    val ON_DRAWN = 1


    /**
     * 创建纹理并配置
     */
    fun glGenTextures(textures: IntArray) {
        //创建
        GLES20.glGenTextures(textures.size, textures, 0)
        //配置
        for (i in textures.indices) {
            // opengl的操作 面向过程的操作
            //bind 就是绑定 ，表示后续的操作就是在这一个 纹理上进行
            // 后面的代码配置纹理，就是配置bind的这个纹理
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textures[i])
            /**
             * 过滤参数
             * 当纹理被使用到一个比他大 或者比他小的形状上的时候 该如何处理
             */
            // 放大
            // GLES20.GL_LINEAR  : 使用纹理中坐标附近的若干个颜色，通过平均算法 进行放大
            // GLES20.GL_NEAREST : 使用纹理坐标最接近的一个颜色作为放大的要绘制的颜色
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_NEAREST)
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_NEAREST)

            /*设置纹理环绕方向*/
            //纹理坐标 一般用st表示，其实就是x y
            //纹理坐标的范围是0-1。超出这一范围的坐标将被OpenGL根据GL_TEXTURE_WRAP参数的值进行处理
            //GL_TEXTURE_WRAP_S, GL_TEXTURE_WRAP_T 分别为x，y方向。
            //GL_REPEAT:平铺
            //GL_MIRRORED_REPEAT: 纹理坐标是奇数时使用镜像平铺
            //GL_CLAMP_TO_EDGE: 坐标超出部分被截取成0、1，边缘拉伸
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_REPEAT)
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_REPEAT)

            //解绑
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0)
        }
    }


    fun readRawTextFile(context: Context?, rawId: Int): String {
        val `is` = context?.resources?.openRawResource(rawId)
        val br = BufferedReader(InputStreamReader(`is`))
        var line: String
        val sb = StringBuilder()
        try {
            while (true) {
                line = br.readLine()
                if (line == null)
                    break
                sb.append(line)
                sb.append("\n")

            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        try {
            br.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return sb.toString()
    }


    fun loadProgram(vSource: String, fSource: String): Int {
        /**
         * 顶点着色器
         */
        var vShader = loadShader(vSource, GLES20.GL_VERTEX_SHADER)
//        val vShader = GLES20.glCreateShader(GLES20.GL_VERTEX_SHADER)
//        //加载着色器代码
//        GLES20.glShaderSource(vShader, vSource)
//        //编译（配置）
//        GLES20.glCompileShader(vShader)
//
//        //查看配置 是否成功
//        val status = IntArray(1)
//        GLES20.glGetShaderiv(vShader, GLES20.GL_COMPILE_STATUS, status, 0)
//        check(status[0] == GLES20.GL_TRUE) {
//            //失败
//            "load vertex shader:" + GLES20.glGetShaderInfoLog(vShader)
//        }

        /**
         * 片元着色器
         * 流程和上面一样
         */
        var fShader = loadShader(fSource, GLES20.GL_FRAGMENT_SHADER)
//        val fShader = GLES20.glCreateShader(GLES20.GL_FRAGMENT_SHADER)
//        //加载着色器代码
//        GLES20.glShaderSource(fShader, fSource)
//        //编译（配置）
//        GLES20.glCompileShader(fShader)
//
//        //查看配置 是否成功
//        GLES20.glGetShaderiv(fShader, GLES20.GL_COMPILE_STATUS, status, 0)
//        check(status[0] == GLES20.GL_TRUE) {
//            //失败
//            "load fragment shader:" + GLES20.glGetShaderInfoLog(vShader)
//        }


//        val program = GLES20.glCreateProgram()
//        //绑定顶点和片元
//        GLES20.glAttachShader(program, vShader)
//        GLES20.glAttachShader(program, fShader)
//        //链接着色器程序
//        GLES20.glLinkProgram(program)
//        //获得状态
//        GLES20.glGetProgramiv(program, GLES20.GL_LINK_STATUS, status, 0)
//        check(status[0] == GLES20.GL_TRUE) { "link program:" + GLES20.glGetProgramInfoLog(program) }
//        GLES20.glDeleteShader(vShader)
//        GLES20.glDeleteShader(fShader)
        return createProgram(vShader, fShader)
    }

    /**
     * 加载着色器
     */
    fun loadShader(strSource: String, type: Int): Int {
        val shader = GLES20.glCreateShader(type)
        //加载着色器代码
        GLES20.glShaderSource(shader, strSource)
        //编译（配置）
        GLES20.glCompileShader(shader)

        //查看配置 是否成功
        val status = IntArray(1)
        GLES20.glGetShaderiv(shader, GLES20.GL_COMPILE_STATUS, status, 0)
        check(status[0] == GLES20.GL_TRUE) {
            //失败
            "load  shader error:" + GLES20.glGetShaderInfoLog(shader)
        }
        return shader

    }

    /**
     * 创建着色器程序
     */
    private fun createProgram(vShader: Int, fShader: Int): Int {
        val status = IntArray(1)
        val program = GLES20.glCreateProgram()
        //绑定顶点和片元
        GLES20.glAttachShader(program, vShader)
        GLES20.glAttachShader(program, fShader)
        //链接着色器程序
        GLES20.glLinkProgram(program)
        //获得状态
        GLES20.glGetProgramiv(program, GLES20.GL_LINK_STATUS, status, 0)
        check(status[0] == GLES20.GL_TRUE) { "link program:" + GLES20.glGetProgramInfoLog(program) }
        GLES20.glDeleteShader(vShader)
        GLES20.glDeleteShader(fShader)
        return program
    }


    fun loadTexture(img: Bitmap, usedTexId: Int): Int {
        return loadTexture(img, usedTexId, true)
    }

    fun loadTexture(img: Bitmap, usedTexId: Int, recycle: Boolean): Int {
        val textures = IntArray(1)
        if (usedTexId == NO_TEXTURE) {
            GLES20.glGenTextures(1, textures, 0)
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textures[0])
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

            GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, img, 0)
        } else {
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, usedTexId)
            GLUtils.texSubImage2D(GLES20.GL_TEXTURE_2D, 0, 0, 0, img)
            textures[0] = usedTexId
        }
        if (recycle) {
            img.recycle()
        }
        return textures[0]
    }

    fun loadTexture(context: Context, name: String): Int {
        val textureHandle = IntArray(1)

        GLES20.glGenTextures(1, textureHandle, 0)

        if (textureHandle[0] != 0) {

            // Read in the resource
            val bitmap = getImageFromAssetsFile(context, name)

            // Bind to the texture in OpenGL
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureHandle[0])

            // Set filtering
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR)
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR)
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE)
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE)
            // Load the bitmap into the bound texture.
            GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap, 0)

            // Recycle the bitmap, since its data has been loaded into OpenGL.
            bitmap!!.recycle()
        }

        if (textureHandle[0] == 0) {
            throw RuntimeException("Error loading texture.")
        }

        return textureHandle[0]
    }

    private fun getImageFromAssetsFile(context: Context, fileName: String): Bitmap? {
        var image: Bitmap? = null
        val am = context.resources.assets
        try {
            val `is` = am.open(fileName)
            image = BitmapFactory.decodeStream(`is`)
            `is`.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }

        return image
    }

    fun copyAssets2SdCard(context: Context, src: String, dst: String) {
        try {
            val file = File(dst)
            if (!file.exists()) {
                val inputStream = context.assets.open(src)
                val fos = FileOutputStream(file)
                var len = -1
                val buffer = ByteArray(2048)

                while (true) {
                    len = inputStream.read(buffer)
                    if (len == -1)
                        break
                    fos.write(buffer, 0, len)
                }
                inputStream.close()
                fos.close()
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }

    }
}
