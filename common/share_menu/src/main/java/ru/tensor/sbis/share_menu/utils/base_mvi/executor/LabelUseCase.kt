package ru.tensor.sbis.share_menu.utils.base_mvi.executor

/**
 * Use-case для выполнения широковещательных сообщений.
 *
 * @author vv.chekurda
 */
internal interface LabelUseCase<Environment> {

    /**
     * Исполнить широковещательное сообщение в среде [env].
     */
    fun perform(env: Environment)
}