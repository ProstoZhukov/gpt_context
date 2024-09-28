# Модуль "Панель вкладок"
| Ответственность | Ответственные |
|-----------------|---------------|
| Разработка | [Абраменко А.И.](https://dev.saby.ru/person/ab170353-ca5c-4e65-aa67-d58cdabc8b22) |

## Описание
Компонент "Панель вкладок" небходим для отображения списка вкладок в виде "Иконка + текст" и выделения выбранной вкладки.
Используется в компоненте "Выбор файлов" и "Шаринг файлов".

## Руководство по подключению и инициализации
Для использования небходимо подключить модуль через **gradle** файл:
```kotlin
implementation project(':design_tab_panel')
```
После, можно использовать либо напрямую через код:
```kotlin
val tabPanel = TabPanelView(context)
```
Либо через **xml**:

```xml
<ru.tensor.sbis.design.tab_panel.TabPanelView />
```

## Описание публичного API
Для отображения вкладок, небходимо их установить через ***TabPanelView.setTabPanelItems***
Также можно установить начальный выбранный элемент через ***TabPanelView.setSelectedItem***
Установка слушателя очуществляется через ***TabPanelView.setClickItemHandler***
```kotlin
val tabPanel = TabPanelView(context)
val tab1 = DefaultTabPanelItem(id = "Tab1", icon = SbisMobileIcon.Icon.smi_3D, title = R.string.tab1_title)
val tab2 = DefaultTabPanelItem(id = "Tab2", icon = SbisMobileIcon.Icon.smi_AddFolder, title = R.string.tab2_title)
val tabs = listOf(tab1, tab2)
tabPanel.setTabPanelItems(tabs)
tabPanel.setSelectedItem(tab1)
tabPanel.setClickItemHandler { item: TabPanelItem ->
    // Handler
}
```