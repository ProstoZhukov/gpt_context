#### Превью выбора

|Класс|Ответственные|Добавить|
|-----|-------------|--------|
|[SelectionPreviewView](src/main/java/ru/tensor/sbis/design/selection/ui/view/selectionpreview/view/SelectionPreviewView.kt)|[Бессонов Ю.С.](https://online.sbis.ru/person/0744ffc8-075a-40e7-a1bd-5d6fff8655f2)|[Задачу/поручение/ошибку](https://online.sbis.ru/area/d5cff451-8688-4af0-970a-8127570b0308)|

#### Использование в приложениях
- [Коммуникатор](https://git.sbis.ru/mobileworkspace/apps/droid/communicator)

##### Внешний вид
![SelectionPreviewView](/doc_resources/img/selection-preview.png)

[Стандарт внешнего вида](http://axure.tensor.ru/MobileAPP/#g=1&p=выбор_региона&c=1)

##### Описание
Компонент предназначен для предварительного просмотра нескольких элементов из числа выбранных (режим превью выбора), либо нескольких предлагаемых элементов для быстрого выбора (режим превью предложений).

В режиме **превью выбора** отображается несколько выбранных элементов. При наличии оставшихся элементов дополнительно присутствует строка с общим числом выбранных.

В режиме **превью предложений** компонент представляет собой блок с заголовком, списком элементов и строкой с надписью "Ещё".

Согласно спецификации, максимальное число видимых элементов - 3, но может быть изменено при необходимости.

##### Стилизация
Тема компонента задаётся атрибутом `selectionPreviewViewTheme`. По умолчанию используется тема [SelectionPreviewView](src/main/res/values/theme_preview.xml).

###### Переопределение темы
При необходимости можно оформить собственную тему, в частности, основываясь на теме по умолчанию. Предусмотрены следующие атрибуты:

|Атрибут|Описание|
|-------|--------|
|SelectionPreviewView_itemBodyStyle|Стиль корневой `View` элемента списка|
|SelectionPreviewView_itemTitleStyle|Стиль текста элемента списка|
|SelectionPreviewView_removeIconStyle|Стиль иконки удаления выбранного элемента|
|SelectionPreviewView_markIconStyle|Стиль отметки выбранного элемента|
|SelectionPreviewView_itemDividerStyle|Стиль разделителя элементов|
|SelectionPreviewView_totalCountStyle|Стиль текста общего числа выбранных|
|SelectionPreviewView_suggestionHeaderTitleStyle|Стиль текста заголовка превью предложений|
|SelectionPreviewView_suggestionArrowIconStyle|Стиль иконки справа от заголовка превью предложений|
|SelectionPreviewView_counterStyle|Стиль значения счётчика|
|SelectionPreviewView_moreTextStyle|Стиль текста "Ещё"|

##### Описание особенностей работы
- Компонент принимает на вход объект типа, представляющего [SelectionPreviewData](src/main/java/ru/tensor/sbis/design/selection/ui/view/selectionpreview/model/SelectionPreviewData.kt), а именно
    - `SelectionPreviewListData` с данными для отображения превью выбранных элементов
    - либо `SelectionSuggestionListData` с данными для отображения превью предложений выбора
- Для переопределения макс. числа отображаемых элементов необходимо задать значение `maxDisplayedEntries` при создании модели для отображения.  
- Необходимость отображения строки с надписью "Ещё" в превью предолжений выбора определяется значением флага `showMoreItem` в `SelectionSuggestionListData`. При отсутствии значения строка будет показана только если число элементов превышает `maxDisplayedEntries`.  
- Выбранные элементы должны реализовывать `SelectionPreviewItem`, а предлагаемые - `SelectionSuggestionItem` (доступны реализации по умолчанию - `DefaultSelectionPreviewItem` и `DefaultSelectionSuggestionItem`).
- Обработчик действий с элементами должен реализовывать [SelectionPreviewActionListener](src/main/java/ru/tensor/sbis/design/selection/ui/view/selectionpreview/listener/SelectionPreviewActionListener.kt). Поскольку этот интерфейс общий для превью выбора и превью предложений, его можно использовать при переключении режима работы. Если некоторые методы не требуются, рекомендуется использовать `SelectionPreviewActionListenerAdapter` и реализовывать только необходимые.
- Для показа содержимого используется метод `showData(SelectionPreviewData)`. При работе с databinding следует применять адаптер `selectionPreviewData`.

- Пример использования превью выбора:
```kotlin
private lateinit var selectedRegions: List<DefaultSelectionPreviewItem>
private lateinit var regionSelectionPreviewView: SelectionPreviewView

private val previewActionListener = object : SelectionPreviewActionListenerAdapter<DefaultSelectionPreviewItem>() {
    override fun onItemClick(item: DefaultSelectionPreviewItem) {
        // обработка нажатия на элемент
    }

    override fun onRemoveClick(item: DefaultSelectionPreviewItem) {
        // обработка нажатия на кнопку удаления
    }
}

fun createRegionsPreviewData(): SelectionPreviewListData<DefaultSelectionPreviewItem> {
    return SelectionPreviewListData(
        items = selectedRegions,
        actionListener = previewActionListener
    )
}

fun updateRegionsPreview() {
    // отображение превью выбранных элементов
    regionSelectionPreviewView.showData(createRegionsPreviewData())
}
```

- Пример использования превью предложений выбора:
```kotlin
private lateinit var suggestedCategories: List<DefaultSelectionPreviewItem>
private lateinit var categorySelectionPreviewView: SelectionPreviewView

private val suggestionActionListener = object : SelectionPreviewActionListenerAdapter<DefaultSelectionSuggestionItem>() {
    override fun onItemClick(item: DefaultSelectionSuggestionItem) {
        // обработка нажатия на элемент
    }

    override fun onShowAllClick() {
        // обработка нажатия на заголовок и надпись "Ещё" для показа всех доступных элементов
    }
}

fun createCategoriesSuggestionData(): SelectionSuggestionListData<DefaultSelectionSuggestionItem> {
    return SelectionSuggestionListData(
        headerTitle = R.string.categories_title,
        items = suggestedCategories,
        actionListener = suggestionActionListener
    )
}

fun updateCategoriesSuggestionPreview() {
    // отображение превью предлагаемых элементов
    categorySelectionPreviewView.showData(createCategoriesSuggestionData())
}
```

- Пример использования с databinding:
```xml
<?xml version="1.0" encoding="utf-8"?>
<layout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto">

    <data>
        <variable
            name="categoriesSuggestion"
            type="ru.tensor.sbis.design.selection.ui.view.selectionpreview.model.SelectionPreviewData"/>

        <variable
            name="regionsPreview"
            type="ru.tensor.sbis.design.selection.ui.view.selectionpreview.model.SelectionPreviewData"/>
    </data>

    <LinearLayout
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        android:orientation="vertical">

        <ru.tensor.sbis.design.selection.ui.view.selectionpreview.view.SelectionPreviewView
            android:id="@+id/categories"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            app:selectionPreviewData="@{categoriesSuggestion}"/>

        <ru.tensor.sbis.design.selection.ui.view.selectionpreview.view.SelectionPreviewView
            android:id="@+id/regions"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            app:selectionPreviewData="@{regionsPreview}"/>

    </LinearLayout>
</layout>
```
```kotlin
binding.regionsPreview = createRegionsPreviewData()
binding.categoriesSuggestion = createCategoriesSuggestionData()
```

##### Использование в приложениях
- [Курьер](https://git.sbis.ru/mobileworkspace/apps/droid/courier)