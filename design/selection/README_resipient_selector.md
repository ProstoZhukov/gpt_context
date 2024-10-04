#### Название компонента

|Класс|Ответственные|Добавить|
|-----|-------------|--------|
|[RecipientSelectorFragmentFactory](src/main/java/ru/tensor/sbis/design/selection/ui/factories/RecipientSelectorFragmentFactory.kt)|[Бубенщиков Сергей](https://online.sbis.ru/person/1fb93b8c-350f-4785-8589-b0ff2edfbfa7)|[Задачу/поручение/ошибку](https://online.sbis.ru/area/d5cff451-8688-4af0-970a-8127570b0308)|

##### Внешний вид
![recipient_selector_list](/doc_resources/img/recipient-selector-0.png)     ![recipient_selector_with_collapsed_selection](/doc_resources/img/recipient-selector-1.png)     ![recipient_selector_with_expanded_selection](/doc_resources/img/recipient-selector-2.png)         
[Стандарт внешнего вида](http://axure.tensor.ru/MobileStandart8/%D0%B2%D1%8B%D0%B1%D0%BE%D1%80_%D0%B0%D0%B4%D1%80%D0%B5%D1%81%D0%B0%D1%82%D0%BE%D0%B2.html)   

##### Описание
Компонент для выбора адресатов как одного так и нескольких. Компонент из себя представляет фрагмент, на котором показывается строка поиска и список сотрудников/папок/групп, которые можно выбрать. Выбранные сотрудники/папки/группы показываются на этом же фрагменте над списком.

##### Стилизация
У компонента определена тема по умолчанию `SelectionDefaultTheme.Recipient`. 
Для применения прикладной темы, её нужно передать в аргумент `themeRes` фабричного метода:
```kotlin
// создание компонента для одиночного выбора адресатов с прикладной темой
val singleSelector = createSingleRecipientSelector(
    themeRes = R.style.MyRecipientSelectorTheme
)
```
```kotlin
// создание компонента для множественного выбора адресатов с прикладной темой
val singleSelector = createMultiRecipientSelector(
    themeRes = R.style.MyRecipientSelectorTheme
)
```
###### Переопределение темы
При переопределении темы рекомендуется расширить стандартную тему `SelectionDefaultTheme.Recipient`.
Тема компонента составная и включает в себя следующие подтемы:

|Атрибут|Описание|
|-------|--------|
|Selector_headerTheme|Тема шапки (кнопки и строка поиска)|
|Selector_listTheme|Тема списка. Включает стилизацию элементов списка|     

Тема шапки состоит из следующих атрибутов:

|Атрибут|Описание|
|-------|--------|
|Selector_headerContainerStyle|Стиль, который будет применён к контейнеру шапки компонента выбора|
|Selector_cancelButtonStyle|Стиль кнопки "Отмена" в шапке компонента выбора|
|Selector_searchPanelStyle|Стиль панели поиска (см. атрибуты `SearchInput`)|
|Selector_doneButtonStyle|Стиль кнопки применить в шапке компонента множественного выбора|
|Selector_headerDividerStyle|Стиль разделителя шапки компонента выбора|    

Тема списка, в свою очередь, состоит из тем для разного рода элементов:

|Атрибут|Описание|
|-------|--------|
|Selector_personMultiItemTheme|Тема персоны в списке|
|Selector_groupMultiItemTheme|Тема группы соц. сети в списке|
|Selector_departmentMultiItemTheme|Тема рабочей группы в списке|

Каждая из тем элемента списка содержит атрибуты:

|Атрибут|Описание|
|-------|--------|
|Selector_itemContainerStyle|Стиль, который будет применён к контейнеру элемента|
|Selector_itemTitleStyle|Стиль заголовка элемента в компоненте выбора|
|Selector_itemSubtitleStyle|Стиль подзаголовка элемента в компоненте выбора|
|Selector_itemSelectionIconStyle|Стиль кнопки "Выбрать" элемента в компоненте множественного выбора|
|Selector_itemMarkerStyle|Стиль маркера элемента в компоненте одиночного выбора|

В дополнение к основным атрибутам, темы элементов дополняются уникальными атрибутами:

|Атрибут|Описание|
|-------|--------|
|Selector_personPhotoStyle|Стиль фотографии профиля (для элемента персоны)|
|Selector_groupPhotoStyle|Стиль фотографии группы (соц. сети)|
|Selector_departmentIconStyle|Стиль иконки рабочей группы|

Пример переопределения цвета иконки папки для элементов рабочих групп:
```xml
<?xml version="1.0" encoding="utf-8"?>
<resources>
    <style name="MyRecipientTheme" parent="SelectionDefaultTheme.Recipient">
        <item name="Selector_listTheme">@style/MyCustomListTheme</item>
    </style>
    
    <style name="MyCustomListTheme" parent="SelectorDefaultListTheme">
        <item name="Selector_departmentMultiItemTheme">@style/MyDepartmentTheme</item>
    </style>
    
    <style name="MyDepartmentTheme" parent="SelectionRecipientItemTheme.Multi.Department">
        <item name="Selector_departmentIconStyle">@style/MyDepartmentIconStyle</item>
    </style>
    
    <style name="MyDepartmentIconStyle" parent="SelectionRecipientDepartmentIcon">
        <item name="android:textColor">@color/text_color_gray_4</item>
    </style>
</resources>
```

##### Описание особенностей работы 
- ключевой особенностью, на которую нужно обращать внимание при использовании компонента, является 
требование поддержки сериализации для подписок и других терминов API. За счёт этого механизма 
компонент не теряет связь с пользовательским окружением после восстановления состояния
- типовой подход для обработки событий в сериализуемой функции - получение Dagger компонента из контекста. 
Все подписки принимают пользовательские Activity
- предвыбранные данные (прошлый пользовательский выбор) загружаются в компонент только при инициализации