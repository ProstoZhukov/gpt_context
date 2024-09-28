package ru.tensor.sbis.red_button.data

/**
 * Класс для хранения состояний данных для "Красной кнопки" и передачи значений во фрагменты
 * @property operationUuid uuid операции (используется для идентификации в контроллере)
 * @property phone         телефон на который придёт смс с кодом подверждения
 * @property pin           5-ти значный пин код
 *
 * @author ra.stepanov
 */
class RedButtonData {
    var operationUuid: String = ""
    var phone: String = ""
    var pin: String = ""
}