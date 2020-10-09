/*
 * Copyright (c) 2018-2020 DevYK
 *
 * This file is part of MobileFFmpeg.
 *
 * MobileFFmpeg is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * MobileFFmpeg is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with MobileFFmpeg.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.devyk.ffmpeglib.callback;

import com.devyk.ffmpeglib.entity.LogMessage;

/**
 * <p>Represents a callback function to receive an asynchronous execution result.
 *
 * @author DevYK
 * @since v2.1
 */
public interface ExecuteCallback {


    void onStart(Long executionId);

//    /**
//     * <p>Called when an asynchronous FFmpeg execution is completed.
//     *
//     * @param executionId id of the execution that completed
//     * @param returnCode  return code of the execution completed, 0 on successful completion, 255
//     *                    on user cancel, other non-zero codes on error
//     */
//    void apply(long executionId, int returnCode);

    /**
     * 如果外部传递了当前操作视频的时长，那么返回的是百分比进度，反之返回的是操作视频对应的时长进度
     *
     * @param v
     */
    void onProgress(float v);

    void onSuccess(long executionId);

    void onFailure(long executionId, String error);

    void onCancel(long executionId);

    void onFFmpegExecutionMessage(LogMessage logMessage);



}
