package ru.tensor.sbis.design.media_player.contract

import android.content.Context
import androidx.media3.common.util.UnstableApi
import ru.tensor.sbis.communication_decl.communicator.media.MediaPlayer
import ru.tensor.sbis.communication_decl.communicator.media.MediaPlayerFeature
import ru.tensor.sbis.communication_decl.communicator.media.MediaPlayerSessionHelper
import ru.tensor.sbis.communication_decl.communicator.media.ProximityHelper
import ru.tensor.sbis.design.media_player.MediaPlayerImpl
import ru.tensor.sbis.design.media_player.helpers.MediaPlayerSessionHelperImpl
import ru.tensor.sbis.design.media_player.proximity.ProximityHelperImpl
import ru.tensor.sbis.network_native.apiservice.contract.ApiService
import ru.tensor.sbis.verification_decl.login.LoginInterface

@UnstableApi
/**
 * Реализация [MediaPlayerFeature].
 *
 * @author rv.krohalev
 */
class MediaPlayerFeatureImpl(
    private val appContext: Context,
    loginInterface: LoginInterface,
    apiService: ApiService
) : MediaPlayerFeature {

    private val mediaPlayer by lazy {
        MediaPlayerImpl(appContext, loginInterface, apiService)
    }

    override fun getMediaPlayer(): MediaPlayer =
        mediaPlayer

    override fun getProximityHelper(): ProximityHelper =
        ProximityHelperImpl(mediaPlayer, appContext)

    override fun getMediaPlayerSessionHelper(): MediaPlayerSessionHelper =
        MediaPlayerSessionHelperImpl()
}