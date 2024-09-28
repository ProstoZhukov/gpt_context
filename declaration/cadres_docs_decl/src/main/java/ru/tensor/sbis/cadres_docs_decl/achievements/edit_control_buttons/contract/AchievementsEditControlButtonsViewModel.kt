package ru.tensor.sbis.cadres_docs_decl.achievements.edit_control_buttons.contract

import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.flow.SharedFlow
import ru.tensor.sbis.cadres_docs_decl.achievements.edit_control_buttons.EditControlEvent

/** Контракт ViewModel-и плавающей управляющей кнопки секции ПиВ */
interface AchievementsEditControlButtonsViewModel {

    /** Шина с событиями от управляющей кнопки */
    val editControlEvent: SharedFlow<EditControlEvent>

    /** Видимость управляющей кнопки */
    val controlBtnIsVisible: MutableLiveData<Boolean>
}