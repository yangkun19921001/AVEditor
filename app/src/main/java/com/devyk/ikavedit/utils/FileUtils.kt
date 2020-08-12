package com.devyk.ikavedit.utils

import java.io.File
import java.io.IOException
import android.provider.MediaStore
import android.provider.DocumentsContract
import android.content.ContentUris
import android.os.Environment.getExternalStorageDirectory
import android.os.Build
import android.content.ContentResolver
import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.os.Environment
import android.R.attr.data


/**
 * <pre>
 *     author  : devyk on 2020-08-09 13:53
 *     blog    : https://juejin.im/user/578259398ac2470061f3a3fb/posts
 *     github  : https://github.com/yangkun19921001
 *     mailbox : yang1001yk@gmail.com
 *     desc    : This is FileUtils
 * </pre>
 */
object FileUtils {


    fun createFileByDeleteOldFile(filePath: String): Boolean {
        return createFileByDeleteOldFile(getFileByPath(filePath))
    }

    fun getFileByPath(filePath: String): File? {
        return File(filePath)
    }

    fun createFileByDeleteOldFile(file: File?): Boolean {
        if (file == null) return false
        // file exists and unsuccessfully delete then return false
        if (file.exists() && !file.delete()) return false
        if (!createOrExistsDir(file.parentFile)) return false
        try {
            return file.createNewFile()
        } catch (e: IOException) {
            e.printStackTrace()
            return false
        }

    }

    fun createOrExistsDir(file: File?): Boolean {
        return file != null && if (file.exists()) file.isDirectory else file.mkdirs()
    }


    fun getFilePathByUri(context: Context, uri: Uri): String? {
        // 通过ContentProvider查询文件路径
        val resolver = context.getContentResolver()
        val cursor = resolver.query(uri, null, null, null, null)
        var path = uri.getPath()
        if (cursor!!.moveToFirst()) {
            // 多媒体文件，从数据库中获取文件的真实路径
            path = cursor!!.getString(cursor!!.getColumnIndex("_data"))
        }
        cursor!!.close()
        return path
    }


}