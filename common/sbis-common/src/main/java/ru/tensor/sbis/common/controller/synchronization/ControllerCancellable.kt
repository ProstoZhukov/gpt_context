package ru.tensor.sbis.common.controller.synchronization

import androidx.annotation.MainThread
import androidx.fragment.app.FragmentManager

/**
 * Интерфейс для отмены асинхронных операций контроллера, которые были вызваны CRUD методом list().
 * Должен быть использован при уходе из реестра для отмены синхронизаций.
 */
interface ControllerCancellable {

    /**
     * Вызвать у контроллера (контроллеров) метод cancelAll.
     *
     * cancelAll - мгновенный неблокирующий метод, поэтому метод [cancelControllerSynchronizations] будет вызван на UI потоке.
     * Детали: https://online.sbis.ru/page/dialog/123a3629-eecc-469f-94d5-116010df0c49?message=959c42ae-00d5-40c7-b218-e160f53ba636&inviteduser=6148dfb3-2e78-4328-89f3-6cff9625ceae
     */
    @MainThread
    fun cancelControllerSynchronizations()
}