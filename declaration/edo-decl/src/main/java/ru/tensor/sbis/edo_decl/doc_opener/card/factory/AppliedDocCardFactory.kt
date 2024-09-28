package ru.tensor.sbis.edo_decl.doc_opener.card.factory

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import ru.tensor.sbis.edo_decl.doc_opener.OpenDocRequest
import ru.tensor.sbis.edo_decl.doc_opener.card.config.DocCardConfig
import ru.tensor.sbis.edo_decl.doc_opener.doc_model.DocModel

/**
 * Фабрика прикладной карточки документа
 *
 * @author sa.nikitin
 */
interface AppliedDocCardFactory : BaseDocCardFactory<DocCardConfig> {

    /**
     * Поддерживаемые типы документов
     */
    val supportedDocTypes: List<String>

    /**
     * Ключ модуля, к которому принадлежит карточка
     * Параметр позволяет уникально определить карточку, если для одного типа документа их зарегистрировано более одной
     *
     * ВНИМАНИЕ! На текущий момент значение ключа должно быть равно названию прикладной БД
     * В будущем значение ключа будет выдаваться микросервисом типов ЭДО
     *
     * Если на вход опенеру поступил ключ модуля, см. [OpenDocRequest.moduleKey], то поиск карточки будет с учетом этого ключа
     *
     * Пример:
     *  - Карточка задачи регистрируется для открытия типа "Договор" с moduleKey - 'Tasks'
     *  - Дефолтная карточка реестра Входящих/Исходящих тоже регистрируется для открытия типа "Договор",
     *    но с moduleKey - 'InOutDocs'
     *  - Из реестра задач в опенер будет запрос на открытие договора с moduleKey - 'Tasks'
     *  - Опенер учтет этот параметр и откроет именно задачную карточку
     */
    val moduleKey: String? get() = null

    /**
     * Следует ли использовать фабрику только при совпадении [OpenDocRequest.moduleKey] с [moduleKey]
     */
    val useOnlyIfModuleKeyMatches: Boolean get() = false

    /**
     * Тип карточки
     */
    val cardType: AppliedCardType

    /**
     * Предпроверка фабрики на готовность открыть документ.
     * Выполняется после того, как будет найден поддерживаемый docType из [supportedDocTypes].
     *
     * Необходимо переопределить, если возможность открытия документа
     * зависит от переданной модели [DocModel].
     */
    @Suppress("DeprecatedCallableAddReplaceWith")
    @Deprecated("Нужно реализовывать через комбинацию supportedDocTypes и moduleKey")
    fun isReadyToOpenCardByModel(model: DocModel): Boolean = true

    /**
     * [SPECIALIZED] - карточка специализирующаяся под контретный тип документа
     * [COMMON] - общая карточка под различные типы документа
     */
    @Parcelize
    enum class AppliedCardType : Parcelable {
        SPECIALIZED,
        COMMON
    }
}

