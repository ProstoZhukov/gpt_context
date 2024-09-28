package ru.tensor.sbis.design.utils.insets

import android.view.View

/**
 * Устанавливает отступ дл содержимого вью, кода приходит системный инсет.
 *
 * @author ma.kolpakov
 */
fun addTopPaddingByInsets(view: View) {
    DefaultViewInsetDelegateImpl().initInsetListener(
        DefaultViewInsetDelegateParams(listOf(ViewToAddInset(view, listOf(IndentType.PADDING to Position.TOP))))
    )
}