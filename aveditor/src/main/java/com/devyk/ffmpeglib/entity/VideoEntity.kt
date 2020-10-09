package com.devyk.ffmpeglib.entity

import android.os.Parcel
import android.os.Parcelable

/**
 * <pre>
 *     author  : devyk on 2020-09-29 15:27
 *     blog    : https://juejin.im/user/578259398ac2470061f3a3fb/posts
 *     github  : https://github.com/yangkun19921001
 *     mailbox : yang1001yk@gmail.com
 *     desc    : This is VideoEntity
 * </pre>
 */
data class VideoEntity(
    var id: Int = 0,
    var videoName: String = "",
    var videoPath: String = "",
    var videoDuration: Long = 0,
    var videoSize: Long = 0,
    var isSelect: Boolean =false
) :
    Parcelable {
    constructor(source: Parcel) : this(
        source.readInt(),
        source.readString().toString(),
        source.readString().toString(),
        source.readLong(),
        source.readLong()
    )

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
        writeInt(id)
        writeString(videoName)
        writeString(videoPath)
        writeLong(videoDuration)
        writeLong(videoSize)
    }

    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<VideoEntity> = object : Parcelable.Creator<VideoEntity> {
            override fun createFromParcel(source: Parcel): VideoEntity = VideoEntity(source)
            override fun newArray(size: Int): Array<VideoEntity?> = arrayOfNulls(size)
        }
    }
}