package ru.tensor.sbis.design.gallery.impl.store.primitives

import android.net.Uri
import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * Реализация mvi-сущности State
 */
internal sealed interface GalleryState : Parcelable {

    /**
     * Состояние "Контент"
     *
     * @property cameraStubVisible          Видна ли заглушка камеры
     * @property storageStubVisible         Видна ли заглушка хранилища
     * @property albums                     Список альбомов, полученных из MediaStore
     * @property type                       Тип отображения, см. [Type]
     * @property barConfig                  Конфиг верхней панели, см. [GalleryBarConfig]
     * @property selectedItemsIds           Список идентификаторов выбранных элементов
     * @property cameraSnapshotUri          Сгенерированный uri для снимка с камеры
     * @property isEnabledAddButton         Включена ли кнопка "Добавить"
     */
    @Parcelize
    data class Content(
        val cameraStubVisible: Boolean,
        val storageStubVisible: Boolean,
        val albums: Map<Int, GalleryAlbumItem>,
        val type: Type,
        val barConfig: GalleryBarConfig,
        val selectedItemsIds: MutableMap<Int, Int>,
        val cameraSnapshotUri: Uri?,
        val isEnabledAddButton: Boolean,
    ) : GalleryState {

        /** Тип отображения элементов */
        sealed interface Type : Parcelable {

            /**
             * Отображение медиа-элементов
             *
             * @property items      Список элементов
             * @property albumId    Идентификатор альбома, элементы которого отображаем
             */
            @Parcelize
            data class Media(
                val items: List<GalleryItem>,
                val albumId: Int
            ) : Type

            /**
             * Отображение альбомов с медиа
             *
             * @property items      Список альбомов
             */
            @Parcelize
            data class Albums(
                val items: List<GalleryAlbumItem>
            ) : Type
        }
    }

    /** @SelfDocumented */
    @Parcelize
    class Loading : GalleryState

    /** @SelfDocumented */
    @Parcelize
    class Stub : GalleryState
}