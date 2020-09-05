package com.devyk.aveditor.jni

import com.devyk.aveditor.entity.Speed

/**
 * <pre>
 *     author  : devyk on 2020-08-21 00:58
 *     blog    : https://juejin.im/user/578259398ac2470061f3a3fb/posts
 *     github  : https://github.com/yangkun19921001
 *     mailbox : yang1001yk@gmail.com
 *     desc    : This is AVSpeedEngine
 * </pre>
 */
public class AVSpeedEngine : ISpeedController {


    private var channels: Int = 1
    private var samplingRate: Int = 44100
    private var tempo: Double = 1.0
    private var pitchSemi: Double = 1.0
    private var track: Int = 0

    override external fun initSpeedController(
        track: Int,
        channels: Int,
        samplingRate: Int,
        tempo: Double,
        pitchSemi: Double
    )

    override external fun putData(track: Int, input: ByteArray,  length: Int): Int
    external override fun getData(track: Int, shortArray: ShortArray, length: Int): Int
    override external fun close(track: Int);

    override fun setRecordSpeed(track: Int, speed: Speed) {
        setRecordSpeed(track, speed.value)
    }

    private external fun setRecordSpeed(track: Int, speed: Double)


}