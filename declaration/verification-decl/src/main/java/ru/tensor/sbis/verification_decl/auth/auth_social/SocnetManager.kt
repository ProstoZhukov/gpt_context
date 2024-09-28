package ru.tensor.sbis.verification_decl.auth.auth_social

import android.view.ViewGroup
import androidx.annotation.MainThread
import ru.tensor.sbis.verification_decl.auth.auth_social.data.SocialAuthData

/**
 * Внешний контракт работы со списком социальных сетей.
 *
 * @author ar.leschev
 */
interface SocnetManager {
    /** Целевой фрагмент прошел стадию инфлейта, передать [container] для встраивания списка. */
    fun create(container: ViewGroup)

    /** Установить режим. По-умолчанию - Вход. */
    fun setModeAndRebuild(newMode: Mode)

    /** @SelfDocumented */
    interface Callbacks {
        /** Если передавалась опция ограничения списка - действие на опциональный элемент "Более". */
        @MainThread
        fun onMoreClicked() = Unit

        /** Результат авторизации через соц.сеть [data]. */
        @MainThread
        fun onSocialResult(data: SocialAuthData)
    }

    /**
     *  Доступные режимы.
     *  Могут использоваться компонентом для фильтрации списка соц.сетей.
     */
    enum class Mode {
        /** Вход. */
        ENTER,
        /** Регистрация, иностранные соц.сети могут быть отключены в зависимости от стенда. */
        REG
    }
}