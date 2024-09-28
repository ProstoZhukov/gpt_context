package ru.tensor.sbis.communicator.themes_registry.ui.conversationparticipants

import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

/**
 * Расширение для получения ViewModel из Fragment.
 */
internal inline fun <reified VM : ViewModel> Fragment.getViewModel(): VM =
    ViewModelProvider(this).get(VM::class.java)
