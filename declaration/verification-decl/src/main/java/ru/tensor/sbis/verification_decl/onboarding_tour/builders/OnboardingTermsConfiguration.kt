package ru.tensor.sbis.verification_decl.onboarding_tour.builders

import androidx.annotation.StringRes
import ru.tensor.sbis.verification_decl.onboarding_tour.data.OnboardingTour

/**
 * Интерфейс билдера для создания подписи условий использования [OnboardingTour].
 *
 * @author as.chadov
 */
interface OnboardingTermsConfiguration {

    /**
     * Опциональный ресурс полной подписи с текстом условий и положений.
     * Области для ссылок выделяются двойным символом ASCII номер 37 см. %%.
     * По умолчанию: "Продолжая, вы соглашаетесь с %%правилами сервиса%% и %%политикой конфиденциальности%%".
     */
    @get:StringRes
    var caption: Int

    /**
     * Список ссылок. Для каждого #текст для выделения# передавать одну ссылку.
     */
    var links: List<String>
}