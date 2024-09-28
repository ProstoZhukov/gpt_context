#### Заголовок для тулбара

|Класс|Ответственные|Добавить|
|-----|-------------|--------|

|[SbisTitleView](src/main/java/ru/tensor/sbis/design/view/titleview/SbisTitleView.kt)|[Старицын Никита](https://online.sbis.ru/person/2adcc879-e1a5-464e-bc50-b13d41e00a10)|[Задачу/поручение/ошибку](https://online.sbis.ru/area/d5cff451-8688-4af0-970a-8127570b0308)|

##### Внешний вид
[Стандарт внешнего вида](http://axure.tensor.ru/MobileStandart8/#p=шапка_v3&g=1)   

##### Описание
`SbisTitleView` - вью, предназначенная для отображения заголовка, подзаголовка и фотографий. Можно использовать в качестве контента для [Toolbar](ru.tensor.sbis.design.toolbar.Toolbar).

Пример построения макета с тулбаром:
```xml
<ru.tensor.sbis.design.toolbar.Toolbar
    android:id="@+id/toolbar"
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    app:customViewContainerVisibility="visible">
    
    <ru.tensor.sbis.design.profile.titleview.SbisTitleView
        android:id="@+id/toolbar_title_view"
        android:layout_width="match_parent"
        android:layout_height="match_parent"/>
        
</ru.tensor.sbis.design.toolbar.Toolbar>
```

Компонент `SbisTitleView` отображает данные, которые могут быть описаны моделями данных [TitleViewContent](design.view.titleview.model.TitleViewContent): [Persons](design.view.titleview.model.Persons), [Collage](design.view.titleview.model.Collage) и [Default](design.view.titleview.model.Default)

Для отображения данных достаточно вызвать:
```kotlin
titleView.content = ...
```
`Persons`:

Для использования требуется список персон, у которых [Person.name] и [Person.photoUrl] непустые

- Заголовок формируется как перечисление [Person.name](design.view.titleview.model.Person.name) через запятую.
- Подзаголовок формируется как перечисление непустых [Person.info](design.view.titleview.model.Person.info) через запятую.
- Вью для фотографий видна всегда, даже есть их нет - в таком случае будет дефолтная заглушка.
- Фото - список ссылок.

`Collage`:

- Заголовок формируется как перечисление [titles](design.view.titleview.model.Collage.titles) через запятую.
- Подзаголовок - [subtitle](design.view.titleview.model.Collage.subtitle).
- Вью для фотографий видна всегда, даже есть их нет - в таком случае будет отображаться заглушка из [imagePlaceholderRes](design.view.titleview.model.Collage.imagePlaceholderRes).
- Фото - список ссылок

`Default`:

- Заголовок [title](design.view.titleview.model.Default.title).
- Подзаголовок - [subtitle](design.view.titleview.model.Default.subtitle).
- Вью для фотографий скрывается, если нет ссылки для фотографии
- Фото - одна ссылка

Пример отображения данных персон
```kotlin
titleView.content = Persons(listOf(Person(photoUrl, name, info)))
```
Пример отображения данных как коллаж
```kotlin
titleView.content = Collage(
	listOf(title1, title2),
	listOf(imageUrl1, imageUrl2, imageUrl3),
	subtitle,
	R.drawable.ic_image_holder_simple_circle
)
```
Пример простого отображения данных
```kotlin
titleView.content = Default(
	title,
	subtitle,
	imageUrl,
	R.drawable.ic_image_holder_simple_circle
)
```
##### xml атрибуты
- `DesignSbisTitleView_titleColor` - цвет для заголовка шапки
- `DesignSbisTitleView_subtitleColor` - цвет для подзаголовка шапки

##### Стилизация
Для `SbisTitleView` существует две основные темы `DesignSbisTitleViewThemeWhiteText` и `DesignSbisTitleViewThemeDarkText`.
В их состав включены атрибуты:
- `DesignSbisTitleView_titleColor` 
- `DesignSbisTitleView_subtitleColor`

Тему можно подключить из атрибута `designSbisTitleViewTheme`
```xml
<style name="AppTheme">
    <item name="designSbisTitleViewTheme">@style/DesignSbisTitleViewThemeDarkText</item>
</style>
```
В случае, если атрибут не указан, используется стандартная тема `DesignSbisTitleViewThemeWhiteText`.

##### Трудозатраты внедрения
0.7 ч/д