package ru.tensor.sbis.communication_decl.selection

import androidx.annotation.StyleRes
import java.io.Serializable

/**
 * Конфигурация компонента выбора.
 *
 * @property useCase сценарий использования компонента.
 * @property selectionMode режим выбора данных.
 * @property doneButtonMode режим кнопки подтверждения выбора.
 * @property headerMode режим отображения шапки.
 * @property itemsLimit ограничение по количеству отображаемых элементов.
 * @property excludeList список исключений из отображаемой выборки.
 * @property requestKey ключ уникальности результата выбора.
 * @property enableSwipeBack доступность свайпбэка на фрагменте выбора.
 * @property stringsConfig (опционально) настройки текста в компоненте.
 * @property isFinalComplete Мод механики завершения. true - если подтверждение выбора несет финальный характер.
 * Т.е. после окончательного выбора компонент превратится в тыкву,
 * и в момент перехода к следующему экрану не будет никаких лишних перестроений (промаргиваний)
 * интерфейса.
 * В случае, если компонент выбора после завершения будет оставаться видимым
 * и к нему можно будет вернуться, чтобы изменить выбор - признак должен быть false.
 * @property themeRes прикладная тема, которая будет применена программно.
 *
 * @author vv.chekurda
 */
interface SelectionConfig : Serializable {
    val useCase: SelectionUseCase
    val selectionMode: SelectionMode
    val doneButtonMode: SelectionDoneButtonVisibilityMode
    val headerMode: SelectionHeaderMode
    val itemsLimit: Int?
    val excludeList: List<SelectionItemId>?
    val requestKey: String
    val enableSwipeBack: Boolean

    val stringsConfig: SelectionStringsConfig?
        get() = null

    val isFinalComplete: Boolean
        get() = true

    @get:StyleRes val themeRes: Int?
}