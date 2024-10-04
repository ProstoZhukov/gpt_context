package ru.tensor.sbis.design_dialogs.dialogs.container

import android.os.Bundle

/**
 * Интерфейс контейнера.
 *
 * @author sa.nikitin
 */
interface Container {

    /**
     * Обработать действие с контентом.
     *
     * @param actionId      - идентификатор действия
     * @param data          - данные по действию
     */
    fun onContentAction(actionId: String, data: Bundle?) = Unit

    /**
     * Интерфейс, декларирующий возможность закрытия контейнера.
     * Закрытие конейнера инициируется вызовом метода [.closeContainer].
     */
    interface Closeable {

        /**
         * Закрыть контейнер с контентом.
         */
        fun closeContainer()

    }

    /**
     * Интерфейс, декларирующий возможность указания момента
     * для отображения контента. Отображение контента инициируется
     * вызовом метода [.showContent].
     */
    interface Showable {

        /**
         * Отобразить контент внутри контейнера.
         */
        fun showContent()

    }

    /**
     * Интерфейс, декларирующий возможность отображения прогресса.
     */
    interface HasProgress {

        /**
         * Отобразить прогресс.
         */
        fun showProgress()

        /**
         * Скрыть прогресс.
         */
        fun hideProgress()

    }

    /**
     * Интерфейс, декларирующий возможность изменения размера контейнера.
     */
    interface Resizable {

        fun changeHeightParams(wrapContent: Boolean)
    }
}
