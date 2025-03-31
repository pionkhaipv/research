package pion.tech.pionbase.core.presentation.common

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import androidx.core.animation.doOnEnd
import androidx.core.graphics.toColorInt

class LoadingView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {
    private var progress: Float = 0f
    private var duration: Long = 12000
    private var paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private var colorProgress: Int = Color.RED
    private var colorBackground: Int = Color.GRAY
    private var anim: ValueAnimator? = null

    init {
        colorProgress = "#218380".toColorInt()
        colorBackground = "#E1E1E1".toColorInt()
    }

    // khoảng từ 0->1
    fun setProgress(value: Float) {
        progress = value
        postInvalidate()
    }

    override fun onDraw(canvas: Canvas) {
        canvas.apply {
            val viewHeight = height.toFloat()
            val viewWidth = width.toFloat()
            paint.color = colorBackground
            drawRoundRect(0f, 0f, viewWidth, viewHeight, viewHeight, viewHeight, paint)
            paint.color = colorProgress
            drawRoundRect(0f, 0f, viewWidth * progress, viewHeight, viewHeight, viewHeight, paint)
        }
    }

    fun endAnim() {
        anim?.end()
    }

    fun startAnim(duration: Long, onSuccess: () -> Unit = {}) {
        anim?.cancel()
        anim = ValueAnimator.ofFloat(1f)
        anim?.addUpdateListener {
            progress = it.animatedValue as Float
            postInvalidate()
        }
        anim?.doOnEnd {
            onSuccess.invoke()
        }
        anim?.duration = duration
        anim?.start()
    }
}