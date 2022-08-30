package com.dede.android_eggs

import android.os.Bundle
import android.view.Gravity
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity

/**
 * Created by shhu on 2022/8/30 13:39.
 *
 * @author shhu
 * @since 2022/8/30
 */
class MagicSushiActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val magicSushi = MagicSushi(this)
        val layoutParams = FrameLayout.LayoutParams(
            FrameLayout.LayoutParams.WRAP_CONTENT,
            FrameLayout.LayoutParams.WRAP_CONTENT)
            .apply { gravity = Gravity.CENTER }
        setContentView(magicSushi, layoutParams)
    }
}