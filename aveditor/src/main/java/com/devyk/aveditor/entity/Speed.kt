package com.devyk.aveditor.entity

enum class Speed(val value: Double) {
    //级慢
    VERY_SLOW(0.25),
    //慢
    SLOW(0.5),
    //正常
    NORMAL(1.0),
    //快
    FAST(2.0),
    //极快
    VERY_FAST(3.0)
}