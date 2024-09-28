package ru.tensor.sbis.edo_decl.doc_opener.card.config

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import ru.tensor.sbis.edo_decl.doc_opener.doc_model.DocModel

/**
 * Конфиг карточки документа с УПФ
 *
 * @property docModel            Модель документа для открытия в карточке
 * @property printFormAbsPath    Абсолютный путь до html файла УПФ в внутреннем кэше приложения
 *
 * @author sa.nikitin
 */
@Parcelize
class PrintFormDocCardConfig(val docModel: DocModel, val printFormAbsPath: String) : Parcelable