package ru.tensor.sbis.design.media_player.contract

import ru.tensor.sbis.communication_decl.videocall.bl.CallStateProvider
import ru.tensor.sbis.network_native.apiservice.contract.ApiService
import ru.tensor.sbis.verification_decl.login.LoginInterface

/**
 * Внешние зависимости модуля плеера для проигрывания аудио и видео сообщений.
 * @see LoginInterface.Provider
 * @see ApiService.Provider
 * @see CallStateProvider
 *
 * @author da.zhukov
 */
interface MediaPlayerDependency :
    LoginInterface.Provider,
    ApiService.Provider {

    val callStateProvider: CallStateProvider?
}