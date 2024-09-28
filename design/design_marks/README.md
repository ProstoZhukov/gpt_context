# Компонент Пометки

| Ответственность | Ответственные                                                                      |
|-----------------|------------------------------------------------------------------------------------|
| [design_marks]  | [Гераськин Роман](https://dev.sbis.ru/person/45883fa3-9b14-458b-97b3-3f55f527230d) |

## Документация

- [API компонента](https://dev.sbis.ru/shared/disk/86318973-1634-4a1b-b686-67b090561529)
- [Стандарт](https://n.sbis.ru/article/9c9eba04-fcbf-430c-bf5f-f940bed5e18a)
- [Макет компонента](http://axure.tensor.ru/StandardsV8/%D0%BF%D0%BE%D0%BC%D0%B5%D1%82%D0%BA%D0%B8_23_6100.html)

## Описание

Модуль содержит реализацию компонента "Пометки. Компонент отображает список пометок во всплывающей
снизу шторке  `ContainerMovablePanel`. После завершения работы с компонентом он возвращает список
выбранных в виде листа моделек.

### Типы пометок:

- цветом - содержит цветную иконку кружка, заголовок пометки может окрашиваться в цвет пометки и
  имеет возможность кастомизации шрифта
- иконкой (платформенная) - содержит стандартную иконку

## Руководство по подключению и инициализации

Для подключения компонента требуется подключить следующие модули:

| Репозиторий    | модуль          |
|----------------|-----------------|
| `design_marks` | `:design_marks` |

Так же, для работы с FragmentResultAPI потребуется подключение зависимости:

```groovy
implementation "androidx.fragment:fragment-ktx:$rootProject.ext.fragmentVersion"
```

## Описание публичного API

### Стилизация

Тема компонента по умолчанию `MarksComponentDefaultTheme`. Для Розницы/Престо используется
тема `MarksComponentLargeTheme`. Тему компонента можно задавать через атрибут: `marksComponentTheme`

### Инициализация

#### Пометка цветом

Пример конструктора модели пометок цветом:

```kotlin
SbisMarksColorElement(
    id: CharSequence,
    title: PlatformSbisString,
    checkboxValue: SbisMarksCheckboxStatus,
    color: SbisColor,
    textStyle: Int
) : SbisMarksElement
```

#### Платформенная пометка

Пример конструктора модели пометок цветом:

```kotlin
SbisMarksIconElement(
    id: CharSequence,
    title: PlatformSbisString,
    checkboxValue: SbisMarksCheckboxStatus,
    icon: SbisMobileIcon. Icon
) : SbisMarksElement
```

Есть уже созданные платфрменные пометки "Важно" и "Плюс". Для их создания рекомендуется
воспользоваться фабричными top-level функциями, определёнными в файле `ElementFactory`:

```kotlin
val markImportant = createImportant(checkboxStatus) // Пометка "Важно"
val markPlus = createPlus(checkboxStatus)           // Пометка "Плюс"
```

Здесь `checkboxStatus` начальное значение чекбокса (поддерживается состояние `UNDEFINED`),
тип: `SbisMarksCheckboxStatus`

#### Компонент пометок

Для отображения шторки со списком необходимо создать экземпляр
класса `SbisMarksMovablePanelLauncher`. В конструкторе передать список моделей для формирования
списка пометок и тип отображаемого списка.

### Вызов

Для отображения панельки со списком пометок, у созданного экземпляра
класса `SbisMarksMovablePanelLauncher` нужно вызвать метод `showMarksPanel(...)`. В аргументах нужно
передать `fragmentManager` и экземпляр контейнера, куда будет встроен фрагмент панели с пометками.

### Получение результата

Для получения результата во фрагменте, в котором происходит вызов панельки с пометками, требуется
воспользоваться FragmentResultAPI и подписаться на получение результата методом
`requireActivity().supportFragmentManager.setFragmentResultListener(resultKey, viewLifecycleOwner) {...}`. В
качестве ключа `resultKey` нужно использовать статическую
строку: `SbisMarksMovablePanelLauncher.FRAGMENT_RESULT_KEY`.

Для доступа к данным, хранящимся в получаемом `bundle` нужно воспользоваться ключами из таблицы:

|          | Выбранные элементы                              | Тип списка                                           |
|----------|-------------------------------------------------|------------------------------------------------------|
| Сущность | Список моделей, выбранных пользователем пометок | Тип списка пометок                                   |
| Ключ     | `SbisMarksMovablePanelLauncher.ITEMS_STATE`     | `SbisMarksMovablePanelLauncher.COMPONENT_TYPE_STATE` |
| Тип      | `List<SbisMarksElement>`                        | `SbisMarksComponentType`                             |

### Пример работы с компонентом

```kotlin
// Инициализация
val items = getMarksItemList() // формируем список из необходимых пометок
val componentType = SbisMarksComponentType.COLOR
val panelLauncher = SbisMarksMovablePanelLauncher(items, componentType)


// Отображение
panelLauncher.showMarksPanel(childFragmentManager, binding.marksPanelContainer)


// Получение результата
val resultKey = SbisMarksMovablePanelLauncher.FRAGMENT_RESULT_KEY
val itemsKey = SbisMarksMovablePanelLauncher.ITEMS_STATE
val componentTypeKey = SbisMarksMovablePanelLauncher.COMPONENT_TYPE_STATE

requireActivity().supportFragmentManager.setFragmentResultListener(resultKey, viewLifecycleOwner) { result, bundle ->
    val selectedItems = bundle.getParcelableArrayListUniversally(itemsKey)
    val componentType = bundle.getParcelableUniversally(componentTypeKey)
    // ...
}
```

## Описания UI компонентов

Компонент имеет три типа отображения:

### Цветом

Компонент представляет из себя список пометок где цветом помечены иконки-кружки пометок. Заголовки
пометок имеют одинаковую стандартную стилизацию. Если в списке присутствует уже выбранная пометка,
она отмечается специальным маркером. В данном типе отображения выбрать можно только одну пометку.

### Цветом и стилем

То же самое что и формат **Цветом**, но дополнительно применяется стилизация текста. Текст красится
в соответствующий иконке цвет. К тексту применяется шрифтовая стилизация. Можно комбинировать
несколько шрифтов одновременно. За шрифт заголовка отвечает `SbisMarksFontStyle`.

### С дополнительными пометками

Представляет из себя список пометок состоящих из двух типов: платформенные и цветные. Данный формат
подразумевает множественный выбор пометок. Напротив каждой пометки есть чекбокс. Текст не ни как не
стилизуется. Из списка цветных пометок можно выбрать только одну. Платформенных пометок модно
выбрать несколько.


