package ru.tensor.sbis.communicator.sbis_conversation.utils

import android.net.Uri
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ru.tensor.sbis.attachments.generated.AttachmentsUtils
import ru.tensor.sbis.communication_decl.communicator.media.data.UriResolver
import ru.tensor.sbis.communicator.common.util.castTo
import timber.log.Timber
import java.lang.RuntimeException

/**
 * Дефолтная реализация [UriResolver] для плеера.
 *
 * @author vv.chekurda
 */
internal class DefaultMediaPlayerUriResolver : UriResolver {

    @OptIn(DelicateCoroutinesApi::class)
    override fun resolve(attachId: Long, callback: (Long, Uri?, Throwable?) -> Unit) {
        GlobalScope.launch(Dispatchers.IO) {
            val mediaPath = AttachmentsUtils.getContentUri(attachId)
            val (uri, ex) = try {
                val uri = mediaPath?.let(Uri::parse)
                    ?: throw RuntimeException("Cannot resolve uri for attachId = $attachId")
                uri to null
            } catch (ex: Exception) {
                Timber.e(ex)
                null to ex
            }
            withContext(Dispatchers.Main) {
                callback(attachId, uri, ex)
            }
        }
    }
}