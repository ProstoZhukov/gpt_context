/**
 * Набор инструментов для определения сцецифики отображения SbisTitleView.
 *
 * @author ns.staricyn
 */
package ru.tensor.sbis.design.profile.titleview.utils

import androidx.annotation.DimenRes
import ru.tensor.sbis.design.profile.R
import ru.tensor.sbis.design.profile_decl.titleview.Default
import ru.tensor.sbis.design.profile_decl.titleview.TitleViewContent

/**
 * Вью для отображения картинки нужно скрыть только в том случае, если список картинок пуст и тип контента [Default].
 */
fun isNeedHideImage(content: TitleViewContent): Boolean =
    content is Default && content.imageUrl.isEmpty()

/**
 * Получить размер для заголовка, когда он помещается на одной строке:
 * - есть подзаголовок - *MEDIUM*
 * - иначе - *LARGE*
 */
@DimenRes
fun chooseTitleTextSize(content: TitleViewContent): Int =
    when {
        content.subtitle.isNotEmpty() -> R.dimen.design_profile_sbis_title_view_title_text_size_medium
        else -> R.dimen.design_profile_sbis_title_view_title_text_size_large
    }

/**
 * Получить размер для заголовка, когда он не помещается на одной строке:
 * - есть подзаголовок - *MEDIUM*
 * - иначе - *SMALL*
 */
@DimenRes
fun chooseSmallerTitleTextSize(content: TitleViewContent): Int =
    when {
        content.subtitle.isNotEmpty() -> R.dimen.design_profile_sbis_title_view_title_text_size_medium
        else -> R.dimen.design_profile_sbis_title_view_title_text_size_small
    }

/**
 * Получить максимальное кол-во строк для заголовка:
 *
 * [singleLineTitle] - `true` или есть подзаголовок - 1, иначе - 2
 */
fun chooseTitleMaxLines(singleLineTitle: Boolean, content: TitleViewContent): Int =
    if (singleLineTitle || content.subtitle.isNotEmpty()) 1 else 2