package ru.tensor.sbis.edo_decl.passage.config

import android.os.Parcelable
import androidx.annotation.IdRes
import kotlinx.parcelize.Parcelize

/**
 * Конфигурация отображения компонента переходов
 *
 * @property showInitialProgressBar     Отображать ли прогресс бар при первичном выполнении, т.е.
 *                                      до отображения списка переходов, или диалога активации сертификата
 * @property popoverAnchor              Идентификатор вью, над которой расположится UI компонента.
 *                                      Используется только на планшетах.
 *
 * @author sa.nikitin
 */
@Deprecated("Больше не используется")
@Parcelize
data class PassageUiConfig(
    val showInitialProgressBar: Boolean = false,
    @Deprecated("Больше не используется")
    @IdRes val popoverAnchor: Int? = null
) : Parcelable