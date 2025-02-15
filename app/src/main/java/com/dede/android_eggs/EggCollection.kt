package com.dede.android_eggs

import android.content.Context
import android.content.res.TypedArray
import android.util.AttributeSet
import androidx.preference.PreferenceCategory

/**
 * Egg
 *
 * @author hsh
 * @since 2020/11/11 2:31 PM
 */
class EggCollection : PreferenceCategory {

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    private var clickCount = 0
    private var isChecked = false

    init {
        isPersistent = true
    }

    override fun onClick() {
        if (clickCount < 4) {
            clickCount++
            return
        }
        clickCount = 0
        val newValue = !isChecked()
        if (callChangeListener(newValue)) {
            setChecked(newValue)
        }
    }

    override fun onGetDefaultValue(a: TypedArray, index: Int): Boolean {
        return a.getBoolean(index, false)
    }

    override fun onSetInitialValue(defaultValue: Any?) {
        setChecked(getPersistedBoolean(defaultValue as? Boolean ?: false))
    }

    fun setChecked(newValue: Boolean) {
        val changed = newValue != this.isChecked
        if (changed) {
            this.isChecked = newValue
            persistBoolean(newValue)
            notifyChanged()
        }
    }

    fun isChecked(): Boolean {
        return isChecked
    }

    override fun isEnabled(): Boolean {
        return true
    }

    override fun isSelectable(): Boolean {
        return true
    }
}