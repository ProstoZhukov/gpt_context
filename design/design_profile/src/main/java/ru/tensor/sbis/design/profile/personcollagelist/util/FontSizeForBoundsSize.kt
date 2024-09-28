package ru.tensor.sbis.design.profile.personcollagelist.util

import androidx.annotation.Px

/**
 * Размер шрифта при ширине контейнера не меньшей, чем [boundsSize].
 *
 * @author us.bessonov
 */
internal data class FontSizeForBoundsSize(@Px val boundsSize: Int, @Px val textSize: Float)