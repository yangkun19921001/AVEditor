package com.devyk.ffmpeglib.util

import android.content.Context
import android.util.Log

import java.io.File
import java.io.FileOutputStream
import java.io.RandomAccessFile


/**
 * <pre>
 *     author  : devyk on 2020-09-28 21:14
 *     blog    : https://juejin.im/user/578259398ac2470061f3a3fb/posts
 *     github  : https://github.com/yangkun19921001
 *     mailbox : yang1001yk@gmail.com
 *     desc    : This is FileUtils
 * </pre>
 */
object FileUtils {

    /**
     * 此类用于生成合并视频所需要的文档
     * @param strcontent 视频路径集合
     * @param filePath 生成的地址
     * @param fileName 生成的文件名
     */
    fun writeTxtToFile(strcontent: List<String>, filePath: String, fileName: String) {
        //生成文件夹之后，再生成文件，不然会出错
        makeFilePath(filePath, fileName)
        val strFilePath = filePath + fileName
        // 每次写入时，都换行写
        var strContent = ""
        for (i in strcontent.indices) {
            strContent += "file " + strcontent[i] + "\r\n"
        }
        try {
            val file = File(strFilePath)
            //检查文件是否存在，存在则删除
            if (file.isFile && file.exists()) {
                file.delete()
            }
            file.parentFile!!.mkdirs()
            file.createNewFile()
            val raf = RandomAccessFile(file, "rwd")
            raf.seek(file.length())
            raf.write(strContent.toByteArray())
            raf.close()
            Log.e("TestFile", "写入成功:$strFilePath")
        } catch (e: Exception) {
            Log.e("TestFile", "Error on write File:$e")
        }

    }

    //创建路径
    fun makeFilePath(filePath: String, fileName: String): File? {
        var file: File? = null
        makeRootDirectory(filePath)
        try {
            file = File(filePath + fileName)
            if (!file.exists()) {
                file.createNewFile()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return file
    }

    //创建文件夹
    fun makeRootDirectory(filePath: String) {
        var file: File? = null
        try {
            file = File(filePath)
            if (!file.exists()) {
                file.mkdir()
            }
        } catch (e: Exception) {
            Log.i("error:", e.toString() + "")
        }

    }


    //删除文件夹
    public fun deleteDirectory(folder: File) {
        if (folder.exists()) {
            val files = folder.listFiles() ?: return
            for (i in files.indices) {
                if (files[i].isDirectory) {
                    deleteDirectory(files[i])
                } else {
                    files[i].delete()
                }
            }
        }
        folder.delete()
    }

    /**
     * 从assets目录中复制文件到本地
     *
     * @param context Context
     * @param oldPath String  原文件路径
     * @param newPath String  复制后路径
     */
    fun copyFilesFassets(context: Context, oldPath: String, newPath: String) {
        try {
            val fileNames = context.assets.list(oldPath)
            if (fileNames!!.size > 0) {
                val file = File(newPath)
                file.mkdirs()
                for (fileName in fileNames) {
                    copyFilesFassets(context, "$oldPath/$fileName", "$newPath/$fileName")
                }
            } else {
                val inputStream = context.assets.open(oldPath)
                val ff = File(newPath)
                if (!ff.exists()) {
                    val fos = FileOutputStream(ff)
                    val buffer = ByteArray(1024)
                    var byteCount = 0
                    while (true) {
                        byteCount =inputStream.read(buffer)
                        if (byteCount == -1)return
                        fos.write(buffer, 0, byteCount)
                    }
                    fos.flush()
                    inputStream.close()
                    fos.close()
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }
}
