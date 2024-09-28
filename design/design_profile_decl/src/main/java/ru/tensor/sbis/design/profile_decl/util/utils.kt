/**
 * Вспомогательные инструменты, связанные с отображением фото сотрудника.
 *
 * @author us.bessonov
 */
package ru.tensor.sbis.design.profile_decl.util

import androidx.annotation.Px

/**
 * Определяет размер текста для инициалов в заглушке изображения, в зависимости от размера изображения.
 */
@Px
fun calculateInitialsTextSize(@Px imageSize: Float) = imageSize / 2