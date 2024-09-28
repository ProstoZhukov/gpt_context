package ru.tensor.sbis.design.profile.personcollagelist.util

import android.content.Context
import android.graphics.drawable.Drawable
import androidx.annotation.ColorInt
import androidx.annotation.Px
import ru.tensor.sbis.design.profile.person.UserInitialsDrawable
import ru.tensor.sbis.design.profile_decl.person.InitialsStubData

/**
 * Выполняет создание [Drawable] заглушки с инициалами.
 *
 * @author us.bessonov
 */
internal interface InitialsDrawableFactory {

    /** @SelfDocumented */
    fun createDrawable(
        context: Context,
        @ColorInt
        initialsColor: Int,
        @Px
        initialsTextSize: Float?,
        data: InitialsStubData,
        initialsEnabled: Boolean
    ): Drawable
}

/**
 * Реализация создания [Drawable] заглушки с инициалами по умолчанию.
 *
 * @author us.bessonov
 */
internal object DefaultInitialsDrawableFactory : InitialsDrawableFactory {

    override fun createDrawable(
        context: Context,
        initialsColor: Int,
        initialsTextSize: Float?,
        data: InitialsStubData,
        initialsEnabled: Boolean
    ) = UserInitialsDrawable(
        context,
        initialsColor,
        initialsTextSize,
        data.getInitialsBackgroundColor(context),
        data.initials,
        initialsEnabled
    )
}
