package ru.tensor.sbis.design_selection.ui.main.utils

import android.content.Context
import android.view.LayoutInflater
import androidx.annotation.AttrRes
import androidx.annotation.StyleRes
import ru.tensor.sbis.design.utils.ThemeContextBuilder
import ru.tensor.sbis.design_selection.R

/**
 * Создаёт копию [LayoutInflater], c кастомной темой из [primaryThemeResolver].
 *
 * @author vv.chekurda
 */
fun LayoutInflater.cloneWithSelectionTheme(
    context: Context,
    @AttrRes themeAttr: Int = R.attr.selectionTheme,
    @StyleRes defTheme: Int = R.style.SelectionTheme,
    primaryThemeResolver: (() -> Int?)? = null
): LayoutInflater =
    cloneInContext(
        ThemeContextBuilder(
            context = context,
            defStyleAttr = themeAttr,
            defaultStyle = defTheme,
            primaryThemeResolver = primaryThemeResolver
        ).build()
    )