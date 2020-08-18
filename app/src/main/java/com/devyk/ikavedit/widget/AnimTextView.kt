package com.devyk.ikavedit.widget

import android.animation.Animator
import android.content.Context
import android.util.AttributeSet
import android.widget.LinearLayout
import android.widget.TextView
import android.animation.ObjectAnimator
import android.animation.Keyframe
import android.animation.PropertyValuesHolder
import android.os.Message
import android.view.View
import android.view.View.SCALE_Y
import android.view.View.SCALE_X
import android.text.TextUtils


/**
 * <pre>
 *     author  : devyk on 2020-08-06 17:16
 *     blog    : https://juejin.im/user/578259398ac2470061f3a3fb/posts
 *     github  : https://github.com/yangkun19921001
 *     mailbox : yang1001yk@gmail.com
 *     desc    : This is AnimImageView
 * </pre>
 */
public class AnimTextView : TextView, Animator.AnimatorListener {

    private var mDelayTime = 300L
    private var mlistener: OnClickListener? = null

    /**
     * 设置跑马灯效果
     */
    private var isMarqueeEnable = false
    /**
     * 默认旋转抖动
     */
    private var mIsRotate = true

    override fun onAnimationEnd(animation: Animator?) {

    }

    override fun onAnimationStart(animation: Animator?) {
        mlistener?.onClick(this)
    }

    override fun onAnimationRepeat(animation: Animator?) {
    }

    override fun onAnimationCancel(animation: Animator?) {
        mlistener?.onClick(this)
    }

    constructor(context: Context?) : this(context, null)
    constructor(context: Context?, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    override fun onFinishInflate() {
        super.onFinishInflate()
        init()
    }

    fun init() {
        //默认初始化抖动
        val objectAnimator = startShakeByPropertyAnim(this, 0.9f, 1.1f, 10f, 1000)
        objectAnimator?.start()

        //默认点击事件
        setOnClickListener {
            val objectAnimator = startShakeByPropertyAnim(this, 0.4f, 1.1f, 10f, mDelayTime)
            objectAnimator?.addListener(this)
            objectAnimator
                ?.start()
        }
    }

    /**
     * 使用该点击效果
     */
    fun addOnClickListener(delayTime: Int = 300, listener: OnClickListener) {
        this.mlistener = listener
        this.mDelayTime = delayTime.toLong()
    }

    private fun startShakeByPropertyAnim(
        view: View?,
        scaleSmall: Float,
        scaleLarge: Float,
        shakeDegrees: Float,
        duration: Long
    ): ObjectAnimator? {
        if (view == null) {
            return null;
        }

        //先变小后变大
        val scaleXValuesHolder = PropertyValuesHolder.ofKeyframe(
            View.SCALE_X,
            Keyframe.ofFloat(0f, 1.0f),
            Keyframe.ofFloat(0.25f, scaleSmall),
            Keyframe.ofFloat(0.5f, scaleLarge),
            Keyframe.ofFloat(0.75f, scaleLarge),
            Keyframe.ofFloat(1.0f, 1.0f)
        )
        val scaleYValuesHolder = PropertyValuesHolder.ofKeyframe(
            View.SCALE_Y,
            Keyframe.ofFloat(0f, 1.0f),
            Keyframe.ofFloat(0.25f, scaleSmall),
            Keyframe.ofFloat(0.5f, scaleLarge),
            Keyframe.ofFloat(0.75f, scaleLarge),
            Keyframe.ofFloat(1.0f, 1.0f)
        )

        //先往左再往右
        val rotateValuesHolder = PropertyValuesHolder.ofKeyframe(
            View.ROTATION,
            Keyframe.ofFloat(0f, 0f),
            Keyframe.ofFloat(0.1f, -shakeDegrees),
            Keyframe.ofFloat(0.2f, shakeDegrees),
            Keyframe.ofFloat(0.3f, -shakeDegrees),
            Keyframe.ofFloat(0.4f, shakeDegrees),
            Keyframe.ofFloat(0.5f, -shakeDegrees),
            Keyframe.ofFloat(0.6f, shakeDegrees),
            Keyframe.ofFloat(0.7f, -shakeDegrees),
            Keyframe.ofFloat(0.8f, shakeDegrees),
            Keyframe.ofFloat(0.9f, -shakeDegrees),
            Keyframe.ofFloat(1.0f, 0f)
        )
        var objectAnimator: ObjectAnimator? = null
        if (mIsRotate) {
            objectAnimator =
                ObjectAnimator.ofPropertyValuesHolder(view, scaleXValuesHolder, scaleYValuesHolder, rotateValuesHolder)
            mIsRotate = false
        } else {
            objectAnimator =
                ObjectAnimator.ofPropertyValuesHolder(view, scaleXValuesHolder, scaleYValuesHolder)
        }
        objectAnimator.duration = duration
        return objectAnimator
    }

    fun setMarqueeEnable(enable: Boolean) {
        if (isMarqueeEnable !== enable) {
            isMarqueeEnable = enable
            if (enable) {
                ellipsize = TextUtils.TruncateAt.MARQUEE
            } else {
                ellipsize = TextUtils.TruncateAt.END
            }
            onWindowFocusChanged(enable)
        }
    }

    fun isMarqueeEnable(): Boolean {
        return isMarqueeEnable
    }

    override fun isFocused(): Boolean {
        return isMarqueeEnable
    }

    fun setText(message: String?) {
        message?.let { content ->
            val index = content.lastIndexOf("/")
            if (index == -1)
                return@let
            val content = content.substring(index + 1, content.length )
            text = content
            return
        }
        text = message

    }

    interface OnClickListener {
        /**
         * Called when a view has been clicked.
         *
         * @param v The view that was clicked.
         */
        fun onClick(v: View)
    }

}