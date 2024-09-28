package ru.tensor.sbis.verification_decl.onboarding_tour.builders

import androidx.fragment.app.Fragment
import kotlinx.coroutines.flow.Flow

/**
 * Интерфейс команды выполняемой по завершении отображения тура онбординга.
 *
 * @author as.chadov
 */
fun interface DismissCommand {

    /**
     * Метод [invoke] будет вызван в главном потоке.
     */
    operator fun invoke()
}

/**
 * Интерфейс команды выполняемой перед уходом с конкретного экрана онбординга и настроек.
 *
 * @author as.chadov
 */
fun interface PageCommand {

    /**
     * Поведение в случае вытеснения активити.
     * По умолчанию [invoke] будет вызван повторно для продолжения работы и отслеживания результата команды по новому [Flow].
     */
    val restorable: Boolean get() = true

    /**
     * Метод [invoke] будет вызван в главном потоке.
     *
     * @param fragment онбординг
     * @param byUser инициировано пользователем
     * @return действие по завершению выполнения команды.
     */
    operator fun invoke(fragment: Fragment, byUser: Boolean): Flow<ResultantAction>

    /**
     * Результирующее действие на туре после выполнения [PageCommand].
     */
    enum class ResultantAction(val moveOn: Boolean) {
        /** Выполнить переход к следующей странице. */
        GO_AHEAD(true),

        /** Обновить тур и выполнить переход к следующей странице. */
        UPDATE_AND_GO(true),

        /** Действий не требуется. */
        NOTHING(false);
    }
}

/**
 * Интерфейс команды проверки необходимости отображения текущего экрана в туре онбординга.
 *
 * @author as.chadov
 */
fun interface PageRequiredCallback : () -> Boolean {

    /**
     * Метод [invoke] будет вызван в IO потоке.
     *
     * @return true если следует отображать.
     */
    override operator fun invoke(): Boolean
}

/**
 * Интерфейс команды сообщающей что произошел клик по кнопке в баннере.
 *
 * @author as.chadov
 */
fun interface BannerCommand {

    /**
     * Метод [invoke] будет вызван в главном потоке.
     */
    operator fun invoke()
}

/**
 * Интерфейс команды выполняемой при необходимости обоснования предоставления разрешений пользователем.
 *
 * @author as.chadov
 */
fun interface RationaleCallback {

    /**
     * Метод [invoke] будет вызван в главном потоке.
     *
     * @param permissions список разрешений экрана требующих обоснования.
     * @return true если после выполнения команды необходимо попытаться запросить разрешения повторно, иначе выполнить переход к следующей странице.
     */
    operator fun invoke(fragment: Fragment, permissions: List<String>): Flow<Boolean>
}
