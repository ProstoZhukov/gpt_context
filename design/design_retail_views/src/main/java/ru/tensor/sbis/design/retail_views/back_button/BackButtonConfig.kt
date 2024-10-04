package ru.tensor.sbis.design.retail_views.back_button

import androidx.annotation.StringRes
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

/** Модель кнопки "Назад" в тулбаре. */
data class BackButtonConfig(
    val backNavigationButtonVisible: LiveData<Boolean> = MutableLiveData(false),
    val backNavigationButtonTitle: LiveData<CharSequence>? = null,
    @StringRes val backNavigationButtonTitleRes: LiveData<Int>? = null,
    val actionOnBackNavigationButton: LiveData<() -> Unit>
)