package com.devyk.av.ffmpegcmd.widget

import android.content.Context
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.util.TypedValue
import android.view.View

internal class ThumbView(context: Context, private var mThumbWidth: Int, private var mThumbDrawable: Drawable?) :
    View(context) {

    private val mExtendTouchSlop: Int

    private var mPressed: Boolean = false
    var rangeIndex: Int = 0
        private set

    init {
        mExtendTouchSlop = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            EXTEND_TOUCH_SLOP.toFloat(), context.resources.displayMetrics
        ).toInt()
        setBackgroundDrawable(mThumbDrawable)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(
            View.MeasureSpec.makeMeasureSpec(mThumbWidth, View.MeasureSpec.EXACTLY),
            View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(heightMeasureSpec), View.MeasureSpec.EXACTLY)
        )

        mThumbDrawable!!.setBounds(0, 0, mThumbWidth, measuredHeight)
    }

    fun setThumbWidth(thumbWidth: Int) {
        mThumbWidth = thumbWidth
    }

    fun setThumbDrawable(thumbDrawable: Drawable) {
        mThumbDrawable = thumbDrawable
    }

    fun inInTarget(x: Int, y: Int): Boolean {
        val rect = Rect()
        getHitRect(rect)
        rect.left -= mExtendTouchSlop
        rect.right += mExtendTouchSlop
        rect.top -= mExtendTouchSlop
        rect.bottom += mExtendTouchSlop
        return rect.contains(x, y)
    }

    fun setTickIndex(tickIndex: Int) {
        rangeIndex = tickIndex
    }

    override fun isPressed(): Boolean {
        return mPressed
    }

    override fun setPressed(pressed: Boolean) {
        mPressed = pressed
    }

    companion object {

        private val EXTEND_TOUCH_SLOP = 15
    }
}