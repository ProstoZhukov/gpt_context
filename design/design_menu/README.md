## Меню

| Класс                                                                          | Ответственные                                                                    | Добавить                                                                                    |
|--------------------------------------------------------------------------------|----------------------------------------------------------------------------------|---------------------------------------------------------------------------------------------|
| [SbisMenu](src/main/java/ru/tensor/sbis/design/design_menu/SbisMenu.kt)        | [Гераькин Р.A.](https://dev.sbis.ru/person/45883fa3-9b14-458b-97b3-3f55f527230d) | [Задачу/поручение/ошибку](https://online.sbis.ru/area/63ffe6b3-193f-4d77-91c5-3bafeef9cf49) |

### Внешний вид

[Стандарт внешнего вида](http://axure.tensor.ru/MobileStandart8/#p=%D0%BC%D0%B5%D0%BD%D1%8E&g=1)
[Макет](https://www.figma.com/proto/VN4mmF4SyNlVItzbIz5H2u/%E2%9C%94%EF%B8%8F-%D0%9C%D0%B5%D0%BD%D1%8E?page-id=10359%3A21991&node-id=58233-101110&t=vpZtmG0evMrEbNmN-0&scaling=min-zoom&starting-point-node-id=58233%3A101110&hide-ui=1)
[Спецификация](https://n.sbis.ru/article/menu_sabydoc)

### Описание

Меню предназначено для простого выбора одного параметра или действия из списка.
Может отображаться в трёх вариантах:

- в контейнере
- в шторке (снизу)
- быть встроенным в любое место как обычное View

### Руководство по подключению и инициализации

Для добавления модуля в проект, в `settings.gradle` проекта должны быть подключены следующие модули:

| Репозиторий                                            | Модуль                |
|--------------------------------------------------------|-----------------------|
| https://git.sbis.ru/mobileworkspace/android-design.git | design                |
| https://git.sbis.ru/mobileworkspace/android-design.git | design_sbis_text_view |
| https://git.sbis.ru/mobileworkspace/android-design.git | design_utils          |
| https://git.sbis.ru/mobileworkspace/android-design.git | design_dialogs        |
| https://git.sbis.ru/mobileworkspace/android-utils.git  | modalwindows          |
| https://git.sbis.ru/mobileworkspace/android-utils.git  | base_components       |
| https://git.sbis.ru/mobileworkspace/android-utils.git  | common                |

### Стилизация

Тема компонента основана на глобальных атрибутах и не имеет отдельного атрибута для переопределения
темы.

### API

#### Модели

Модели элементов меню для создания:

| Модель                       | Класс                                                                                          | Описание                                                                                                        |
|------------------------------|------------------------------------------------------------------------------------------------|-----------------------------------------------------------------------------------------------------------------|
| Меню                         | [SbisMenu](src/main/java/ru/tensor/sbis/design/design_menu/SbisMenu.kt)                        | Является корневым классом. Может выступать в роли подменю, являясь обычным элементов в другом меню.             |
| Обычный элемент              | [SbisMenuItem](src/main/java/ru/tensor/sbis/design/design_menu/SbisMenuItem.kt)                | Обычный элемент меню.                                                                                           |
| Меню со вложенными пунктами  | [SbisMenuNested](src/main/java/ru/tensor/sbis/design/design_menu/SbisMenuNested.kt)            | Меню со вложенными элементами. Элементы рисуются в том же окне/шторке что и остальные, но имеют отступ от края. |
| Элемент с прикладным View    | [CustomViewMenuItem](src/main/java/ru/tensor/sbis/design/design_menu/CustomViewMenuItem.kt)    | Элемент меню, которое использует прикладное View для отображения внутри себя. Больше ничего не может.           |
| Разделитель линией           | [LineDivider](src/main/java/ru/tensor/sbis/design/design_menu/dividers/LineDivider.kt)         | Сплошная линия от края до края.                                                                                 |
| Разделитель текстом          | [TextDivider](src/main/java/ru/tensor/sbis/design/design_menu/dividers/TextDivider.kt)         | Только текст по центру.                                                                                         |
| Разделитель линией и текстом | [TextLineDivider](src/main/java/ru/tensor/sbis/design/design_menu/dividers/TextLineDivider.kt) | Разделитель, в котором текст можно расположить справа, по центру или слева, относительно ширины меню.           |

#### Методы отображения

Для показа меню используются фабричные методы расширения
класса [SbisMenu](src/main/java/ru/tensor/sbis/design/design_menu/SbisMenu.kt) находящиеся в
файле [MenuFactories](src/main/java/ru/tensor/sbis/design/design_menu/MenuFactories.kt):

| Метод                             | Описание                                                                                                                                                                                                                                     |
|-----------------------------------|----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| `showPanel`                       | Отображение меню в нижней шторке.                                                                                                                                                                                                            |
| `showMenu`                        | Показывает меню относительно вызывающего элемента (Не в списке). С горизонтальным выравниванием по центру вызывающего элемента. С вертикальным выравниванием снизу от вызывающего элемента. Если не поместилось снизу будет показано сверху. |
| `showMenuWithScreenAlignment`     | Показывает меню относительно вызывающего элемента внутри RecyclerView с выравниванием по горизонтали относительно экрана, по вертикали относительно якоря снизу. Если не вместилось, то сверху.                                              |
| `showMenuWithLocators`            | Универсальный метод отображения меню. Поведение меню настраивается с помощью `ScreenLocator` `AnchorLocator`.                                                                                                                                |
| `showMenuWithAnchorLocatorsByTag` | Универсальный метод отображения меню относительно вью с использованием тегов для определения якоря. Поведение меню настраивается с помощью `AnchorLocator`.                                                                                  |
| `showMenuForConversationRegistry` | Отобразить меню в переписке по особым правилам расположения меню относительно вызывающего элемента.                                                                                                                                          |

### Пример использования

```kotlin
val menu = SbisMenu(
  title = "Заголовок",
  children = listOf(
    SbisMenuItem(
      title = "Заголовок 1",
      icon = SbisMobileIcon.Icon.smi_Loading
    ),
    SbisMenuItem(
      title = "Заголовок 2",
      icon = SbisMobileIcon.Icon.smi_Library
    ),
    SbisMenuItem(
      title = "Заголовок 3",
      icon = SbisMobileIcon.Icon.smi_link
    ),
    SbisMenuItem(
      title = "Заголовок 4",
      icon = SbisMobileIcon.Icon.smi_kalendar
    )
  )
menu.showMenu(childFragmentManager, anchor, dimType = DimType.SHADOW)
```

### Трудозатраты внедрения

0.9 ч/д