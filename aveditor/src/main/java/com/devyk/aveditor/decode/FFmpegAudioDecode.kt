package com.devyk.aveditor.decode

import com.devyk.aveditor.jni.IMusicDecode
import com.devyk.aveditor.jni.JNIManager

/**
 * <pre>
 *     author  : devyk on 2020-08-12 16:35
 *     blog    : https://juejin.im/user/578259398ac2470061f3a3fb/posts
 *     github  : https://github.com/yangkun19921001
 *     mailbox : yang1001yk@gmail.com
 *     desc    : This is FFmpegAudioDecode 将外部传递进来的音乐文件进行解码
 * </pre>
 */
public class FFmpegAudioDecode : IAudioDecode {

    override fun addOnDecodeListener(listener: IMusicDecode.OnDecodeListener) {
        mMusicDecode?.addOnDecodeListener(listener)
    }


    private var mMusicDecode: IMusicDecode? = null

    init {
        mMusicDecode = JNIManager.getAVDecodeEngine()
    }


    override fun addRecordMusic(path: String?) {
        mMusicDecode?.addRecordMusic(path)
    }

    override fun start() {
        mMusicDecode?.start()
    }

    override fun pause() {
        mMusicDecode?.pause()
    }

    override fun resume() {
        mMusicDecode?.resume()
    }


    override fun stop() {
        mMusicDecode?.stop()
    }

}
