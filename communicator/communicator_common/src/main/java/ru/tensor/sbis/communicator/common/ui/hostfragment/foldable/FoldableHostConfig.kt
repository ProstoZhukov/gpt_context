package ru.tensor.sbis.communicator.common.ui.hostfragment.foldable

import android.content.Context
import android.os.Bundle
import androidx.annotation.IdRes
import androidx.fragment.app.FragmentManager

/**
 * Конфигурация foldable host фрагмента
 *
 * @property fragmentManager    менеджер фрагментов, в котором работает host фрагмент
 * @property resources          ресурсы
 * @property savedInstanceState сохраненное состояние фрагмента
 * @property masterContainerId  идентификатор контейнера master фрагментов
 * @property detailContainerId  идентификатор контейнера detail фрагментов
 * @property listener           слушатель изменения foldable состояний
 *
 * @author vv.chekurda
 */
data class FoldableHostConfig(
    val fragmentManager: FragmentManager,
    val context: Context,
    val savedInstanceState: Bundle?,
    @IdRes val masterContainerId: Int,
    @IdRes val detailContainerId: Int,
    val listener: FoldableStateChangeListener? = null
)
