package ru.tensor.sbis.tasks.feature

import android.os.Parcelable
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import kotlinx.parcelize.Parcelize
import ru.tensor.sbis.android_ext_decl.FragmentTransactionArgs
import ru.tensor.sbis.plugin_struct.feature.Feature
import java.util.UUID

/**
 * Описывает внешний API мастера создания задачи.
 *
 * @author aa.sviridov
 */
interface TasksCreateFeature : Feature {

    /**
     * Создаёт экран мастера создания задачи.
     * @param args аргументы, см. [CreateMasterArgs].
     * @param fragmentManager фрагмент менеджер, на котором нужно выполнить транзакцию.
     * @param fragmentTransactionArgs аргменты для построения транзакции фрагмента, см. [FragmentTransactionArgs].
     * @param listener слушатель результата мастера, см. [ExitResult].
     * @return новый экземпляр фрагмента мастера создания задачи.
     */
    fun createTasksCreateMasterFragmentTransaction(
        args: CreateMasterArgs,
        fragmentManager: FragmentManager,
        fragmentTransactionArgs: FragmentTransactionArgs,
        listener: ((ExitResult) -> Unit)?,
    ): FragmentTransaction

    /**
     * Создаёт экран редактирования задачи.
     * @param args аргументы, см. [EditArgs].
     * @return новый экземпляр фрагмента мастера редактирования задачи.
     */
    fun createTaskEditFragment(
        args: EditArgs,
    ): Fragment

    /**
     * Внешний сигнал о завершении мастера.
     *
     * @author aa.sviridov
     */
    enum class ExitResult {
        /**
         * Успешно.
         */
        SUCCESS,

        /**
         * Отменено.
         */
        CANCELLED,

        /**
         * Что-то пошло не так.
         */
        ERROR,
    }

    /**
     * Аргументы мастера создания задачи.
     *
     * @author aa.sviridov
     */
    sealed class CreateMasterArgs : Parcelable {

        /**
         * Идентификатор лица-автора.
         */
        abstract val authorFaceUuid: UUID

        /**
         * Аргументы мастера создания новой подзадачи.
         * @property baseDocUuid идентификатор базового документа, к которой будет прикреплена
         * новая задача.
         * @property authorFaceUuid идентификатор лица-автора.
         *
         * @author aa.sviridov
         */
        @Parcelize
        data class AsSubTask(
            val baseDocUuid: UUID,
            override val authorFaceUuid: UUID,
        ) : CreateMasterArgs()

        /**
         * Аргументы мастера создания задачи в папке.
         * @property folderUuid идентификатор подпапки "от меня", null - корневая.
         * @property authorFaceUuid идентификатор лица-автора.
         * @property presets предустановки, см. [CreateMasterPreset].
         * @property dialogData данные диалога (переписки), к которому нужно прикрепить задачу,
         * null - не нужно, см. [DialogData].
         * @property isCreateImmediately true если нужно попытаться создать задачу сразу с помощью
         * предустановок, false - нет.
         *
         * @author aa.sviridov
         */
        @Parcelize
        data class InFolder(
            val folderUuid: UUID?,
            override val authorFaceUuid: UUID,
            val presets: CreateMasterPreset,
            val dialogData: DialogData?,
            val isCreateImmediately: Boolean,
        ) : CreateMasterArgs()

        /**
         * Данные диалога для прикрепления создаваемой задачи к нему.
         * @property dialogUuid идентификатор диалога.
         * @property linking действие для переписки, см. [Linking].
         *
         * @author aa.sviridov
         */
        @Parcelize
        data class DialogData(
            val dialogUuid: UUID,
            val linking: Linking,
        ) : Parcelable {

            /**
             * Действие для переписки.
             *
             * @author aa.sviridov
             */
            @Parcelize
            enum class Linking : Parcelable {
                /**
                 * Не показывать диалог и связать задачу с перепиской (linkDialogToDocument).
                 */
                LINK,

                /**
                 * Не показывать диалог и добавить задачу к переписке (notifyDialogAboutDocument).
                 */
                APPEND,

                /**
                 * Показать диалог с двумя действиями, описанными в [LINK] и [APPEND].
                 */
                ASK,
            }
        }
    }

    /**
     * Предустановка мастера создания задачи, используется в [CreateMasterArgs.InFolder] в случаях
     * создания задачи через диплинк (расшаривание из других приложений), создания задачи из
     * сообщения и прочее.
     * @property executorsUuids список идентификатором предустановленных исполнителей.
     * @property attachmentsFromUris список локальных предустановленных вложений по URI.
     * @property attachmentsFromDisk список предустановленных вложений из СБИС.Диск, см. [DiskAttachment].
     * @property description предустановленное описание (null если не нужно предустанавливать).
     *
     * @author aa.sviridov
     */
    @Parcelize
    data class CreateMasterPreset(
        val executorsUuids: List<UUID> = emptyList(),
        val attachmentsFromUris: List<String> = emptyList(),
        val attachmentsFromDisk: List<DiskAttachment> = emptyList(),
        val description: String? = null,
    ) : Parcelable {

        /** Функция возвращающая true, если в конструктор не был передан ни один параметр */
        fun isEmpty() = executorsUuids.isEmpty()
            && attachmentsFromUris.isEmpty()
            && attachmentsFromDisk.isEmpty()
            && description == null

        /** Функция возвращающая true, если в конструктор был передан хотя бы один параметр */
        fun isNotEmpty() = !isEmpty()
    }

    /**
     * Аргументы для редактирования задачи.
     *
     * @author aa.sviridov
     */
    sealed class EditArgs : Parcelable {

        /**
         * Глобальный идентификатор документа.
         */
        abstract val documentUuid: UUID

        /**
         * Глобальный идентификатор события по документу.
         */
        abstract val eventUuid: UUID?

        /**
         * Тип документа.
         */
        abstract val docType: String

        /**
         * Идентификатор лица.
         */
        abstract val ownerFaceUuid: UUID

        /**
         * Параметры редактирования задачи по идентификаторам документа и события, вероятно не
         * из реестра задач.
         *
         * @author aa.sviridov
         */
        @Parcelize
        data class Uuids(
            override val documentUuid: UUID,
            override val eventUuid: UUID?,
            override val docType: String,
            override val ownerFaceUuid: UUID,
        ) : EditArgs()

        /**
         * Параметры редактирования подзадачи.
         * @property linkedDocumentUuid глобальный идентификатор связанного документа.
         *
         * @author aa.sviridov
         */
        @Parcelize
        data class SubDoc(
            override val documentUuid: UUID,
            override val eventUuid: UUID?,
            override val docType: String,
            override val ownerFaceUuid: UUID,
            val linkedDocumentUuid: UUID,
        ) : EditArgs()

        /**
         * Параметры редактирования задачи из реестра.
         * @property registryType тип реестра из которого была открыта карточка.
         * @property folderUuid глобальный идентификатор папки в которой находится документ.
         * @property needSyncDraft true - нужно ли синхронизировать черновик, false - не нужно.
         *
         * @author aa.sviridov
         */
        @Parcelize
        data class Registry(
            override val documentUuid: UUID,
            override val eventUuid: UUID?,
            override val docType: String,
            override val ownerFaceUuid: UUID,
            val registryType: RegistryType,
            val folderUuid: UUID?,
            val needSyncDraft: Boolean = false,
        ) : EditArgs()
    }
}