package ru.tensor.sbis.edo_decl.doc_opener.card.config

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import ru.tensor.sbis.edo_decl.doc_opener.doc_model.DocModel

/**
 * Конфиг карточки документа
 *
 * @property docModel Модель документа для открытия в карточке
 *
 * @author sa.nikitin
 */
@Parcelize
class DocCardConfig(val docModel: DocModel) : Parcelable