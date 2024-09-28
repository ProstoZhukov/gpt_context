package ru.tensor.sbis.design.profile.person.controller

import android.graphics.Canvas
import androidx.annotation.ColorInt
import androidx.annotation.Px
import ru.tensor.sbis.design.profile.person.ActivityStatusDrawable
import ru.tensor.sbis.design.profile.person.PersonView
import ru.tensor.sbis.design.profile.person.data.DisplayMode
import ru.tensor.sbis.person_decl.profile.model.ActivityStatus

/**
 * Контракт контроллера [PersonView].
 *
 * @author us.bessonov
 */
internal interface PersonViewController : PersonViewApi {

    /**
     * Hазмер фотографии в px. Ширина и высота.
     */
    val photoSizePx: Int

    /** @SelfDocumented */
    val activityStatus: ActivityStatus

    /** @SelfDocumented */
    val personFullName: String

    /** @SelfDocumented */
    val nodeInfoText: String

    /** @SelfDocumented */
    fun init(personView: PersonView, activityStatusDrawable: ActivityStatusDrawable)

    /**
     * Задаёт режим отображения. Применяется однократно, обрабатываются только значения, меняющие состояние.
     */
    fun setDisplayMode(mode: DisplayMode)

    /** @SelfDocumented */
    fun setCornerRadius(@Px radius: Float)

    /** @SelfDocumented */
    fun setInitialsColor(@ColorInt color: Int)

    /** @SelfDocumented */
    fun onSizeChanged(size: Int)

    /** @SelfDocumented */
    fun onMeasured()

    /** @SelfDocumented */
    fun performLayout()

    /** @SelfDocumented */
    fun performDraw(canvas: Canvas)

    /** @SelfDocumented */
    fun performInvalidate()

    /** @SelfDocumented */
    fun onVisibilityAggregated(isVisible: Boolean)

    /** @SelfDocumented */
    fun onViewDetachedFromWindow()

    /**
     * Установить статус активности для отображения в превью.
     */
    fun setPreviewActivityStatus()
}