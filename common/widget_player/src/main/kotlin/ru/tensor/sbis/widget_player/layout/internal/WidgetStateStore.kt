package ru.tensor.sbis.widget_player.layout.internal

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelStore
import ru.tensor.sbis.widget_player.converter.WidgetID

/**
 * У каждого виджета своя песочница в виде [ViewModelStore].
 *
 * @author am.boldinov
 */
internal class WidgetStateStore : ViewModel() {

    private val states = mutableMapOf<WidgetID, ViewModelStore>()

    fun getOrCreate(id: WidgetID): ViewModelStore {
        return states.getOrPut(id) {
            ViewModelStore()
        }
    }

    fun remove(id: WidgetID) {
        states[id]?.clear()
        states.remove(id)
    }

    override fun onCleared() {
        super.onCleared()
        states.values.forEach {
            it.clear()
        }
        states.clear()
    }
}