package ru.tensor.sbis.red_button.feature

import androidx.activity.ComponentActivity
import io.reactivex.Single
import ru.tensor.sbis.verification_decl.red_button.RedButtonActivatedProvider

/**
 * API компонента "Красной Кнопки", описывающий предоставляемый компонентом функционал.
 *
 * @author ra.stepanov
 */
interface RedButtonFeature : RedButtonActivatedProvider {

    /**
     * Подписаться на событие контроллера о том, что нужно показать заглушку.
     */
    fun subscribeOnRedButtonControllerCallback() {}

    /**
     * Проверить нужно ли показать заглушку.
     * @return [Single], излучающий true, если нужно, иначе false.
     */
    fun isLockedUi(): Single<Boolean> = Single.just(false)

    /**
     * Открыть заглушку, если требуется иначе, запустить делегат.
     * @param activity родительская активность.
     * @param noStubHandler делегат для обработки случая если заглушка не требуется.
     */
    fun openStubIfNeeded(activity: ComponentActivity, noStubHandler: () -> Unit) {}
}