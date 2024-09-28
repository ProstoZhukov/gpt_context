package ru.tensor.sbis.mediaplayer.sbistube

import android.content.Context
import android.util.Pair
import androidx.media3.common.ErrorMessageProvider
import androidx.media3.common.ParserException
import androidx.media3.common.PlaybackException
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.HttpDataSource
import androidx.media3.exoplayer.ExoPlaybackException
import androidx.media3.exoplayer.hls.playlist.HlsPlaylistTracker
import androidx.media3.exoplayer.mediacodec.MediaCodecRenderer
import androidx.media3.exoplayer.mediacodec.MediaCodecUtil
import androidx.media3.exoplayer.source.UnrecognizedInputFormatException
import ru.tensor.sbis.mediaplayer.R
import java.io.IOException

@UnstableApi
/**
 * Класс-провайдер сообщения об ошибке на основании исключения [ExoPlaybackException]
 *
 * @author sa.nikitin
 */
class SbisTubeErrorMessageProvider(
    private val context: Context
) : ErrorMessageProvider<PlaybackException> {

    override fun getErrorMessage(exception: PlaybackException): Pair<Int, String> =
        Pair(
            0,
            if (exception !is ExoPlaybackException) {
                unknownError()
            } else {
                when (exception.type) {
                    ExoPlaybackException.TYPE_SOURCE -> dataSourceErrorMessage(exception.sourceException)
                    ExoPlaybackException.TYPE_RENDERER -> rendererErrorMessage(exception.rendererException)
                    ExoPlaybackException.TYPE_UNEXPECTED -> unknownError()
                    else -> unknownError()
                }
            }
        )

    private fun unknownError(): String = context.getString(R.string.media_sbis_tube_unknown_error)

    private fun dataSourceErrorMessage(exception: IOException): String {
        return if (
            exception is ParserException ||
            exception is HlsPlaylistTracker.PlaylistStuckException) {
            ""
        } else {
            context.getString(
                when (exception) {
                    is HttpDataSource.HttpDataSourceException -> R.string.media_sbis_tube_network_data_source_error
                    is UnrecognizedInputFormatException       -> R.string.media_sbis_tube_unrecognized_data_source_error
                    else                                      -> R.string.media_sbis_tube_data_source_error
                }
            )
        }
    }

    private fun rendererErrorMessage(exception: Exception): String =
        context.getString(
            when (exception) {
                is MediaCodecRenderer.DecoderInitializationException -> {
                    if (exception.codecInfo == null) {
                        when {
                            exception.cause is MediaCodecUtil.DecoderQueryException -> R.string.media_sbis_tube_querying_decoders_error
                            exception.secureDecoderRequired -> R.string.media_sbis_tube_no_secure_decoder_error
                            else -> R.string.media_sbis_tube_no_decoder_error
                        }
                    } else {
                        R.string.media_sbis_tube_instantiating_decoder_error
                    }
                }
                else -> R.string.media_sbis_tube_unknown_error
            }
        )
}