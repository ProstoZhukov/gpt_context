/**
 * Файл с функциями для работы с [SbisTabViewItemContent].
 *
 * @author da.zolotarev
 */
package ru.tensor.sbis.design.tabs.util

import ru.tensor.sbis.design.tabs.api.SbisTabViewItemContent

/**
 * Проверить наличие текстовых [SbisTabViewItemContent].
 */
internal fun List<SbisTabViewItemContent>.isContainsTextContent() =
    any { it is SbisTabViewItemContent.Text || it is SbisTabViewItemContent.AdditionalText }

/**
 * Проверить наличие [SbisTabViewItemContent] с иконками кастомного размера.
 */
internal fun List<SbisTabViewItemContent>.isContainsCustomSizeImage() =
    any { it is SbisTabViewItemContent.Icon && it.customDimen != null }