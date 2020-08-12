/*
 * Copyright (C) 2018 CyberAgent, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.devyk.aveditor.video.filter.gpuimage

import android.content.Context
import com.devyk.aveditor.video.filter.gpuimage.base.GPUImageFilter

open class GPUImageTwoPassFilter(context: Context,
    firstVertexShader: String, firstFragmentShader: String,
    secondVertexShader: String, secondFragmentShader: String
) : GPUImageFilterGroup(context,null) {
    init {
        addFilter(GPUImageFilter(context,firstVertexShader, firstFragmentShader))
        addFilter(GPUImageFilter(context,secondVertexShader, secondFragmentShader))
    }
}
