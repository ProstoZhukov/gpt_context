package ru.tensor.sbis.verification_decl.onboarding_tour.data

import androidx.annotation.StringRes
import androidx.core.content.res.ResourcesCompat.ID_NULL
import ru.tensor.sbis.verification_decl.onboarding_tour.data.BannerButtonType.SKIP

/**
 * Тип кнопки на баннере экрана тура.
 *
 * @param caption опциональная замена подписи для кнопки [SKIP]. По умолчанию "Пропустить".
 */
enum class BannerButtonType(@StringRes var caption: Int? = ID_NULL) {

    /** Отображение кнопки закрытия тура (кнопка с крестиком). */
    CLOSE,

    /**
     * Отображение кнопки пропуска перехода тура (кнопка "Пропустить"), т.е. текстовая кнопка для пропуска одной страницы.
     * Для последней страницы будет закрывать тура как isClosable.
     */
    SKIP,

    /** Кнопка отсутствует. */
    NONE;
}