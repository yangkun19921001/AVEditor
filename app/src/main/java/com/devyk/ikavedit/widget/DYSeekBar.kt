package com.devyk.ikavedit.widget


import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.drawable.BitmapDrawable
import android.os.Handler
import android.util.AttributeSet
import android.widget.SeekBar
import com.devyk.ikavedit.R


/**
 * <pre>
 *     author  : devyk on 2020-05-23 23:11
 *     blog    : https://juejin.im/user/578259398ac2470061f3a3fb/posts
 *     github  : https://github.com/yangkun19921001
 *     mailbox : yang1001yk@gmail.com
 *     desc    : This is DYSeekBar
 * </pre>
 */
class DYSeekBar : SeekBar {
    /**
     * 背景画笔
     */
    private var mBackgroundPaint: Paint? = null

    /**
     * 进度画笔
     */
    private var mProgressPaint: Paint? = null

    /**
     * 第二进度画笔
     */
    private var mSecondProgressPaint: Paint? = null

    /**
     * 游标画笔
     */
    private var mThumbPaint: Paint? = null

    /**
     * 文字画笔
     */
    private var mTextPaint: Paint? = null

    /**
     * 默认
     */
    private val TRACKTOUCH_NONE = -1
    /**
     * 开始拖动
     */
    private val TRACKTOUCH_START = 0
    private var mTrackTouch = TRACKTOUCH_NONE

    /**
     * 是否绘制小圆点
     */
    private var draw_thumb_status = STATUS.NO_DRAW;

    private var mOnChangeListener: OnChangeListener? = null

    //TrackingTouch
    private var isTrackingTouch = false
    private var mTrackingTouchSleepTime = 0
    private val mHandler = Handler()
    private val mRunnable = Runnable { setTrackTouch(TRACKTOUCH_NONE) }

    private var mBackgroundColor = Color.WHITE
    private var mProgressColor = Color.RED
    private var mTextColor = Color.WHITE
    private var mTextSize = 12f

    var isShowCircle = false


    constructor(context: Context) : super(context) {
        init(context, null)
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init(context, attrs)
    }

    /**
     * 初始化
     */
    @SuppressLint("ResourceAsColor")
    private fun init(context: Context, attrs: AttributeSet?) {


        val ta = context.obtainStyledAttributes(attrs, R.styleable.DYSeekBar)
        try {
            mBackgroundColor =
                ta.getColor(R.styleable.DYSeekBar_dy_bg_color, resources.getColor(R.color.alpha_90_black))

            mProgressColor =
                ta.getColor(
                    R.styleable.DYSeekBar_dy_progress_color,
                    resources.getColor(R.color.recordbutton_down_color)
                )

            mTextSize =
                ta.getDimension(R.styleable.DYSeekBar_dy_text_size, (12f * resources.displayMetrics.scaledDensity))

            mTextColor =
                ta.getColor(R.styleable.DYSeekBar_dy_text_color, Color.WHITE)

             isShowCircle = ta.getBoolean(R.styleable.DYSeekBar_dy_show_circle, false)
            updateIsDrawCircle(isShowCircle)

        } finally {
            ta.recycle()
        }



        setBackgroundColor(Color.TRANSPARENT)
        //
        mBackgroundPaint = Paint()
        mBackgroundPaint!!.isDither = true
        mBackgroundPaint!!.isAntiAlias = true
        mBackgroundPaint!!.setColor(mBackgroundColor)
//        mBackgroundPaint!!.color = Color.parseColor("#FF8B898A")

        //
        mProgressPaint = Paint()
        mProgressPaint!!.isDither = true
        mProgressPaint!!.isAntiAlias = true
        mProgressPaint!!.setColor(mProgressColor)
//        mProgressPaint!!.color = Color.parseColor("#0288d1")

        //
        mSecondProgressPaint = Paint()
        mSecondProgressPaint!!.isDither = true
        mSecondProgressPaint!!.isAntiAlias = true
        mSecondProgressPaint!!.setColor(resources.getColor(R.color.second_progress_color))
//        mSecondProgressPaint!!.color = Color.parseColor("#b8b8b8")

        //
        mThumbPaint = Paint()
        mThumbPaint!!.isDither = true
        mThumbPaint!!.isAntiAlias = true
        mThumbPaint!!.setColor(resources.getColor(R.color.dy_seekbar_progress_color))

        mTextPaint = Paint()
        mTextPaint!!.isAntiAlias = true
        mTextPaint!!.textSize = mTextSize.toFloat()
        mTextPaint!!.setColor(mTextColor)

        //
        thumb = BitmapDrawable()
        setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, i: Int, b: Boolean) {
                if (mTrackTouch == TRACKTOUCH_START) {
                    if (mOnChangeListener != null) {
                        mOnChangeListener!!.onProgressChanged(this@DYSeekBar)
                    }
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {
                isTrackingTouch = true
                setDrawThumbStatus(STATUS.DRAW)
                mHandler.removeCallbacks(mRunnable)
                if (mTrackTouch == TRACKTOUCH_NONE) {
                    setTrackTouch(TRACKTOUCH_START)
                    if (mOnChangeListener != null) {
                        mOnChangeListener!!.onTrackingTouchStart(this@DYSeekBar)
                    }
                }
            }

            override fun onStopTrackingTouch(seekBar: SeekBar) {
                isTrackingTouch = false
                updateIsDrawCircle(isShowCircle)
                if (mTrackTouch == TRACKTOUCH_START) {
                    if (mOnChangeListener != null) {
                        mOnChangeListener!!.onTrackingTouchFinish(this@DYSeekBar)
                    }
                    mHandler.postDelayed(mRunnable, mTrackingTouchSleepTime.toLong())
                }
            }
        })
    }

    private fun updateIsDrawCircle(isShowCircle: Boolean) {
        if (isShowCircle) {
            draw_thumb_status = STATUS.DRAW
        } else
            draw_thumb_status = STATUS.NO_DRAW
    }


    @Synchronized
    override fun onDraw(canvas: Canvas) {
        var rSize = height / 6
        if (isTrackingTouch) {
            rSize = height / 6
        }
        val height = height / 4 / 3 / 4
        var leftPadding = rSize / 2

        if (progress > 0) {
            leftPadding = 0
        }

        val backgroundRect = RectF(
            leftPadding.toFloat(), (getHeight() / 2 - height).toFloat(), width.toFloat(),
            (getHeight() / 2 + height).toFloat()
        )
        canvas.drawRoundRect(backgroundRect, rSize.toFloat(), rSize.toFloat(), mBackgroundPaint!!)

        if (max != 0) {
            val secondRight = (secondaryProgress.toFloat() / max * width).toInt()
            val secondProgressRect = RectF(
                leftPadding.toFloat(), (getHeight() / 2 - height).toFloat(),
                secondRight.toFloat(), (getHeight() / 2 + height).toFloat()
            )
            canvas.drawRoundRect(secondProgressRect, rSize.toFloat(), rSize.toFloat(), mSecondProgressPaint!!)

            val progressRight = (progress.toFloat() / max * width).toInt()
            val progressRect = RectF(
                leftPadding.toFloat(), (getHeight() / 2 - height).toFloat(),
                progressRight.toFloat(), (getHeight() / 2 + height).toFloat()
            )
            canvas.drawRoundRect(progressRect, rSize.toFloat(), rSize.toFloat(), mProgressPaint!!)


            /**
             * 如果没有事件响应那么就不绘制原点
             */
            if (draw_thumb_status == STATUS.NO_DRAW) return
            var cx = (progress.toFloat() / max * width).toInt()
            if (cx + rSize > width) {
                cx = width - rSize
            } else {
                cx = Math.max(cx, rSize)
            }
            val cy = getHeight() / 2

            canvas.drawText(
                progress.toString(),
                cx.toFloat() - mTextPaint!!.measureText(progress.toString()) / 2,
                cy.toFloat() - rSize.toFloat() - 5,
                mTextPaint!!
            )

            canvas.drawCircle(cx.toFloat(), cy.toFloat(), rSize.toFloat(), mThumbPaint!!)
        }
    }

    @Synchronized
    override fun setProgress(progress: Int) {
        if (mTrackTouch == TRACKTOUCH_NONE && max != 0) {
            super.setProgress(progress)
        }
        postInvalidate()
    }

    @Synchronized
    override fun setSecondaryProgress(secondaryProgress: Int) {
        super.setSecondaryProgress(secondaryProgress)
        postInvalidate()
    }

    @Synchronized
    override fun setMax(max: Int) {
        super.setMax(max)
        postInvalidate()
    }

    @Synchronized
    private fun setTrackTouch(trackTouch: Int) {
        this.mTrackTouch = trackTouch
    }

    /**
     * 设置背景颜色
     *
     * @param backgroundColor
     */
    fun setBackgroundPaintColor(backgroundColor: Int) {
        mBackgroundPaint!!.color = backgroundColor
        postInvalidate()
    }

    /**
     * 设置进度颜色
     *
     * @param progressColor
     */
    fun setProgressColor(progressColor: Int) {
        mProgressPaint!!.color = progressColor
        postInvalidate()
    }

    /**
     * 设置第二进度颜色
     *
     * @param secondProgressColor
     */
    fun setSecondProgressColor(secondProgressColor: Int) {
        mSecondProgressPaint!!.color = secondProgressColor
        postInvalidate()
    }

    /**
     * 设置游标颜色
     *
     * @param thumbColor
     */
    fun setThumbColor(thumbColor: Int) {
        mThumbPaint!!.color = thumbColor
        postInvalidate()
    }

    fun setOnChangeListener(onChangeListener: OnChangeListener) {
        this.mOnChangeListener = onChangeListener
    }

    fun setTrackingTouchSleepTime(mTrackingTouchSleepTime: Int) {
        this.mTrackingTouchSleepTime = mTrackingTouchSleepTime
    }

    interface OnChangeListener {
        /**
         * 进度改变
         *
         * @param seekBar
         */
        fun onProgressChanged(seekBar: DYSeekBar)

        /**
         * 开始拖动
         *
         * @param seekBar
         */
        fun onTrackingTouchStart(seekBar: DYSeekBar)

        /**
         * 拖动结束
         *
         * @param seekBar
         */
        fun onTrackingTouchFinish(seekBar: DYSeekBar)

    }


    public fun setDrawThumbStatus(status: STATUS) {
        draw_thumb_status = status;
    }

    enum class STATUS {
        DRAW,
        NO_DRAW
    }
}