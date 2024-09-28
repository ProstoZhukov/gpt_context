package ru.tensor.sbis.list.base.presentation

import android.content.Context
import androidx.lifecycle.MutableLiveData
import ru.tensor.sbis.design.stubview.StubViewContent

/**
 * Псевдонимы данных.
 */
typealias StubViewContentFactory = (Context) -> StubViewContent
typealias StubLiveData = MutableLiveData<StubViewContentFactory>
typealias BooleanLiveData = MutableLiveData<Boolean>