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
 * You should have received a copy of the GNU Lesser General Public License
 * along with MobileFFmpeg.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.devyk.ffmpeglib.async;

import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;

import com.devyk.ffmpeglib.callback.ExecuteCallback;
import com.devyk.ffmpeglib.callback.LogCallback;

import com.devyk.ffmpeglib.config.Config;
import com.devyk.ffmpeglib.entity.LogMessage;
import com.devyk.ffmpeglib.ffmpeg.FFmpeg;
import com.tencent.mars.xlog.Log;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * <p>Utility class to execute an FFmpeg command asynchronously.
 *
 * @author DevYK
 */
public class AsyncFFmpegExecuteTask extends AsyncTask<Void, Integer, Integer> {
    private final String[] arguments;
    private static ExecuteCallback sExecuteCallback;
    private final Long executionId;
    private static Long mVideoduration = -11L;
    private AsyncLogCallback mAsyncLogCallback;
    protected static Handler mHandler = null;


    public AsyncFFmpegExecuteTask(final String command, final com.devyk.ffmpeglib.callback.ExecuteCallback executeCallback) {
        this(FFmpeg.parseArguments(command), executeCallback);
    }

    public AsyncFFmpegExecuteTask(final String[] arguments, final com.devyk.ffmpeglib.callback.ExecuteCallback executeCallback) {
        this(FFmpeg.DEFAULT_EXECUTION_ID, arguments, -1, executeCallback);
    }

    public AsyncFFmpegExecuteTask(final String[] arguments, long duration, final com.devyk.ffmpeglib.callback.ExecuteCallback executeCallback) {
        this(FFmpeg.DEFAULT_EXECUTION_ID, arguments, duration, executeCallback);
    }

    public AsyncFFmpegExecuteTask(final long executionId, final String command, long videoduration, final com.devyk.ffmpeglib.callback.ExecuteCallback executeCallback) {
        this(executionId, FFmpeg.parseArguments(command), videoduration, executeCallback);
    }

    public AsyncFFmpegExecuteTask(final long executionId, final String[] arguments, long videoduration, final com.devyk.ffmpeglib.callback.ExecuteCallback executeCallback) {
        this.executionId = executionId;
        this.arguments = arguments;
        onDestory();
        sExecuteCallback = executeCallback;
        this.mVideoduration = videoduration;
        mHandler = new Handler(Looper.getMainLooper());
        mAsyncLogCallback = new AsyncLogCallback();
        enableLogCallback(mAsyncLogCallback);
    }

    private void onDestory() {
        mVideoduration = 0L;
        if (sExecuteCallback != null) {
            sExecuteCallback = null;
        }
        if (mAsyncLogCallback != null) {
            enableLogCallback(null);
            mAsyncLogCallback = null;
        }

        if (mHandler != null) {
            mHandler = null;
        }
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        if (sExecuteCallback != null) {
            sExecuteCallback.onStart(executionId);
        }

    }

    private void enableLogCallback(AsyncLogCallback asyncLogCallback) {
        Config.enableLogCallback(asyncLogCallback);
    }

    /**
     * 子线程中执行
     *
     * @param unused
     * @return
     */
    @Override
    protected Integer doInBackground(final Void... unused) {
        return Config.ffmpegExecute(executionId, this.arguments);
    }


    /**
     * 主线程中执行
     *
     * @param rc
     */
    @Override
    protected void onPostExecute(final Integer rc) {
        synchronized (this) {
            if (sExecuteCallback != null) {
                if (rc == 0)
                    sExecuteCallback.onSuccess(executionId);
                else if (rc == 255)
                    sExecuteCallback.onCancel(executionId);
            }
            enableLogCallback(null);
        }
    }


    /**
     * C++ 调用
     *
     * @param progress
     */
    public static void progress(final float progress) {
        if (mVideoduration != -1 && sExecuteCallback != null) {
            final float v = progress / mVideoduration * 100;
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    if (judgeContainsStr(v)) {
                        return;
                    }
                    if (v > 0f)
                        sExecuteCallback.onProgress(v);
                }
            });
        } else {
            if (sExecuteCallback != null)
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        sExecuteCallback.onProgress(progress);
                    }
                });
        }
    }

    /**
     * 判断是否存在字母
     *
     * @param num
     */
    public static boolean judgeContainsStr(String num) {
        String regex = ".*[a-zA-Z]+.*";
        Matcher m = Pattern.compile(regex).matcher(num);
        return m.matches();
    }

    public static boolean judgeContainsStr(float num_f) {
        boolean contains;
        String num = String.valueOf(num_f);
        try {
            contains = judgeContainsStr(num);
        } catch (Exception e) {
            contains = false;
        }
        return contains;
    }

    /**
     * 执行的 LOG message
     */
    protected class AsyncLogCallback implements LogCallback {
        @Override
        public void apply(final LogMessage message) {
            synchronized (this) {
                switch (message.getLevel()) {
                    case AV_LOG_FATAL:
                    case AV_LOG_ERROR:
                        if (sExecuteCallback != null && mHandler != null)
                            mHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    if ((message.getText().contains("aac") &&
                                            message.getText().contains("Input buffer exhausted before END element found"))
                                            || (
                                            message.getText().contains("Invalid data found when processing input")) ){
                                        Log.e("AsyncLogCallback","这个异常暂时不处理-》"+message.getText());
                                        return;
                                    }
                                    sExecuteCallback.onFailure(executionId, message.getText());
                               //[aac @ 0x738a9f2d00] Input buffer exhausted before END element found
                                    //[aac @ 0x73761ecb00] Input buffer exhausted before END element found
                                    //[aac @ 0x738a9f3c00] Input buffer exhausted before END element found
                                    //Error while decoding stream #1:1: Invalid data found when processing input
                                }
                            });
                        break;
                    default:
                        break;
                }
                if (sExecuteCallback != null && mHandler != null)
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            sExecuteCallback.onFFmpegExecutionMessage(message);
                        }
                    });
            }
        }
    }
}
