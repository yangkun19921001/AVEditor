package com.devyk.aveditor.entity

enum class Speed(val value: Float) {
    //级慢
    VERY_SLOW(0.25f),
    //慢
    SLOW(0.5f),
    //正常
    NORMAL(1.0f),
    //快
    FAST(2.0f),
    //极快
    VERY_FAST(3.0f)
}