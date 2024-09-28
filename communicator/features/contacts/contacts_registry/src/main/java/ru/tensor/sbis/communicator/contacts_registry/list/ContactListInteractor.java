package ru.tensor.sbis.communicator.contacts_registry.list;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

import io.reactivex.Observable;
import io.reactivex.Single;
import kotlin.Unit;
import ru.tensor.sbis.common.generated.CommandStatus;
import ru.tensor.sbis.communicator.contacts_registry.ui.contactlist.model.ContactRegistryModel;
import ru.tensor.sbis.communicator.contacts_registry.ui.spinner.ContactSortOrder;
import ru.tensor.sbis.mvp.data.model.PagedListResult;

/**
 * Интерактор реестра контактов
 *
 * @author da.zhukov
 */
public interface ContactListInteractor {

    /**
     * Поиск контактов
     *
     * @param searchString поисковый запрос
     * @param folderUuid   идентификатор текущей папки
     * @param lastItem     последний элемент текущего списка
     * @param from         номер позиции, с которой запрашиваем
     * @param count        количество запрашиваемых элементов
     * @param order        тип сортировки
     * @param reload       true, если необходимо получить облачные результаты
     * @return результат списка контактов
     */
    Observable<PagedListResult<ContactRegistryModel>> searchContacts(
        @Nullable String searchString,
        @Nullable UUID folderUuid,
        @Nullable ContactRegistryModel lastItem,
        int from, int count,
        @NonNull ContactSortOrder order,
        boolean reload
    );

    /**
     * Перенос списка контактов в папку
     *
     * @param contacts          список контактов
     * @param targetFolderUuid  идентификатор папки, в которую переносятся контакты
     * @param currentFolderUuid идентификатор текущей папки, из которой переносятся контакты
     * @return статус операции
     */
    Single<CommandStatus> moveContacts(@NonNull Collection<UUID> contacts, @Nullable UUID targetFolderUuid, @Nullable UUID currentFolderUuid);

    /**
     * Перенос контакта в папку
     *
     * @param contactUuid       идентификатор контакта
     * @param targetFolderUuid  идентификатор папки, в которую переносятся контакт
     * @param currentFolderUuid идентификатор текущей папки, из которой переносятся контакт
     * @return статус операции
     */
    Single<CommandStatus> moveContact(@NonNull UUID contactUuid, @Nullable UUID targetFolderUuid, @Nullable UUID currentFolderUuid);

    /**
     * Удалить список контактов
     *
     * @param contacts   список идентификаторов контактов
     * @param folderUuid идентификатор папки, в которой находятся контакты
     * @return статус операции
     */
    Single<CommandStatus> deleteContacts(@NonNull Collection<UUID> contacts, @Nullable UUID folderUuid);

    /**
     * Заблокировать список контактов.
     *
     * @param contactUuids список идентификаторов контактов
     * @return статус операции
     */
    Single<CommandStatus> blockContacts(@NonNull List<UUID> contactUuids);

    /**
     * @return Observable для подписки на изменения кэша реестра контактов
     */
    Observable<Unit> observeContactsListCacheChanges();

    /**
     * Подписка на события отправки сообщений
     */
    Observable<Unit> observeMessageSentEvents();

    /**
     * @return Single с признаком доступности функции добавления новых контактов.
     * Проверка необходима для сценариев с ограничениями в пробном demo функционале коммуникатора.
     */
    Single<Boolean> canAddNewContacts();

    /**
     * Отменить асинхронные операций контроллера контактов, которые были вызваны CRUD методом list().
     */
    void cancelContactsControllerSynchronizations();

    /**
     * Подписаться на события изменения настроек от ProfileSettingsController.
     */
    Observable<Unit> observeProfileSettingsEvents();

    /**
     * Проверить нужно ли автоимпортировать контакты для данного пользователя.
     */
    Single<Boolean> getNeedImportContactsFromPhone();
}
