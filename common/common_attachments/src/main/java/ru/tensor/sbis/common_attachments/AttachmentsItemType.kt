package ru.tensor.sbis.common_attachments

import androidx.annotation.IntDef

import kotlin.annotation.Retention

/*** @SelfDocumented */
internal const val FIRST_ELEMENT = -1
/*** @SelfDocumented */
internal const val MIDDLE_ELEMENT = 0
/*** @SelfDocumented */
internal const val LAST_ELEMENT = 1

/**
 * Перечень типов элементов у [AttachmentsContainerAdapter]
 *
 * @author sa.nikitin
 * @since 6/13/2019
 */
@IntDef(value = [FIRST_ELEMENT, MIDDLE_ELEMENT, LAST_ELEMENT])
@Retention(AnnotationRetention.SOURCE)
internal annotation class AttachmentsItemType
