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

import com.devyk.ffmpeglib.ffmpeg.FFmpeg;
import com.devyk.ffmpeglib.ffmpeg.FFprobe;

/**
 * <p>Utility class to execute an FFprobe command asynchronously.
 *
 * @author DevYK
 */
public class AsyncFFprobeExecuteTask extends AsyncTask<Void, Integer, Integer> {
    private final String[] arguments;
    private final com.devyk.ffmpeglib.callback.ExecuteCallback ExecuteCallback;

    public AsyncFFprobeExecuteTask(final String command, final com.devyk.ffmpeglib.callback.ExecuteCallback executeCallback) {
        this.arguments = com.devyk.ffmpeglib.ffmpeg.FFmpeg.parseArguments(command);
        this.ExecuteCallback = executeCallback;
    }

    public AsyncFFprobeExecuteTask(final String[] arguments, final com.devyk.ffmpeglib.callback.ExecuteCallback executeCallback) {
        this.arguments = arguments;
        ExecuteCallback = executeCallback;
    }

    @Override
    protected Integer doInBackground(final Void... unused) {
        return FFprobe.execute(this.arguments);
    }

    @Override
    protected void onPostExecute(final Integer rc) {
//        if (ExecuteCallback != null) {
//            ExecuteCallback.apply(FFmpeg.DEFAULT_EXECUTION_ID, rc);
//        }

        if (ExecuteCallback != null) {
            if (rc == 0)
                ExecuteCallback.onSuccess(com.devyk.ffmpeglib.ffmpeg.FFmpeg.DEFAULT_EXECUTION_ID);
            else if (rc == 255)
                ExecuteCallback.onCancel(FFmpeg.DEFAULT_EXECUTION_ID);
            else
                ExecuteCallback.onFailure(com.devyk.ffmpeglib.ffmpeg.FFmpeg.DEFAULT_EXECUTION_ID,"");
        }
    }

}
