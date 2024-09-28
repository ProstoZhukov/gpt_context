package ru.tensor.sbis.communicator.themes_registry.ui.communication.conversations.theme

import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.functions.Function
import io.reactivex.subjects.PublishSubject
import ru.tensor.sbis.communicator.common.crud.ThemeRepository
import ru.tensor.sbis.communicator.common.data.theme.ConversationMapper
import ru.tensor.sbis.communicator.common.util.CommunicatorActivityStatusSubscriptionInitializer
import ru.tensor.sbis.communicator.generated.ConversationType
import ru.tensor.sbis.communicator.generated.DataRefreshedThemeControllerCallback
import ru.tensor.sbis.communicator.generated.ListResultOfConversationViewDataMapOfStringString
import ru.tensor.sbis.communicator.generated.ThemeFilter
import ru.tensor.sbis.mvp.data.model.PagedListResult
import ru.tensor.sbis.mvp.interactor.crudinterface.command.BaseListCommand
import ru.tensor.sbis.persons.ConversationRegistryItem
import ru.tensor.sbis.profiles.generated.Person
import java.util.*
import kotlin.collections.HashMap
import kotlin.collections.List

/** @SelfDocumented */
internal class ThemeListCommand(
    private val themeRepository: ThemeRepository,
    private val conversationMapper: ConversationMapper,
    private val themeListFilter: ThemeListFilter,
    private val metadataObservable: PublishSubject<Map<String, String>>,
    private val themeListCache: ThemeListCache,
    private val activityStatusSubscriptionsInitializer: CommunicatorActivityStatusSubscriptionInitializer
) : BaseListCommand<ConversationRegistryItem, ListResultOfConversationViewDataMapOfStringString, ThemeFilter, DataRefreshedThemeControllerCallback>(
    themeRepository,
    Function { PagedListResult(ArrayList(), false) }
) {

    private val threadLocalThemeFilter = ThreadLocal<ThemeFilter>()

    override fun getMapper(refresh: Boolean): Function<ListResultOfConversationViewDataMapOfStringString, PagedListResult<ConversationRegistryItem?>> {
        return Function { listResultOfConversationViewData: ListResultOfConversationViewDataMapOfStringString ->
            val metadata = listResultOfConversationViewData.metadata ?: HashMap()
            metadata[LIST_KEY] = (!refresh).toString()
            metadata[HAS_MORE_KEY] = listResultOfConversationViewData.haveMore.toString()
            metadataObservable.onNext(metadata)
            val themeFilter = threadLocalThemeFilter.get()
            val forChats = (themeFilter?.themeType ?: themeListFilter.conversationType) == ConversationType.CHAT
            ThemePagedListResult(
                conversationMapper.applyToList(
                    listResultOfConversationViewData.result,
                    forChats,
                    themeListFilter.isConversationHiddenOrArchived()
                ),
                listResultOfConversationViewData.haveMore,
                metadata,
                themeFilter
            )
        }
    }

    override fun list(filter: ThemeFilter): Observable<PagedListResult<ConversationRegistryItem>> {
        return fetch(
            Observable.fromCallable {
                // получение данных и маппинг происходит на одном потоке, поэтому передаем filter через ThreadLocal
                threadLocalThemeFilter.set(filter)
                mRepository.list(filter)
            }.doOnNext {
                val persons: List<List<Person>> = it.result.map { conversationViewData ->
                    conversationViewData.participantsCollage
                }
                activityStatusSubscriptionsInitializer.initialize(persons.getUuids())
            },
            false
        )
    }

    override fun refresh(filter: ThemeFilter): Observable<PagedListResult<ConversationRegistryItem>> {
        return fetch(
            Observable.fromCallable {
                // получение данных и маппинг происходит на одном потоке, поэтому передаем filter через ThreadLocal
                threadLocalThemeFilter.set(filter)
                mRepository.refresh(filter)
            }.doOnNext {
                val persons: List<List<Person>> = it.result.map { conversationViewData ->
                    conversationViewData.participantsCollage
                }
                activityStatusSubscriptionsInitializer.initialize(persons.getUuids())
            },
            true
        )
    }

    override fun getAfterMappingProcessor(refresh: Boolean): Function<PagedListResult<ConversationRegistryItem>, PagedListResult<ConversationRegistryItem>> {
        return Function { result ->
            // получаем фильтр и передаем его в кэш вместе с данными после маппинга
            threadLocalThemeFilter.get()?.let { themeFilter ->
                themeListCache.addToCache(themeFilter, result)
            }
            // сбрасываем значение фильтра для текущего потока
            threadLocalThemeFilter.remove()
            result
        }
    }

    /** @SelfDocumented */
    fun getCache(forChannels: Boolean): PagedListResult<ConversationRegistryItem>? {
        return themeListCache.getCache(forChannels)
    }

    /** @SelfDocumented */
    fun deleteFromCache(forChannels: Boolean, uuid: UUID) {
        themeListCache.deleteFromCache(forChannels, uuid)
    }

    /** @SelfDocumented */
    fun onThemeTabBeforeOpen(): Completable {
        return Completable.fromCallable {
            themeRepository.onThemeTabBeforeOpen()
        }
    }

    /** @SelfDocumented */
    fun onThemeTabAfterOpen(): Completable {
        return Completable.fromCallable {
            themeRepository.onThemeTabAfterOpen()
        }
    }

    private fun List<List<Person>>.getUuids(): List<UUID> {
        val result = mutableListOf<UUID>()
        this.forEach {
            it.forEach { person ->
                result.add(person.uuid)
            }
        }
        return result
    }
}