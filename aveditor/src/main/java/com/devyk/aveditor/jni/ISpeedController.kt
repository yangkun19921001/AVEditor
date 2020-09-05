package com.devyk.aveditor.jni

import com.devyk.aveditor.entity.Speed

interface ISpeedController {

    /**
     * @param track 操作音频速率控制的对象索引，不能重复
     * @param channels 通道
     * @param samplingRate 采样率
     * @param pitchSemi 变调率
     */
    public fun initSpeedController(
        track: Int,
        channels: Int,
        samplingRate: Int,
        tempo: Double,
        pitchSemi: Double
    )

    /**
     * 放入 PCM 数据
     */
    public fun putData(track: Int, input: ByteArray, length: Int): Int

    /**
     * 取出变速之后的 PCM 数据
     */
    public fun getData(track: Int, out: ShortArray, length: Int): Int

    /**
     * 关闭资源
     */
    public fun close(track: Int)

    public fun setRecordSpeed(track: Int, speed: Speed = Speed.NORMAL)
}
