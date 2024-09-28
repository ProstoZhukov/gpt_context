package ru.tensor.sbis.business.common.testUtils

import android.content.res.Resources
import org.robolectric.annotation.Implementation
import org.robolectric.annotation.Implements
import org.robolectric.annotation.RealObject
import org.robolectric.shadow.api.Shadow
import ru.tensor.sbis.design.profile.titleview.TitleTextView

/**
 * Призрак [Resources] для исправления возможных [Resources.NotFoundException].
 *
 * Повод: после перехода на использование нетранзитивных R классов некоторые из ресурсов стали недоступны, пока что
 * иной способ не был найден.
 * Например "text_span_dialog_title_view_title_names_smaller_size" используемый в профиле [TitleTextView] через ресурс c id
 * "text_span_dialog_title_view_title_names_smaller_size" не добавленного в зависимости модуля "text_span"
 */
@Implements(Resources::class)
internal class ShadowResources {

    @RealObject
    private lateinit var resources: Resources

    @Suppress("unused")
    @Implementation
    fun getDimension(
        id: Int
    ): Float = try {
        Shadow.directlyOn(resources, Resources::class.java).getDimension(id)
    } catch (e: Throwable) {
        14.0f
    }
}