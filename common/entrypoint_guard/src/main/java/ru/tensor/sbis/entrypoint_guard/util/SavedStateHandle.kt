package ru.tensor.sbis.entrypoint_guard.util

import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.SavedStateViewModelFactory
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

/**
 * Метод для получения VM.
 *
 * @author kv.martyshenko
 */
internal fun AppCompatActivity.getVM(): SavedStateHandleHolder {
    return ViewModelProvider(this, SavedStateViewModelFactory())[SavedStateHandleHolder::class.java]
}

/**
 * Хранитель [SavedStateHandle].
 *
 * @author kv.martyshenko
 */
internal class SavedStateHandleHolder(val savedStateHandle: SavedStateHandle) : ViewModel() {
    /**
     * Если уже выполнился метод onCreate активности.
     * Зануляется при уничтожении.
     */
    var isCreateFinished: Boolean = false
}