package ru.tensor.sbis.design.profile.personcollagelist.collagechildrenmanager

import android.view.View
import androidx.annotation.ColorInt
import ru.tensor.sbis.design.profile.imageview.PersonImageView
import ru.tensor.sbis.design.profile.personcollagelist.PersonCollageLineView
import ru.tensor.sbis.design.profile.personcollagelist.util.PersonCollageLineViewPool
import ru.tensor.sbis.design.profile_decl.person.Shape

/**
 * Контракт инструмента, управляющего дочерними элементами [PersonCollageLineView].
 *
 * @author us.bessonov
 */
internal interface PersonCollageLineViewChildrenManager {

    /**
     * Текущий список View отображаемых фото.
     */
    val children: List<PersonImageView>

    /** @SelfDocumented */
    fun setCollageView(view: View)

    /** @SelfDocumented */
    fun setViewPool(pool: PersonCollageLineViewPool)

    /** @SelfDocumented */
    fun setShape(shape: Shape)

    /** @SelfDocumented */
    fun setInitialsColor(@ColorInt color: Int)

    /**
     * Помещает все добавленные View в пул.
     */
    fun onDetachedFromWindow()

    /**
     * Обновляет изображения, если обнаружится, что среди них есть невалидные (после возможной очистки из-за нехватки
     * памяти).
     */
    fun reloadImagesIfRecycled()

    /**
     * Требуется ли выполнение layout при обновлении списка (может измениться ширина), или достаточно перерисовки.
     */
    fun isReLayoutRequired(newSize: Int, maxVisibleCount: Int, totalCount: Int): Boolean
}