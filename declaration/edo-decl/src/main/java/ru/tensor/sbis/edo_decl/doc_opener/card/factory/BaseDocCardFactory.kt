package ru.tensor.sbis.edo_decl.doc_opener.card.factory

import android.os.Parcelable
import androidx.fragment.app.Fragment

/**
 * Базовый интерфейс фабрики карточки документа
 *
 * @author sa.nikitin
 */
interface BaseDocCardFactory<in CONFIG : Parcelable> : Parcelable {

    /**
     * Создать новый [Fragment] карточки документа по конфигурации [config]
     */
    fun newDocCardFragment(config: CONFIG): Fragment
}