# компонент поделиться ссылкой LinkShareFragment

##### Внешний вид

https://www.figma.com/proto/cxNVM6H6W8GHQNwMPmhaGr/%E2%9C%94%EF%B8%8F-%D0%9F%D0%BE%D0%B4%D0%B5%D0%BB%D0%B8%D1%82%D1%8C%D1%81%D1%8F-%D1%81%D1%81%D1%8B%D0%BB%D0%BA%D0%BE%D0%B9?node-id=4892-39042

#### Описание

Меню с командами для получения ссылки на документ, группу и тп. В компоновку можно добавить
“переключатели” - это элемент для выбора “типа” ссылки, например для внешних пользователей или для
сотрудников. Обязательные команды: “скопировать”, “открыть в браузере“, “отправить” и “qr-code”.
Можно вывести дополнительно прикладные команды, используя группировку для
разделения между командами.

- [ответственный Московчук А,Д,](https://online.sbis.ru/person/f6fa6997-bb39-4e71-ba27-e998125c9e74)

## Как использовать

Добавление зависимости:
Вам нужно добавить зависимость на модуль link_share_decl в файл build.gradle вашего проекта, чтобы
ваш проект мог использовать его функциональность.

```build.gradle
implementation project(':link_share_decl')
```

Создание модели SbisLinkShareModel:
Здесь вы создаете объект SbisLinkShareModel, который представляет собой модель данных для функции
шаринга ссылок. Этот объект содержит список ссылок SbisLinkShareLinkModel, список доступных действий
ActionType, тип панели PanelType и тип отображения ViewType.

```kotlin
val testModel = SbisLinkShareParams(
    listOf(SbisLinkShareLinkModel("https://link.ru/")),
    "title",
    ON_MOVABLE_PANEL_WITH_BACK_BUTTON
)
```

Создание контейнера во фрагменте:
В вашем макете XML должен быть FrameLayout с идентификатором link_share_container_id, который будет
служить контейнером для вашего фрагмента.

```xml

<FrameLayout android:id="@+id/link_share_container_id" android:layout_width="match_parent"
    android:layout_height="match_parent"/>
```

Заполнение контейнера с помощью FragmentManager:
В вашем коде на Kotlin вы создаете объект ContainerMovableFragment.Builder(), чтобы создать
фрагмент, который будет содержать ваш контент. Затем вы настраиваете этот фрагмент с помощью
методов, таких как setContentCreator(), setExpandedPeekHeight(), setDefaultHeaderPaddingEnabled(), и
передаете его в FragmentManager для добавления на экран.

Чтобы дополнительно добавить прикладные команды нужно передать в SbisLinkShareModel 
в параметр customMenuItem список из SbisLinkShareCustomMenuItem

пример использования:

```kotlin
    val testСustomItems = listOf<SbisLinkShareCustomMenuItem>(
    SbisLinkShareCustomMenuItem(
        listOf(
            SbisLinkShareMenuItemModel(
                CUSTOM,
                R.string.icon1,
                R.string.title1,
                {action1}
            ),
            SbisLinkShareMenuItemModel(
                CUSTOM,
                R.string.icon2,
                R.string.title2),
                {action2}
            )
        ),
        "название группы"
    )

    val testModel = SbisLinkShareModel(
    links = listOf(
        SbisLinkShareLinkModel(
            url = "https://www.google.com/",
            caption = "тип 1"
        ), SbisLinkShareLinkModel(
            url = "https://dzen.ru/",
            caption = "тип 2"
        )
    ),
    customMenuItem = testСustomItems
)

val creator: ContentCreatorParcelable =
    component.getDependency().getLinkShareContentCreator(testModel)

val containerId = R.id.link_share_container_id

val containerFragment = ContainerMovableFragment.Builder()
    .setContentCreator(creator)
    .setExpandedPeekHeight(MovablePanelPeekHeight.FitToContent())
    .setDefaultHeaderPaddingEnabled(false)
    .build()

childFragmentManager
    .beginTransaction()
    .add(containerId, containerFragment)
    .addToBackStack(null)
    .commit()
```

В примере создается объект SbisLinkShareModel, затем создается фрагмент
ContainerMovableFragment с помощью его Builder и добавляется в FragmentManager с помощью метода
beginTransaction()

