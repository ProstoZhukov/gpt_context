package ru.tensor.sbis.message_panel.declaration.repository

import io.reactivex.Observable
import java.util.*

/**
 * TODO: 11/13/2020 Добавить документацию
 * TODO: 11/13/2020 Оформить API на основе интерактора https://online.sbis.ru/opendoc.html?guid=35ce3634-f554-4629-b789-a226a292f275
 *
 * @author ma.kolpakov
 */
interface MessagePanelDraftRepository {

    val draftUuid: Observable<UUID>
}