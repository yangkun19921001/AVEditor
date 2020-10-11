package com.devyk.aveditor.jni

import com.devyk.aveditor.muxer.JavaMp4Muxer

/**
 * <pre>
 *     author  : devyk on 2020-08-20 17:32
 *     blog    : https://juejin.im/user/578259398ac2470061f3a3fb/posts
 *     github  : https://github.com/yangkun19921001
 *     mailbox : yang1001yk@gmail.com
 *     desc    : This is AVJavaMuxerEngine
 * </pre>
 */
public class AVJavaMuxerEngine : IJavaMuxer {


    public override fun javaMergeVieo(inPath: ArrayList<String>, outPath: String) {
        val javaMp4Muxer = JavaMp4Muxer(inPath, outPath)
        javaMp4Muxer.merge()
    }
}