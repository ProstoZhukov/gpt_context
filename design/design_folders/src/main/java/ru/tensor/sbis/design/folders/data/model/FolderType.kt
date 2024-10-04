package ru.tensor.sbis.design.folders.data.model

import androidx.annotation.StringRes
import androidx.core.content.res.ResourcesCompat
import ru.tensor.sbis.design.R
import ru.tensor.sbis.design.folders.data.model.FolderActionType.*

/**
 * Тип папки. Влияет на отображение иконки и набора действий по свайпу
 *
 * @param iconRes ресур иконки. По-умолчанию без иконки
 * @param actions список экшенов по свайпу
 *
 * @author ma.kolpakov
 */
enum class FolderType(
    @StringRes internal val iconRes: Int = ResourcesCompat.ID_NULL,
    internal val actions: List<FolderActionType> = emptyList(),
) {

    /** Обычная. Без иконки, без экшенов */
    DEFAULT,

    /** Редактируемая. Без иконки, с экшенами [RENAME], [CREATE], [DELETE] */
    EDITABLE(actions = listOf(RENAME, CREATE, DELETE)),

    /** Можно только удалить. Без иконки */
    DELETABLE(actions = listOf(DELETE)),

    /** С настройками. Тонкая угловая стрелка влево, с экшенами [RENAME], [CREATE], [DELETE] */
    WITH_SETTINGS(iconRes = R.string.design_mobile_icon_enter, actions = listOf(RENAME, CREATE, DELETE)),

    /** Расшаренная. Иконка толстой закруглённой стрелки вправо, экшен [UNSHARE] */
    SHARED(iconRes = R.string.design_mobile_icon_arrow_black, actions = listOf(UNSHARE)),

    /** На приемке. Иконка галочки с часами, без экшенов */
    ON_ACCEPTANCE(iconRes = R.string.design_mobile_icon_partially_ready);

    /** @SelfDocumented */
    internal val hasIcon: Boolean = iconRes != ResourcesCompat.ID_NULL

    /** @SelfDocumented */
    internal val hasActions: Boolean = actions.isNotEmpty()
}
