package ru.tensor.sbis.pin_code.decl

/**
 * Результат успешного выполения запроса проверки введенного пин-кода.
 * @param data результат выполнения [PinCodeRepository.onCodeEntered]
 *
 * @author mb.kruglova
 */
class PinCodeSuccessResult<RESULT>(val data: RESULT)