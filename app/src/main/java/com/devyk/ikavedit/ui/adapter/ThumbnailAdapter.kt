package com.devyk.ikavedit.ui.adapter

import android.graphics.Bitmap
import android.media.MediaMetadataRetriever
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.bitmap.VideoDecoder.FRAME_OPTION
import com.bumptech.glide.request.RequestOptions
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.devyk.ikavedit.R
import java.io.File
import java.util.concurrent.TimeUnit

/**
 * <pre>
 *     author  : devyk on 2020-09-30 10:32
 *     blog    : https://juejin.im/user/578259398ac2470061f3a3fb/posts
 *     github  : https://github.com/yangkun19921001
 *     mailbox : yang1001yk@gmail.com
 *     desc    : This is ThumbnailAdapter
 * </pre>
 */
public class ThumbnailAdapter :BaseQuickAdapter<File,BaseViewHolder>{

    // 帧间隔时长 1s
    private val FRAME_INTERVAL_TIME_S = 1

    constructor(data: MutableList<File>?) : super(R.layout.adapter_thumbnail, data)

    override fun convert(holder: BaseViewHolder, item: File) {
        val requestOptions = RequestOptions.frameOf( TimeUnit.SECONDS.toMicros((holder.layoutPosition * FRAME_INTERVAL_TIME_S).toLong()))
        requestOptions.set(FRAME_OPTION, MediaMetadataRetriever.OPTION_CLOSEST)
        requestOptions.diskCacheStrategy(DiskCacheStrategy.RESOURCE)
        requestOptions.dontTransform()
        Glide.with(context).load(item).apply(requestOptions).into(holder.getView(R.id.video_thumb))

    }
}