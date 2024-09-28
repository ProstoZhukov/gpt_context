package ru.tensor.sbis.mvi_extension.router

import androidx.lifecycle.LifecycleOwner
import ru.tensor.sbis.mvi_extension.router.navigator.Navigator

/**
 * Created by Aleksey Boldinov on 23.08.2022.
 */
interface Router<ENTITY : LifecycleOwner> {

    fun attachNavigator(navigator: Navigator<ENTITY>)
}