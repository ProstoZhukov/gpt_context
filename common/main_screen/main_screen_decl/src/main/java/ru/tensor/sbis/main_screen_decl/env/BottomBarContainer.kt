package ru.tensor.sbis.main_screen_decl.env

import android.view.View
import androidx.annotation.IdRes

interface BottomBarContainer {
    fun addView(view: View, position: Int)

    fun removeView(@IdRes containerId: Int)
}