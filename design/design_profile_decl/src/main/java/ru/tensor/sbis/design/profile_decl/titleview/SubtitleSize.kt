package ru.tensor.sbis.design.profile_decl.titleview

import androidx.annotation.DimenRes
import ru.tensor.sbis.design.profile_decl.R

/**
 * Перечисление возможных размеров текста подзаголовка `SbisTitleView`
 *
 * @author ns.staricyn
 */
@Suppress("unused")
enum class SubtitleSize(@DimenRes val sizeRes: Int) {

    /**
     * Малый размер текста
     */
    SMALL(R.dimen.design_profile_decl_sbis_title_view_subtitle_text_size_small),

    /**
     * Большой размер текста
     */
    LARGE(R.dimen.design_profile_decl_sbis_title_view_subtitle_text_size_large)
}