package com.dede.android_eggs

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.PointF
import android.util.AttributeSet
import android.util.Log
import android.util.TypedValue
import android.view.MotionEvent
import android.view.View
import android.view.ViewConfiguration
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.view.ViewCompat
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt
import kotlin.random.Random

/**
 * Magic Sushi
 *
 * @author shhu
 * @since 2022/8/29
 */
class MagicSushi @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0,
) : ViewGroup(context, attrs, defStyleAttr) {

    companion object {
        private const val TAG = "MagicSushi"

        private const val SUSHI_SPACE = 8f  // 寿司间隔 dp

        private const val GRID_ROW = 7      // 行
        private const val GRID_COLUMN = 6   // 列

        private val sushiRes = intArrayOf(
            R.drawable.t_platlogo,
            R.drawable.s_platlogo,
            R.drawable.r_icon,
            R.drawable.q_icon,
            R.drawable.p_icon,
            R.drawable.o_icon,
            R.drawable.n_icon,
            R.drawable.m_icon,
            R.drawable.l_icon,
            R.drawable.ic_k_platlogo,
            R.drawable.j_platlogo,
            R.drawable.i_platlogo,
            R.drawable.h_platlogo,
            R.drawable.g_platlogo,
        )

        private val SUSHI_TYPE_COUNT = min(25, sushiRes.size)
    }

    private val sushiGrid = IntArray(GRID_ROW * GRID_COLUMN)
    // row * column
    // O x 0  1  2  3  4  5  column
    // y
    // 0   0, 0, 0, 0, 0, 0,
    // 1   0, 0, 0, 0, 0, 0,
    // 2   0, 0, 0, 0, 0, 0,
    // 3   0, 0, 0, 0, 0, 0,   
    // 4   0, 0, 0, 0, 0, 0,
    // 5   0, 0, 0, 0, 0, 0,
    // 6   0, 0, 0, 0, 0, 0,
    // row

    private val sushiRandom = Random

    private val Float.dp: Int
        get() = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
            this, context.resources.displayMetrics).roundToInt()

    init {
        for (i in sushiGrid.indices) {
            val sushi = sushiRes[sushiRandom.nextInt(SUSHI_TYPE_COUNT)]
            sushiGrid[i] = sushi
            addView(Sushi(context, sushi))
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val widthSize = MeasureSpec.getSize(widthMeasureSpec)

        setMeasuredDimension(widthSize, widthSize / GRID_COLUMN * GRID_ROW)

        val space = SUSHI_SPACE.dp
        val childWidthMeasureSpec = MeasureSpec.makeMeasureSpec(
            ((measuredWidth.toFloat() - space * (GRID_COLUMN + 1)) / GRID_COLUMN).roundToInt(),
            MeasureSpec.EXACTLY)
        val childHeightMeasureSpec = MeasureSpec.makeMeasureSpec(
            ((measuredHeight.toFloat() - space * (GRID_ROW + 1)) / GRID_ROW).roundToInt(),
            MeasureSpec.EXACTLY)
        for (i in 0 until childCount) {
            val sushi = getChildAt(i)
            // 1:1
            sushi.measure(childWidthMeasureSpec, childHeightMeasureSpec)
        }
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        val space = SUSHI_SPACE.dp
        for (i in 0 until childCount) {
            val sushi = getChildAt(i)
            val mc = i % GRID_COLUMN
            val mr = i / GRID_COLUMN
            println("x,y: ${mc},$mr")

            val cw = sushi.measuredWidth
            val ch = sushi.measuredHeight

            val left = space + cw * mc + space * mc
            val top = space + ch * mr + space * mr
            val right = left + cw
            val bottom = top + ch
            sushi.layout(left, top, right, bottom)
        }
    }

    override fun addView(child: View?, index: Int, params: LayoutParams?) {
        if (child !is Sushi) {
            throw IllegalArgumentException("MagicSushi only receive Sushi child views, $child")
        }
        super.addView(child, index, params)
    }

    private fun switch(child: Sushi, switchType: Sushi.SwitchType) {
        Log.i(TAG, "switch: $switchType")
    }

    private class Sushi @JvmOverloads constructor(context: Context, sushi: Int = -1) :
        AppCompatImageView(context) {

        private val slop = ViewConfiguration.get(context).scaledTouchSlop

        private val magicSushi: MagicSushi
            get() = parent as MagicSushi

        init {
            setImageResource(sushi)
            ViewCompat.setElevation(this, 0f)
        }

        enum class MoveType {
            LOCK,
            VERTICAL,
            HORIZONTAL
        }

        enum class SwitchType {
            L, T, R, B
        }

        private val dpt = PointF()
        private val spt = PointF()

        private var moveType = MoveType.LOCK

        @SuppressLint("ClickableViewAccessibility")
        override fun onTouchEvent(event: MotionEvent): Boolean {
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    spt.set(x, y)
                    dpt.set(event.x, event.y)
                    moveType = MoveType.LOCK
                    ViewCompat.setElevation(this, 1f)
                }
                MotionEvent.ACTION_MOVE -> {
                    val dx = event.x - dpt.x
                    val dy = event.y - dpt.y
                    if (moveType == MoveType.LOCK && max(abs(dx), abs(dy)) > slop) {
                        moveType =
                            if (abs(dx) >= abs(dy)) MoveType.HORIZONTAL else MoveType.VERTICAL
                    }
                    if (moveType == MoveType.HORIZONTAL) {
                        x += dx
                    } else if (moveType == MoveType.VERTICAL) {
                        y += dy
                    }
                }
                MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                    if (moveType == MoveType.HORIZONTAL && abs(x - spt.x) >= width / 2f) {
                        magicSushi.switch(this, if (x > spt.x) SwitchType.R else SwitchType.L)
                    } else if (moveType == MoveType.VERTICAL && abs(y - spt.y) >= height / 2f) {
                        magicSushi.switch(this, if (y > spt.y) SwitchType.B else SwitchType.T)
                    } else {
                        x = spt.x
                        y = spt.y
                    }
                    moveType = MoveType.LOCK
                    ViewCompat.setElevation(this, 0f)
                }
            }
            return true
        }
    }
}