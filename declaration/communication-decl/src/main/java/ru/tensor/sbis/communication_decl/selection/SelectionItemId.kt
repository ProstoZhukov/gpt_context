package ru.tensor.sbis.communication_decl.selection

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.util.UUID

/**
 * ID элемента в компоненте выбора.
 *
 * @author vv.chekurda
 */
interface SelectionItemId : Parcelable

/**
 * Дефолтная реализация идентификатора элемента в компоненте выбора.
 */
@Parcelize
data class DefaultSelectionItemId(val value: UUID) : SelectionItemId