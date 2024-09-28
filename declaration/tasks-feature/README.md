# tasks-feature

| Ответственность | Ответственные                                                                                                  |
|-----------------|----------------------------------------------------------------------------------------------------------------|
| Участок работ   | [Реестр задач](https://online.sbis.ru/area/d42ac495-a707-4a6e-a2d4-74a2e2b1a765)                               |
| Участок работ   | [Реестр задач другого сотрудника](https://online.sbis.ru/area/84e53220-c845-4d71-84ba-3d34519981df)            |
| Участок работ   | [Карточка документа / задачи](https://online.sbis.ru/area/bc4bf090-b78a-467f-b27c-d3c25fd03fd6)                |
| Участок работ   | [Создание / редактирование задачи (android)](https://online.sbis.ru/area/c397c4dd-3683-4881-82a8-292c4842faee) |
| Участок работ   | [Реест подзадач (карточка документа)](https://online.sbis.ru/area/dfd180d5-17c6-49d6-9203-77bd7acfc5cc)        |

## Описание
Содержит декларацию публичных API.
- **Реестр задач** содержит методы по созданию экранов реестра задач как раздела в приложении, так и реестра задач сотрудника. Кроме того имеются дополнительные методы для предоставления информации о количестве задач и лице-получателе в модуль мотивации. 
- **Карточка документа/задачи** содержит методы для создания экрана карточки документа/задачи. **Это не универсальная карточка для открытия любого документа. В основном поддерживаются задачи, входящие/исходящие документы, а также УПФ.** 
- **Создание/редактирование задач** содержит методы для создания соответсвующих экранов мастера создания и редактирования задач, другие типы документов не поддерживаются.
- **Реест подокументов/подзадач** содержит методы для получения и отображения поддокументов/подзадач с возможностью пагинации, можно кастомизировать заголовок.

## Руководство по подключению и инициализации
Модуль содержит только декларацию и не содердит функционал, поэтому помимо включения зависимостей в сборку дополнительных действий не потребуется. Зависимости представлены в следующей таблице.

| Репозиторий                                                | модуль           |
|------------------------------------------------------------|------------------|
| https://git.sbis.ru/mobileworkspace/android-utils.git      | plugin_struct    |
| https://git.sbis.ru/mobileworkspace/android-utils.git      | deeplink         |
| https://git.sbis.ru/mobileworkspace/android-utils.git      | list             |
| https://git.sbis.ru/mobileworkspace/android-serviceapi.git | android-ext-decl |

## Описание публичного API
Содержит набор основных интерфейсов (`TasksFeature`, `DocumentFeature`, `TasksCreateFeature`, `SubdocsFeature`) и классов, являющимися входными и выходными параметрами методов этих интерфейсов.

### Реестр задач
`TasksFeature` содержит методы:
- `createTasksListFragment` - создаёт экран реестра задач в виде фрагмента.

Открытие фрагмента реестра своих задач в виде раздела ННП/аккордеона (с запуском мастера создания задачи с предустановленным описанием, опционален)
```kotlin
fragmentManager.beginTransaction()
    .replace(
        R.id.your_container,
        tasksFeature.createTasksListFragment(
            action = CreateNewTaskWithDescriptionDeeplinkAction(
                description = yourDescription,
            ),
        )
    )
    .commit()
```
Открытие фрагмента реестра задач сотрудника (свой тоже можно открыть в том же виде, если передать свой идентификатор)
```kotlin
fragmentManager.beginTransaction()
    .replace(
        R.id.your_container,
        tasksFeature.createTasksListFragment(
            ownerFaceUuid = employeeFaceUuid
        )
    )
    .commit()
```
Открытие реестра задач как в ННП/аккордеоне, но для выбора единственной задачи из списка:
```kotlin
fragmentManager.beginTransaction()
    .replace(
        R.id.your_container,
        tasksFeature.createTasksListFragment(
            mode = SINGLE_SELECTION,
        )
    )
    .commit()
```
- `getEmployeeTaskCounters` - предоставляет счётчики и ответственного, на которого переназначены задачи, например так
```kotlin
tasksFeature.getEmployeeTaskCounters(uuid = yourEmployyeeFaceUuid)
    .subscribeOn(Schedulers.io())
    .subscribe(
        { result ->
            val mainCounter = result.values.mainCounter
            val additionalCounter = result.values.additionalCounter
            val responsible = result.responsible
            // каким-то образом использовать mainCounter и additionalCounter и responsible
        }, { error ->
            // обработать ошибку
        }
    )
```
- `isThereAccessToViewUserTasks` - проверяет есть ли доступ пользователя для просмотра реестра задач сотрудника. Использование аналочино `getEmployeeTaskCounters`, например
```kotlin
tasksFeature.isThereAccessToViewUserTasks(uuid = yourEmployyeeFaceUuid)
    .subscribeOn(Schedulers.io())
    .subscribe(
        { result ->
            // каким-то образом использовать значение
        }, { error ->
            // обработать ошибку
        }
    )
```
- `createTaskCardView` - создаёт плашку задачи/документа в виде View, также происходит заполнение данных. Пример использования
```kotlin
val container = FrameLayout(context)
container.add(
    tasksFeature.createTaskCardView(
        documentUuid = it,
        context = requireContext(),
        onTaskClick = {
            // ваш код обработки клика по плашке
        },
        onPersonClick = {
            // ваш код обработки клика по персоне в плашке
        },
        errorHandler = {
            // ваш код обработки ошибки получения данных
        },
        onDeleteClick = {
            // ваш код обработки удаления при смахивании
        }
    )
)
```
Нужно иметь ввиду что при подгрузке и заполнении данных вьюшка изменит свой размер.

### Карточка документа/задачи
`DocumentFeature` содержит методы:
- `createDocumentCardFragment` - создаёт экран карточки документа/задачи в виде фрагмента, открыть его можно так
```kotlin
fragmentManager.beginTransaction()
    .replace(
        R.id.your_container,
        documentFeature.createDocumentCardFragment(
            args = WithUuidAndEventUuidArgs(
                documentUuid = yourDocumentUuid,
                eventUuid = yourEventUuid,
                docType = yourDocType,
            ),
            additionalArgs = AdditionalDocumentOpenArgs.Regular()
        )
    )
    .commit()
```
- `createDocumentCardActivityIntent` - создаёт экран карточки документа/задачи в виде активности, открыть его можно так
```kotlin
context.startActivity(
    documentFeature.createDocumentCardActivityIntent(
        context = context,
        args = WithUuidAndEventUuidArgs(
            documentUuid = yourDocumentUuid,
            eventUuid = yourEventUuid,
            docType = yourDocType,
        ),
        additionalArgs = AdditionalDocumentOpenArgs.Regular()
    )
)
```

### Создание/редактирование задач
`TasksCreateFeature` содержит методы:
- `createTasksCreateMasterFragmentTransaction` - создаёт экран мастера создания задачи.
Открытие мастера создания задачи без предустановок:
```kotlin
tasksCreateFeature.createTasksCreateMasterFragmentTransaction(
    args = TasksCreateFeature.CreateMasterArgs.InFolder(
        folderUuid = null,
        authorFaceUuid = yourUuid,
        presets = CreateMasterPreset(),
        dialogData = null,
        isCreateImmediately = false,
    ),
    fragmentManager = fm,
    fragmentTransactionArgs = FragmentTransactionArgs(
        containerResId = R.id.your_container,
        fragmentTag = yourTag,
        backStackName = yourBackStackName,
    ),
    listener = null,
).commit()
```
Открытие мастера создания задачи с предустановленным описанием в произвольную папку:
```kotlin
tasksCreateFeature.createTasksCreateMasterFragmentTransaction(
    args = TasksCreateFeature.CreateMasterArgs.InFolder(
        folderUuid = yourFolderUuid,
        authorFaceUuid = yourUuid,
        presets = CreateMasterPreset(
            desctiontion = "your description",
        ),
        dialogData = null,
        isCreateImmediately = false,
    ),
    fragmentManager = fm,
    fragmentTransactionArgs = FragmentTransactionArgs(
        containerResId = R.id.your_container,
        fragmentTag = yourTag,
        backStackName = yourBackStackName,
    ),
    listener = null,
).commit()
```
Открытие мастера создания подзадачи:
```kotlin
tasksCreateFeature.createTasksCreateMasterFragmentTransaction(
    args = TasksCreateFeature.CreateMasterArgs.AsSubTask(
        baseDocUuid = yourBaseDocUuid,
        authorFaceUuid = yourUuid,
    ),
    fragmentManager = fm,
    fragmentTransactionArgs = FragmentTransactionArgs(
        containerResId = R.id.your_container,
        fragmentTag = yourTag,
        backStackName = yourBackStackName,
    ),
    listener = null,
).commit()
```
Открытие мастера создания задачи с созданием целиком из предустановок (с описанием и исполнителями) и связать её с диалогом:
```kotlin
tasksCreateFeature.createTasksCreateMasterFragmentTransaction(
    args = TasksCreateFeature.CreateMasterArgs.InFolder(
        folderUuid = null,
        authorFaceUuid = yourUuid,
        presets = TasksCreateFeature.CreateMasterPreset(
            executorsUuids = yourExecutorsUuids,
            desctiontion = "your description",
        ),
        dialogData = DialogData(yourDialogUuid, LinkDialogToTask.LINK),
        isCreateImmediately = true,
    ),
    fragmentManager = fm,
    fragmentTransactionArgs = FragmentTransactionArgs(
        containerResId = R.id.your_container,
        fragmentTag = yourTag,
        backStackName = yourBackStackName,
    ),
    listener = null,
).commit()
```
- `createTaskEditFragment` - создаёт экран редактирования задачи. Является последним шагом мастера создания задачи.
Открытие экрана редактирования задачи:
```kotlin
fragmentManager.beginTransaction()
    .replace(
        R.id.your_container,
        tasksCreateFeature.createTaskEditFragment(
            args = TasksCreateFeature.EditArgs.Uuids(
                documentUuid = yourDocumentUuid,
                eventUuid = yourEventUuid,
                docType = yourDocType,
                ownerFaceUuid = yourFaceUuid,
            )
        )
    )
    .commit()
```

### Реестр подзадач
Представлен двумя интерфейсами - `SubdocsFeature`, который создаёт компонент подзадач, и `SubdocsComponent`, сам компонент. Для использования вызовите `getOrCreateComponent`, он создаст экземпляр `SubdocsComponent` или вернёт существующий:
```kotlin
val subdocsComponent = subdocsFeature.getOrCreateComponent(
    fragment = yourFragment,
    documentUuid = yourDocumentUuid,
    ownerFaceUuid = ownerFaceUuid,
    customization = SubdocsCustomization(
        headerPlural = R.plurals.your_subdocs_header_plural,
    ),
)
```
Подпишитесь на получение обновлений ячеек:
```kotlin
subdocsComponent.items
    .subscribeOn(AndroidSchedulers.mainThread())
    .subscribe { items ->
        yourSbisList.setListData(Plain(data = items))
    }
```
Если нужно добавить обработчики кликов:
```kotlin
subdocsComponent.events
    .subscribeOn(AndroidSchedulers.mainThread())
    .subscribe { event ->
        when (event) {
            is SubdocsEvent.SubdocClicked -> {
                // ваш код обработчика кликов по поддокументу.
            }
            is SubdocsEvent.PersonClicked -> {
                // ваш код обработчика кликов по коллажу.
            }
        }
    }
```
Если нужно принудительно начать пагинацию заново, например при свайпе сверху вниз в самом начале вашего экрана, вызовите `restartPaginationNow`.