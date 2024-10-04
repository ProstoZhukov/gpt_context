package ru.tensor.sbis.design.toolbar.multilinecollapsingtoolbar

import android.view.View
import androidx.annotation.Px

/**
 * Компонент шапки, используемый в [CollapsingToolbarLayout].
 * Контракт предназначен для обеспечения отрисовки сворачиваемого заголовка в том же виде, что и по умолчанию.
 *
 * @author us.bessonov
 */
internal interface CollapsingLayoutChildToolbar {

    /** @SelfDocumented */
    fun getView(): View

    /**
     * Добавляет технический view для нужд [CollapsingToolbarLayout]
     */
    fun addDummyView(view: View)

    /** @SelfDocumented */
    fun getTitle(): CharSequence?

    /** @SelfDocumented */
    @Px
    fun getCustomTitleTextSize(): Float? = null

    /** @SelfDocumented */
    @Px
    fun getTitleMarginStart(): Int

    /** @SelfDocumented */
    @Px
    fun getTitleMarginEnd(): Int

    /** @SelfDocumented */
    @Px
    fun getTitleMarginTop(): Int

    /** @SelfDocumented */
    @Px
    fun getTitleMarginBottom(): Int
}
