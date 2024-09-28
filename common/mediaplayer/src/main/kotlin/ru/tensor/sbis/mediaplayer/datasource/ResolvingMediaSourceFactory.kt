package ru.tensor.sbis.mediaplayer.datasource

import android.net.Uri
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.DataSource
import androidx.media3.datasource.DataSpec
import androidx.media3.datasource.ResolvingDataSource

/**
 * Создать перехватывающую фабрику
 * Требуется для проставления параметров из исходной Uri в любую, появившуюся в процессе воспроизведения
 * Актуально для HLS:
 *     Изначальая Uri - это ссылка на мастер плейлист с параметрами
 *     Но внутри мастер плейлиста уже содержатся ссылки без параметров, нужно проставить
 */
@UnstableApi
fun createResolvingDataSourceFactory(upstreamFactory: DataSource.Factory, initialSourceUri: Uri) =
    ResolvingDataSource.Factory(upstreamFactory) { dataSpec: DataSpec ->
        dataSpec.withUri(
            dataSpec.uri.buildUpon().run {
                initialSourceUri.queryParameterNames.forEach { queryParamName ->
                    if (dataSpec.uri.queryParameterNames.contains(queryParamName).not()) {
                        appendQueryParameter(queryParamName, initialSourceUri.getQueryParameter(queryParamName))
                    }
                }
                build()
            }
        )
    }