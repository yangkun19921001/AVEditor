package com.devyk.ikavedit.widget

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.os.Handler
import android.os.Message
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import com.devyk.ikavedit.R

/**
 * <pre>
 *     author  : devyk on 2020-08-06 15:02
 *     blog    : https://juejin.im/user/578259398ac2470061f3a3fb/posts
 *     github  : https://github.com/yangkun19921001
 *     mailbox : yang1001yk@gmail.com
 *     desc    : This is RecordButtom
 * </pre>
 */
public class RecordButton : View {

    private var paint: Paint? = null
    private var mOnGestureListener: OnGestureListener? = null

    private var downColor: Int = 0
    private var upColor: Int = 0

    private var slideDis: Float = 0f

    private var radiusDis: Float = 0f
    private var currentRadius: Float = 0f
    private var downRadius: Float = 0f
    private var upRadius: Float = 0f

    private var strokeWidthDis: Float = 0f
    private var currentStrokeWidth: Float = 0f
    private var minStrokeWidth: Float = 0f
    private var maxStrokeWidth: Float = 0f

    private val mHandler = @SuppressLint("HandlerLeak")
    object : Handler() {
        override fun handleMessage(msg: Message) {
            mOnGestureListener?.onDown()
        }
    }

    private var isDown: Boolean = false
    private var downX: Float = 0f
    private var downY: Float = 0f

    internal var changeStrokeWidth: Boolean = false
    internal var isAdd: Boolean = false

    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init()
    }

    private fun init() {
        downColor = R.color.recordbutton_down_color
        upColor = R.color.recordbutton_up_color
        paint = Paint()
        paint?.isAntiAlias = true//抗锯齿
        paint?.style = Paint.Style.STROKE//画笔属性是空心圆
        currentStrokeWidth = resources.getDimension(R.dimen.dimen_10dp)
        paint?.strokeWidth = currentStrokeWidth//设置画笔粗细

        slideDis = resources.getDimension(R.dimen.dimen_10dp)
        radiusDis = resources.getDimension(R.dimen.dimen_3dp)
        strokeWidthDis = resources.getDimension(R.dimen.dimen_1dp) / 4

        minStrokeWidth = currentStrokeWidth
        maxStrokeWidth = currentStrokeWidth * 2
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        if (downRadius == 0f) {
            downRadius = width * 0.5f - currentStrokeWidth
            upRadius = width * 0.3f - currentStrokeWidth
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {

        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                val parent = parent as ViewGroup
                parent.requestDisallowInterceptTouchEvent(true)
                downX = event.rawX
                downY = event.rawY

                mHandler.sendEmptyMessageDelayed(0, 100)

                isDown = true
                invalidate()
            }
            MotionEvent.ACTION_MOVE -> {
            }
            MotionEvent.ACTION_UP -> {
                val parent1 = parent as ViewGroup
                parent1.requestDisallowInterceptTouchEvent(false)
                val upX = event.rawX
                val upY = event.rawY
                if (mHandler.hasMessages(0)) {
                    if (Math.abs(upX - downX) < slideDis && Math.abs(upY - downY) < slideDis) {
                        mOnGestureListener?.onClick()
                    }
                } else {
                    mOnGestureListener?.onUp()
                }
                initState()
            }
        }
        return true
    }

    fun initState() {
        isDown = false
        mHandler.removeMessages(0)
        invalidate()
    }

    fun setOnGestureListener(listener: OnGestureListener) {
        this.mOnGestureListener = listener
    }

    interface OnGestureListener {
        fun onDown()

        fun onUp()

        fun onClick()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        if (isDown) {
            paint?.color = ContextCompat.getColor(context, downColor)
            if (changeStrokeWidth) {
                if (isAdd) {
                    currentStrokeWidth += strokeWidthDis
                    if (currentStrokeWidth > maxStrokeWidth) isAdd = false
                } else {
                    currentStrokeWidth -= strokeWidthDis
                    if (currentStrokeWidth < minStrokeWidth) isAdd = true
                }
                paint?.strokeWidth = currentStrokeWidth
                currentRadius = width * 0.5f - currentStrokeWidth
            } else {
                if (currentRadius < downRadius) {
                    currentRadius += radiusDis
                } else if (currentRadius >= downRadius) {
                    currentRadius = downRadius
                    isAdd = true
                    changeStrokeWidth = true
                }
            }
            paint?.let {
                canvas.drawCircle(width / 2f, height / 2f, currentRadius, it)
            }
            invalidate()
        } else {
            changeStrokeWidth = true
            currentStrokeWidth = minStrokeWidth
            paint?.strokeWidth = currentStrokeWidth
            paint?.color = ContextCompat.getColor(context, downColor)
            if (currentRadius > upRadius) {
                currentRadius -= radiusDis
                invalidate()
            } else if (currentRadius < upRadius) {
                currentRadius = upRadius
                invalidate()
            }
            paint?.let {
                paint?.style = Paint.Style.FILL
                canvas.drawCircle(width / 2f, height / 2f, currentRadius, it)
                paint?.color = ContextCompat.getColor(context, upColor)
                paint?.style = Paint.Style.STROKE
                paint?.strokeWidth = currentStrokeWidth/2
                canvas.drawCircle(width / 2f, height / 2f, currentRadius + currentStrokeWidth*0.5f, it)
            }
        }
    }
}
