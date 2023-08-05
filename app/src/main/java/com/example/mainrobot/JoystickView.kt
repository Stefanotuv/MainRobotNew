package com.example.mainrobot


import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.PointF
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import kotlin.math.atan2
import kotlin.math.min
import kotlin.math.pow
import kotlin.math.sqrt

class JoystickView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private var outerCircleRadius = 0f
    private var innerCircleRadius = 0f
    private val outerCirclePaint: Paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val innerCirclePaint: Paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val centerPoint = PointF(0f, 0f)
    private val touchPoint = PointF(0f, 0f)
    private var callback: JoystickListener? = null

    init {
        outerCirclePaint.color = Color.GRAY
        outerCirclePaint.style = Paint.Style.FILL

        innerCirclePaint.color = Color.DKGRAY
        innerCirclePaint.style = Paint.Style.FILL
    }

    override fun onSizeChanged(width: Int, height: Int, oldWidth: Int, oldHeight: Int) {
        super.onSizeChanged(width, height, oldWidth, oldHeight)
        val viewDimension = min(width, height)
        outerCircleRadius = viewDimension / 2f
        innerCircleRadius = outerCircleRadius / 2f
        centerPoint.set(width / 2f, height / 2f)
        touchPoint.set(centerPoint)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.drawCircle(centerPoint.x, centerPoint.y, outerCircleRadius, outerCirclePaint)
        canvas.drawCircle(touchPoint.x, touchPoint.y, innerCircleRadius, innerCirclePaint)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN, MotionEvent.ACTION_MOVE -> {
                val x = event.x
                val y = event.y
                val distance = calculateDistance(x, y, centerPoint.x, centerPoint.y)
                if (distance <= outerCircleRadius) {
                    touchPoint.set(x, y)
                } else {
                    val scaleFactor = outerCircleRadius / distance
                    val newX = centerPoint.x + (x - centerPoint.x) * scaleFactor
                    val newY = centerPoint.y + (y - centerPoint.y) * scaleFactor
                    touchPoint.set(newX, newY)
                }
                callback?.onJoystickMoved(calculateJoystickOffsetX(), calculateJoystickOffsetY())
                invalidate()
                return true
            }
            MotionEvent.ACTION_UP -> {
                touchPoint.set(centerPoint)
                callback?.onJoystickReleased()
                invalidate()
                return true
            }
        }
        return super.onTouchEvent(event)
    }

    private fun calculateDistance(x1: Float, y1: Float, x2: Float, y2: Float): Float {
        val dx = x1 - x2
        val dy = y1 - y2
        return sqrt(dx.pow(2) + dy.pow(2))
    }

    private fun calculateJoystickOffsetX(): Float {
        return (touchPoint.x - centerPoint.x) / outerCircleRadius
    }

    private fun calculateJoystickOffsetY(): Float {
        return (touchPoint.y - centerPoint.y) / outerCircleRadius
    }

    fun setJoystickListener(listener: JoystickListener) {
        callback = listener
    }

    interface JoystickListener {
        fun onJoystickMoved(x: Float, y: Float)
        fun onJoystickReleased()
    }
}
