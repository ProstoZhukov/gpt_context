# Базовый компонент выбора.

| Модуль             | Ответственные                                                                          |
|--------------------|----------------------------------------------------------------------------------------|
| [design_selection] | [Чекурда Владимир](https://online.sbis.ru/person/0fe3e077-6d50-431c-9353-f630fc789877) |

#### Описание
Модуль содержит:
1) Базовую реализацию компонента выбора:
- SelectionFragment - фрагмент выбора.
- SelectionContentFragment - фрагмент контента выбора, который отвечает за отображение списка для соответствующей папки.
- SelectionFragmentFactory - фабрика для создания фрагмента выбора.

2) Интерфейсы требуемых зависимостей компонента:
- SelectionDependenciesFactory - фабрика зависимостей.
- SelectionFilterFactory - фабрика фильтров для загрузки списка.
- SelectionStubFactory - фабрика заглушек.
- SelectionResultListener - слушатель результата выбора.
- HeaderButtonContract - контракт головной кнопки, которая находится над списком.
- SelectionCustomization - кастомизатор компонента (списков выбранных и невыбранных элементов).
- SelectionStringsConfig - строки, используемые в компоненте в различных сценариях.

3) Реализации по умолчанию для требуемых зависимостей:
- DefaultSelectionCustomization - реализация кастомизатора компонента (списка выбранных и невыбранных элементов).
- DefaultSelectableItemsCustomization - реализация для кастомизации списка невыбранных элементов.
- DefaultSelectedItemsCustomization - реализация для кастомизации списка выбранных элементов.
- SelectionPersonViewHolderHelper - хэлпер для создания холдеров невыбранных персон.
- SelectionFolderViewHolderHelper - хэлпер для создания холдеров невыбранных папок.
- SelectedPersonItemViewHolderHelper - хэлпер для создания холдеров выбранных персон.
- SelectedFolderItemViewHolderHelper - хэлпер для создания холдеров выбранных папок.

#### Подключение
Модуль подключается к проекту в settings.gradle следующим образом:
`include ':design_selection'`
`project(':design_selection').projectDir = new File(settingsDir, 'design/design_selection')`

А также должны присутствовать все требуемые модули-зависимости, которые указаны в settings.gradle модуля design_selection.

Далее в модуле реализации выбора "сущености" в build.gradle добавить:
`implementation project(':design_selection')`

##### Стилизация
Стандартная тема компонента: `SelectionTheme`.
Атрибут для установки темы компонента: `selectionTheme`.

Атрибуты стилизации:
- `Selection_topNavigationStyle` - стиль для шапки (см. атрибуты SbisTopNavigationView).
- `Selection_selectionPanelStyle` - стиль панели выбранных элементов.
- `Selection_selectionItemsTheme` - тема для ячеек невыбранных элементов списка.
- `Selection_selectedItemsTheme` - тема для ячеек выбранных элементов списка.

Компонент выбора может стилизоваться двумя способами:
1) Через xml темизацию, путем указания темы компонента на уровне манифестов Application или Activity.
```xml
<style name="SelectionTheme">
    <item name="Selection_topNavigationStyle">@style/SelectionTopNavigationStyle</item>
    <item name="Selection_selectionPanelStyle">@style/SelectionPanelStyle</item>
    <item name="Selection_selectedItemsTheme">@style/SelectedItemsTheme</item>
    <item name="Selection_selectionItemsTheme">@style/SelectionItemsTheme</item>
</style>
```
2) Программно, путем указания ресурса темы в SelectionConfig компонента выбора.
```kotlin
SelectionFragmentFactory.createSelectionFragment(
    config = CustomSelectionConfig(
        themeRes = R.style.CustomSelectionTheme
    ),
    dependenciesProvider = SelectionDependenciesFactory.ProviderImpl()
)
```

#### Использование
Для начала посмотрим на финальный вариант фичи вашего модуля, к которому требуется стремиться:
```kotlin
internal object RecipientSelectionFragmentFactory {

    /**
     * Создать фрагмент выбора получателей.
     *
     * @param config конфигурация выбора получателей.
     */
    fun createRecipientSelectionFragment(
        config: RecipientSelectionConfig
    ): Fragment =
        SelectionFragmentFactory.createSelectionFragment(
            config = config,
            dependenciesProvider = RecipientSelectionDependenciesProvider()
        )
}
```

Для реализации своего собственного компонента выбора вам потребуется:
1) Создать новый модуль.
2) Подключить модуль design_selection к своему модулю:
`implementation project(':design_selection')`
3) На текущий момент для работы компонента выбора требуется его контроллер `RecipientController`, готовые реализации которого находятся в модуле design_selection_common.
Его также необходимо подключить к новому модулю:
`implementation project(':design_selection_common')`
Текущий контроллер умеет работать с двумя сущностями:
- персоны
- папки
Планируется поддержка следующих типов сущностей для кастомных справочников:
- Универсальный элемент без привыязки к типу сущности: заголовок, подзаголовок, аватарка, идентификатор.
- Контрагенты (вероятно в рамках универсального элемента)
- Регионы (вероятно в рамках универсального элемента)
4) Для работы компонента ему потребуется 3 вида реализации:
###### SelectionConfig
SelectionConfig - это параметры настройки компонента, которые могут определять его стилизацию, поведение, источники и сценарии использования.
Пример реализации своего конфига:
```kotlin
/**
 * Конфигурация компонента выбора получателей.
 * 
 * @property unfoldDepartments true, если в результате выбора необходимо вернуть распакованных получателей
 * из подразделений.
 * @property isDepartmentsSelectable true, если подразделение можно выбирать в качестве результата.
 * В ином случае в подразделение можно только провалиться.
 * @property closeOnComplete закрыть фрагмент при подтверждении выбора.
 * @property closeOnCancel закрыть фрагмент при отмене выбора.
 */
data class RecipientSelectionConfig(
    override val useCase: RecipientSelectionUseCase,
    override val selectionMode: SelectionMode = useCase.selectionMode,
    override val doneButtonMode: SelectionDoneButtonVisibilityMode = useCase.doneButtonMode,
    override val itemsLimit: Int = useCase.itemsLimit,
    override val searchQueryMinLength: Int = useCase.searchQueryMinLength,
    override val excludeList: List<UUID>? = null,
    override val requestKey: String = EMPTY_REQUEST_KEY,
    override val enableSwipeBack: Boolean = false,
    override val themeRes: Int? = null,
    val unfoldDepartments: Boolean = useCase.unfoldDepartments,
    val isDepartmentsSelectable: Boolean = useCase.isDepartmentsSelectable,
    val closeOnComplete: Boolean = true,
    val closeOnCancel: Boolean = closeOnComplete
) : SelectionConfig
```
Из всех возможных кастомных и родительских настроек конфига существенно важным является поле useCase.
Подробнее о нем ниже.

###### SelectionUseCase
SelectionUseCase - это краткое описание сценария использования компонента выбора.
Данный интерфейс содержит в себе 2 первостепенно-важных поля:
1) `name` - наименование, самый важный параметр, определяющий настройки источников контроллера, которые отвечают за загрузку списка выбранных и невыбранных элементов в указанном сценарии.
Данный строковый ключи обязательно должен быть поддержан на контроллере.
2) `args` - пары аргументов, которые могут содержать различные параметры, требуемые для контроллера в этом сценарии,
например, идентификатор диалога/документа, какие-нибудь флаги, который могут потребоваться контроллеру для работы use_case по ключу `name`.

Следующие параметры являются упрощающими для определения и создания `SelectionConfig` и служат настройками по-умолчанию для вашего `SelectionUseCase` сценария.
1)`selectionMode` - мод выбора. 
2)`doneButtonMode` - мод видимости кнопки подтверждения.
3)`itemsLimit` - лимит количества отображаемых элементов.
4)`searchQueryMinLength` - минимальное количество введеных символов в поисковую строку для начала поисковых запросов.

По вопросам добавления новых SelectionUseCase можно проконсультироваться с автором текущего модуля и с ответственным за контроллер получателей Михалевым М.

###### SelectionDependenciesFactory.Provider
SelectionDependenciesFactory.Provider - это поставщик фабрики зависимостей компонента выбора, которая позволяет создать фабрику по контексту и `SelectionConfig` в используемом сценарии.
Пример реализации поставщика фабрики зависимостей:
```kotlin
internal class RecipientSelectionDependenciesProvider : SelectionDependenciesFactory.Provider<SelectionItem> {

    override fun getFactory(appContext: Context, config: SelectionConfig): SelectionDependenciesFactory<SelectionItem> {
        val component = appContext.getRecipientSelectionComponent(config as RecipientSelectionConfig)
        return RecipientSelectionDependenciesFactory(component)
    }
}
```

Пример реализации фабрики зависимостей:
```kotlin
internal class RecipientSelectionDependenciesFactory(
    private val component: RecipientSelectionComponent
) : SelectionDependenciesFactory<SelectionItem> {

    override fun getSelectionControllerProvider(
        appContext: Context
    ): SelectionControllerWrapper.Provider<*, *, *, *, *, *, SelectionItem> =
        component.controllerProvider

    override fun getFilterFactory(appContext: Context): SelectionFilterFactory<*> =
        component.filterFactory

    override fun getStubFactory(appContext: Context): SelectionStubFactory =
        component.stubFactory

    override fun getSelectionResultListener(appContext: Context): SelectionResultListener<SelectionItem, *> =
        component.resultListener

    override fun getSelectionCustomization(appContext: Context): SelectionCustomization<SelectionItem> =
        component.customization

    override fun getSelectorStrings(appContext: Context): SelectionStringsConfig =
        component.selectionStrings

    override fun getHeaderButtonContract(appContext: Context): HeaderButtonContract<SelectionItem, *>? =
        component.headerButtonContract
}
```

1) В качестве контроллера рекомендуется использовать `SelectionControllerProviderImpl` из модуля `design_selection_common`,
тк компонент завязан работой бизнес-логики выбора непосредственно на контроллер `RecipientController`.

2) Пример реализации фабрики фильтров `SelectionFilterFactory`:
```kotlin
internal class RecipientSelectionFilterFactory @Inject constructor() : SelectionFilterFactory<RecipientFilter> {

    override fun createFilter(meta: SelectionFilterMeta): Any =
        RecipientFilter(
            meta.query,
            meta.folderItem?.id?.let { UUIDUtils.fromString(it) }
        )
}
```

3) Пример реализации фабрики заглушек `SelectionStubFactory`:
```kotlin
internal class RecipientSelectionStubFactory @Inject constructor() : StubFactory {

    override fun create(type: StubType): StubViewContentFactory = {
        when (type) {
            StubType.BAD_FILTER -> StubViewCase.NO_SEARCH_RESULTS.getContent()
            StubType.NO_NETWORK -> StubViewCase.NO_CONNECTION.getContent()
            StubType.SERVER_TROUBLE -> StubViewCase.SERVICE_UNAVAILABLE.getContent()
            StubType.NO_DATA -> StubViewCase.NO_DATA.getContent()
        }
    }
}
```

4) Пример реализации слушателя результата выбора `SelectionResultListener` с использованием асинхронных обработок списка:
```kotlin
internal class RecipientSelectionResultListener @Inject constructor(
    private val config: RecipientSelectionConfig,
    private val interactor: RecipientSelectionInteractor
) : SelectionResultListener<SelectionItem, FragmentActivity> {

    private val resultDelegate: RecipientSelectionResultDelegate
        get() = RecipientSelectionPlugin.singletonComponent.recipientSelectionResultDelegate

    override fun onComplete(
        activity: FragmentActivity,
        result: List<SelectionItem>,
        requestKey: String,
        disposable: DisposableContainer
    ) {
        disposable += interactor.getRecipientSelectionData(
            result = result,
            unfoldDepartments = config.unfoldDepartments
        )
            .doFinally { if (config.closeOnComplete) activity.onBackPressedDispatcher.onBackPressed() }
            .subscribe { selectionData ->
                resultDelegate.onSuccess(
                    data = selectionData,
                    requestKey = requestKey
                )
            }
    }

    override fun onCancel(activity: FragmentActivity, requestKey: String) {
        resultDelegate.onCancel(requestKey)
        if (config.closeOnCancel) activity.onBackPressed()
    }
}
```

5) Пример реализации кастомизатора `SelectionCustomization`:
```kotlin
class DefaultSelectionCustomization : SelectionCustomization<SelectionItem> {

    override fun getListItemsCustomization(): SelectableItemsCustomization<SelectionItem> =
        DefaultSelectableItemsCustomization()

    override fun getSelectedItemsCustomization(): SelectedItemsCustomization<SelectionItem> =
        DefaultSelectedItemsCustomization()
}
```
Пример реализации кастомизатора выбранных/невыбранных:
```kotlin
class DefaultSelectedItemsCustomization : SelectedItemsCustomization<SelectionItem> {

    @Suppress("UNCHECKED_CAST")
    override fun createViewHolderHelpers(
        clickDelegate: SelectedItemClickDelegate,
        activityProvider: Provider<FragmentActivity>
    ): Map<Any, ViewHolderHelper<SelectionItem, *>> = mapOf(
        SelectionPersonItemModel::class.java.simpleName to SelectedPersonItemViewHolderHelper(
            clickDelegate = clickDelegate
        ) as ViewHolderHelper<SelectionItem, *>,

        SelectionFolderItemModel::class.java.simpleName to SelectedFolderItemViewHolderHelper(
            clickDelegate = clickDelegate
        ) as ViewHolderHelper<SelectionItem, *>
    )

    override fun getViewHolderType(item: SelectionItem): Any =
        when (item) {
            is SelectionPersonItemModel,
            is SelectionFolderItemModel -> item::class.java.simpleName
            else -> error("Unexpected model ${item::class.java} for DefaultSelectedItemsCustomization")
        }
}
```

6) Пример реализации контракта головной кнопки `HeaderButtonContract`:
```kotlin
internal class PrivateChatHeaderButtonContract : HeaderButtonContract<SelectionItem, FragmentActivity> {

    override val layout: Int = R.layout.design_recipient_selection_private_chat_header_button

    override fun onButtonClicked(
        activity: FragmentActivity,
        selectedItems: List<SelectionItem>,
        config: SelectionConfig
    ): HeaderButtonStrategy {
        singletonComponent.recipientSelectionResultDelegate.onSuccess(requestKey = config.requestKey)
        val newUseCase = RecipientSelectionUseCase.NewChat
        return HeaderButtonStrategy(
            newConfig = (config as RecipientSelectionConfig).copy(
                useCase = newUseCase,
                selectionMode = newUseCase.selectionMode,
                doneButtonMode = newUseCase.doneButtonMode,
                unfoldDepartments = newUseCase.unfoldDepartments,
                isDepartmentsSelectable = newUseCase.isDepartmentsSelectable
            )
        )
    }
}
```

#### Реализации компонентов выбора:
- [Компонент выбора получателей(контактов/сотрудников/подразделений/лиц)](https://git.sbis.ru/mobileworkspace/android-design/-/tree/rc-23.3200/design_recipient_selection)

#### Использование в приложениях
- [Коммуникатор](https://git.sbis.ru/mobileworkspace/apps/droid/communicator)
- [Курьер](https://git.sbis.ru/mobileworkspace/apps/droid/courier)
- [SabyLite](https://git.sbis.ru/mobileworkspace/apps/droid/sabylite)
- [Storekeeper](https://git.sbis.ru/mobileworkspace/apps/droid/storekeeper)
- [Brand](https://git.sbis.ru/mobileworkspace/apps/droid/brand)
- [Sabyget](https://git.sbis.ru/mobileworkspace/apps/droid/sabyget)
- [Sabyclients](https://git.sbis.ru/mobileworkspace/apps/droid/sabyclients)
- [Mysaby](https://git.sbis.ru/mobileworkspace/apps/droid/mysaby)
- [Sabydisk](https://git.sbis.ru/mobileworkspace/apps/droid/sabydisk)
- [Business](https://git.sbis.ru/mobileworkspace/apps/droid/business)