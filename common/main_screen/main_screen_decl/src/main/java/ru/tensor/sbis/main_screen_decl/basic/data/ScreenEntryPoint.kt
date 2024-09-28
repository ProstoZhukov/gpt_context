package ru.tensor.sbis.main_screen_decl.basic.data

import android.view.View
import androidx.annotation.IdRes
import ru.tensor.sbis.design.SbisMobileIcon
import ru.tensor.sbis.design.profile_decl.person.PersonData
import ru.tensor.sbis.design.theme.res.SbisString

/**
 * Элемент, вызывающий открытие нового экрана.
 *
 * @author us.bessonov
 */
sealed class ScreenEntryPoint(val id: ScreenId) {

    /**
     * Кнопка с иконкой и опциональным счётчиком.
     *
     * @param id идентификатор ассоциируемого экрана.
     * @param icon иконка.
     * @param counter конфигурация счётчика, отображаемого у кнопки.
     */
    class Icon(
        id: ScreenId,
        val icon: Char,
        val counter: Counter? = null
    ) : ScreenEntryPoint(id) {

        constructor(
            id: ScreenId,
            icon: SbisMobileIcon.Icon,
            counter: Counter? = null
        ) : this(id, icon.character, counter)
    }

    /**
     * Фото персоны (профиля).
     *
     * @param photoData данные фото профиля.
     */
    class Profile(val photoData: PersonData) : ScreenEntryPoint(ScreenId.Tag("Profile"))

    /**
     * Пункт меню.
     *
     * @param id идентификатор ассоциируемого экрана.
     * @param title текст пункта меню.
     */
    class MenuItem(id: ScreenId, val title: SbisString) : ScreenEntryPoint(id)

    /**
     * Произвольный [View].
     *
     * @param id идентификатор ассоциируемого экрана.
     * @param view новый [View] для вызова прикладного экрана.
     */
    class CustomView(id: ScreenId, val view: View) : ScreenEntryPoint(id)

    /**
     * Произвольный [View], который уже присутствует в иерархии.
     *
     * @param id идентификатор ассоциируемого экрана.
     * @param viewId идентификатор существующего [View] для вызова прикладного экрана.
     */
    class ViewLocator(id: ScreenId, @IdRes val viewId: Int) : ScreenEntryPoint(id)
}