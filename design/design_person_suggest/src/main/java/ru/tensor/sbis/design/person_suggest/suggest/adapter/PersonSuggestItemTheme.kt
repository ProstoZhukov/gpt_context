package ru.tensor.sbis.design.person_suggest.suggest.adapter

import androidx.annotation.ColorInt
import androidx.annotation.Px
import ru.tensor.sbis.design.person_suggest.suggest.PersonSuggestView
import ru.tensor.sbis.design.profile_decl.person.PhotoSize

/**
 * Модель темы компонента панели выбора персоны [PersonSuggestView].
 *
 * @property backgroundColor цвет фона панели.
 * @property listHorizontalPadding величина горизонтальных отступов списка в px.
 * @property personHorizontalPadding величина горизонтальных отступов фотографии персоны в px.
 * @property personVerticalPadding величина вертикальных отступов фотографии персоны в px.
 * @property photoSize размер фото персоны.
 *
 * @author vv.chekurda
 */
internal data class PersonSuggestTheme(
    @ColorInt val backgroundColor: Int,
    @Px val listHorizontalPadding: Int,
    @Px val personHorizontalPadding: Int,
    @Px val personVerticalPadding: Int,
    val photoSize: PhotoSize
)