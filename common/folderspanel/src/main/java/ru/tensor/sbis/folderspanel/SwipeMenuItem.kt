package ru.tensor.sbis.folderspanel

import androidx.annotation.ColorRes
import ru.tensor.sbis.design.R
import ru.tensor.sbis.design.SbisMobileIcon

/**@SelfDocumented*/
data class SwipeMenuItem(val icon: SbisMobileIcon.Icon,
                         @ColorRes val iconColor: Int = R.color.text_color_accent_2,
                         val clickAction : Runnable)