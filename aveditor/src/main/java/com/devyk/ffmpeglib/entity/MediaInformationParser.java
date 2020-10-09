/*
 * Copyright (c) 2018, 2020 DevYK
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

package com.devyk.ffmpeglib.entity;

import android.util.Log;

import com.devyk.ffmpeglib.config.Config;
import com.devyk.ffmpeglib.entity.MediaInformation;
import com.devyk.ffmpeglib.entity.StreamInformation;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Helper class for parsing {@link com.devyk.ffmpeglib.entity.MediaInformation}.
 *
 * @since 3.0
 */
public class MediaInformationParser {

    /**
     * Extracts MediaInformation from the given ffprobe json output.
     *
     * @param ffprobeJsonOutput ffprobe json output
     * @return created {@link com.devyk.ffmpeglib.entity.MediaInformation} instance of null if a parsing error occurs
     */
    public static com.devyk.ffmpeglib.entity.MediaInformation from(final String ffprobeJsonOutput) {
        try {
            return fromWithError(ffprobeJsonOutput);
        } catch (JSONException e) {
            Log.e(Config.TAG, "MediaInformation parsing failed.", e);
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Extracts MediaInformation from the given ffprobe json output.
     *
     * @param ffprobeJsonOutput ffprobe json output
     * @return created {@link MediaInformation} instance
     * @throws JSONException if a parsing error occurs
     */
    public static com.devyk.ffmpeglib.entity.MediaInformation fromWithError(final String ffprobeJsonOutput) throws JSONException {
        JSONObject jsonObject = new JSONObject(ffprobeJsonOutput);
        JSONArray streamArray = jsonObject.optJSONArray("streams");

        ArrayList<StreamInformation> arrayList = new ArrayList<>();
        for (int i = 0; streamArray != null && i < streamArray.length(); i++) {
            JSONObject streamObject = streamArray.optJSONObject(i);
            if (streamObject != null) {
                arrayList.add(new com.devyk.ffmpeglib.entity.StreamInformation(streamObject));
            }
        }

        return new com.devyk.ffmpeglib.entity.MediaInformation(jsonObject, arrayList);
    }

}
