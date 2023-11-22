package com.daon.customtimer

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.util.Log
import android.view.View

class TimerView(context: Context, attrs: AttributeSet) : View(context, attrs) {
    private val paint = Paint()
    private var progress = 0
    private var customColor = Color.RED
    private lateinit var animator: ValueAnimator

    init {
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.TimerView)
        customColor = typedArray.getColor(R.styleable.TimerView_customColor, Color.RED)
        typedArray.recycle()
        paint.color = customColor
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        // 원 그리기
        val centerX = width / 2
        val centerY = height / 2
        val radius = minOf(centerX, centerY).toFloat()

        // progress에 따라 채워진 부분을 그립니다.
        val sweepAngle = (360f / 100) * progress
        canvas.drawArc(
            centerX - radius, centerY - radius, centerX + radius, centerY + radius,
            -90f, sweepAngle, true, paint
        )
    }

    // progress를 업데이트하는 메서드
    fun updateProgressAnimated(newProgress: Int) {
        if (::animator.isInitialized) {
            animator.cancel()
        }

        Log.e("Timer", "updateProgressAnimated -> newProgress: $newProgress")
        animator = ValueAnimator.ofInt(progress, newProgress)
        animator.addUpdateListener { valueAnimator ->
            progress = valueAnimator.animatedValue as Int
            invalidate()
        }
        animator.start()
    }
}