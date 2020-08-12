package com.devyk.ikavedit.utils.intent

import android.content.Intent
import android.R.attr.scheme
import android.app.Activity
import android.content.Context
import android.net.Uri
import java.io.File
import java.nio.file.Files.exists
import java.util.*
import androidx.core.app.ActivityCompat.startActivityForResult




/**
 * <pre>
 *     author  : devyk on 2020-08-10 12:04
 *     blog    : https://juejin.im/user/578259398ac2470061f3a3fb/posts
 *     github  : https://github.com/yangkun19921001
 *     mailbox : yang1001yk@gmail.com
 *     desc    : This is OpenFileUtils
 * </pre>
 */
object OpenFileUtil {

    /**声明各种类型文件的dataType */
    private val DATA_TYPE_ALL = "*/*"//未指定明确的文件类型，不能使用精确类型的工具打开，需要用户选择
    private val DATA_TYPE_APK = "application/vnd.android.package-archive"
    private val DATA_TYPE_VIDEO = "video/*"
    val DATA_TYPE_AUDIO = "audio/*"
    private val DATA_TYPE_HTML = "text/html"
    private val DATA_TYPE_IMAGE = "image/*"
    private val DATA_TYPE_PPT = "application/vnd.ms-powerpoint"
    private val DATA_TYPE_EXCEL = "application/vnd.ms-excel"
    private val DATA_TYPE_WORD = "application/msword"
    private val DATA_TYPE_CHM = "application/x-chm"
    private val DATA_TYPE_TXT = "text/plain"
    private val DATA_TYPE_PDF = "application/pdf"

    fun openFile(filePath: String): Intent? {

        val file = File(filePath)
        if (!file.exists())
            return null
        /* 取得扩展名 */
        val end = file.getName().substring(file.getName().lastIndexOf(".") + 1, file.getName().length)
            .toLowerCase(Locale.getDefault())
        /* 依扩展名的类型决定MimeType */
        return if (end == "m4a" || end == "mp3" || end == "mid" || end == "xmf" || end == "ogg" || end == "wav") {
            getAudioFileIntent(filePath)
        } else if (end == "3gp" || end == "mp4") {
            getVideoFileIntent(filePath)
        } else if (end == "jpg" || end == "gif" || end == "png" || end == "jpeg" || end == "bmp") {
            getImageFileIntent(filePath)
        } else if (end == "apk") {
            getApkFileIntent(filePath)
        } else if (end == "ppt") {
            getPptFileIntent(filePath)
        } else if (end == "xls") {
            getExcelFileIntent(filePath)
        } else if (end == "doc") {
            getWordFileIntent(filePath)
        } else if (end == "pdf") {
            getPdfFileIntent(filePath)
        } else if (end == "chm") {
            getChmFileIntent(filePath)
        } else if (end == "txt") {
            getTextFileIntent(filePath, false)
        } else {
            getAllIntent(filePath)
        }
    }

    // Android获取一个用于打开APK文件的intent
    fun getAllIntent(param: String): Intent {

        val intent = Intent()
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        intent.action = Intent.ACTION_VIEW
        val uri = Uri.fromFile(File(param))
        intent.setDataAndType(uri, DATA_TYPE_ALL)
        return intent
    }

    // Android获取一个用于打开APK文件的intent
    fun getApkFileIntent(param: String): Intent {

        val intent = Intent()
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        intent.action = Intent.ACTION_VIEW
        val uri = Uri.fromFile(File(param))
        intent.setDataAndType(uri, DATA_TYPE_APK)
        return intent
    }

    // Android获取一个用于打开VIDEO文件的intent
    fun getVideoFileIntent(param: String): Intent {

        val intent = Intent("android.intent.action.VIEW")
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        intent.putExtra("oneshot", 0)
        intent.putExtra("configchange", 0)
        val uri = Uri.fromFile(File(param))
        intent.setDataAndType(uri, DATA_TYPE_VIDEO)
        return intent
    }

    // Android获取一个用于打开AUDIO文件的intent
    fun getAudioFileIntent(param: String): Intent {

        val intent = Intent("android.intent.action.VIEW")
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        intent.putExtra("oneshot", 0)
        intent.putExtra("configchange", 0)
        val uri = Uri.fromFile(File(param))
        intent.setDataAndType(uri, "audio/*")
        return intent
    }

    // Android获取一个用于打开Html文件的intent
    fun getHtmlFileIntent(param: String): Intent {

        val uri = Uri.parse(param).buildUpon().encodedAuthority("com.android.htmlfileprovider").scheme("content")
            .encodedPath(param).build()
        val intent = Intent("android.intent.action.VIEW")
        intent.setDataAndType(uri, DATA_TYPE_HTML)
        return intent
    }

    // Android获取一个用于打开图片文件的intent
    fun getImageFileIntent(param: String): Intent {

        val intent = Intent("android.intent.action.VIEW")
        intent.addCategory("android.intent.category.DEFAULT")
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        val uri = Uri.fromFile(File(param))
        intent.setDataAndType(uri, DATA_TYPE_IMAGE)
        return intent
    }

    // Android获取一个用于打开PPT文件的intent
    fun getPptFileIntent(param: String): Intent {

        val intent = Intent("android.intent.action.VIEW")
        intent.addCategory("android.intent.category.DEFAULT")
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        val uri = Uri.fromFile(File(param))
        intent.setDataAndType(uri, DATA_TYPE_PPT)
        return intent
    }

    // Android获取一个用于打开Excel文件的intent
    fun getExcelFileIntent(param: String): Intent {

        val intent = Intent("android.intent.action.VIEW")
        intent.addCategory("android.intent.category.DEFAULT")
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        val uri = Uri.fromFile(File(param))
        intent.setDataAndType(uri, DATA_TYPE_EXCEL)
        return intent
    }

    // Android获取一个用于打开Word文件的intent
    fun getWordFileIntent(param: String): Intent {

        val intent = Intent("android.intent.action.VIEW")
        intent.addCategory("android.intent.category.DEFAULT")
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        val uri = Uri.fromFile(File(param))
        intent.setDataAndType(uri, DATA_TYPE_WORD)
        return intent
    }

    // Android获取一个用于打开CHM文件的intent
    fun getChmFileIntent(param: String): Intent {

        val intent = Intent("android.intent.action.VIEW")
        intent.addCategory("android.intent.category.DEFAULT")
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        val uri = Uri.fromFile(File(param))
        intent.setDataAndType(uri, DATA_TYPE_CHM)
        return intent
    }

    // Android获取一个用于打开文本文件的intent
    fun getTextFileIntent(param: String, paramBoolean: Boolean): Intent {

        val intent = Intent("android.intent.action.VIEW")
        intent.addCategory("android.intent.category.DEFAULT")
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        if (paramBoolean) {
            val uri1 = Uri.parse(param)
            intent.setDataAndType(uri1, DATA_TYPE_TXT)
        } else {
            val uri2 = Uri.fromFile(File(param))
            intent.setDataAndType(uri2, DATA_TYPE_TXT)
        }
        return intent
    }

    // Android获取一个用于打开PDF文件的intent
    fun getPdfFileIntent(param: String): Intent {

        val intent = Intent("android.intent.action.VIEW")
        intent.addCategory("android.intent.category.DEFAULT")
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        val uri = Uri.fromFile(File(param))
        intent.setDataAndType(uri, DATA_TYPE_PDF)
        return intent
    }

    fun openSysAudioFile(context: Activity,resultCodec:Int){
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type =DATA_TYPE_AUDIO // specify "audio/mp3" to filter only mp3 files
        context.startActivityForResult(intent,resultCodec)
    }

}