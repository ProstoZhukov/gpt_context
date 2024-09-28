package ru.tensor.sbis.design.gallery.impl.store

import com.arkivanov.essenty.statekeeper.StateKeeper
import com.arkivanov.mvikotlin.core.store.SimpleBootstrapper
import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.core.store.StoreFactory
import ru.tensor.sbis.design.gallery.impl.store.primitives.GalleryAction
import ru.tensor.sbis.design.gallery.impl.store.primitives.GalleryIntent
import ru.tensor.sbis.design.gallery.impl.store.primitives.GalleryLabel
import ru.tensor.sbis.design.gallery.impl.store.primitives.GalleryState
import ru.tensor.sbis.mvi_extension.create
import javax.inject.Inject

internal typealias GalleryStore = Store<GalleryIntent, GalleryState, GalleryLabel>

private const val GALLERY_STORE_NAME = "gallery_store"

internal class GalleryStoreFactory @Inject constructor(
    private val storeFactory: StoreFactory,
    private val executor: GalleryExecutor,
    private val reducer: GalleryReducer
) {

    fun create(stateKeeper: StateKeeper, initialState: GalleryState): GalleryStore =
        object : GalleryStore by storeFactory.create(
            stateKeeper,
            name = GALLERY_STORE_NAME,
            initialState = initialState,
            bootstrapper = SimpleBootstrapper(GalleryAction.CheckPermissions),
            executorFactory = ::executor,
            reducer = reducer,
            saveStateSupplier = {
                if (it is GalleryState.Content) {
                    it.copy(
                        albums = emptyMap(),
                        type =
                        when (it.type) {
                            is GalleryState.Content.Type.Media -> it.type.copy(items = emptyList())
                            is GalleryState.Content.Type.Albums -> it.type.copy(items = emptyList())
                        }
                    )
                } else {
                    it
                }
            }
        ) {}
}