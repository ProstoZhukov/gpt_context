package ru.tensor.sbis.mvi_extension.router.navigator

import androidx.lifecycle.LifecycleOwner

/**
 * Created by Aleksey Boldinov on 23.08.2022.
 */
interface Navigator<ENTITY : LifecycleOwner> {

    fun execute(command: (ENTITY) -> Unit)
}