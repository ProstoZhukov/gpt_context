### Группа компонентов для организации фильтрации реестра по выбранной персоне.

|Класс|Ответственные|
|-----|-------------|
|[PersonSuggestView](src/main/java/ru/tensor/sbis/design/person_suggest/suggest/PersonSuggestView.kt)|[Чекурда Владимир](https://online.sbis.ru/person/0fe3e077-6d50-431c-9353-f630fc789877)|
|[PersonInputLayout](src/main/java/ru/tensor/sbis/design/person_suggest/input/PersonInputLayout.kt)|[Чекурда Владимир](https://online.sbis.ru/person/0fe3e077-6d50-431c-9353-f630fc789877)|
|[PersonSuggestServiceWrapper](src/main/java/ru/tensor/sbis/design/person_suggest/service/PersonSuggestServiceWrapper.kt)|[Чекурда Владимир](https://online.sbis.ru/person/0fe3e077-6d50-431c-9353-f630fc789877)|

#### I) PersonSuggestView

Компонент предназначен для отображения подсказки в виде списка фотографий персон для быстрого выбора. В частности используется в сценариях поиска, чтобы дать пользователю возможность выбрать персону без ввода символов в строку поиска.

Поддерживает два поведения отображения:
 - Стандартный - позволяет делегировать ему события клавиатуры для показа панели над клавиатурой.
 - Кастомный - Вы можете самостоятельно расположить элемент в любом месте и управлять его видимостью, если стандартное поведение не подходит для Ваших сценариев использования.

API компонента [PersonSuggestViewApi](src/main/java/ru/tensor/sbis/design/person_suggest/suggest/contract/PersonSuggestViewApi.kt).

##### Подключение

Добавление в разметку:
```xml
<ru.tensor.sbis.design.person_suggest.panel.PersonSuggestView
    android:id="@+id/person_suggest_view"
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    android:visibility="gone"/>
```

Перед использованием компонента необходимо его проициниализировать с помощью метода _init()_, в который нужно передать слушатель модели выбранной персоны `PersonSelectionListener`. При желании подключить статусы активности персон - опционально можно передать `ActivityStatusConductor`.
```kotlin
personSuggestView.init { personData ->
    onPersonSelected(personData)
}
```

Для отображения данных необходимо передать список моделей персон, которые вы хотите отобразить. Ограничений на количество элементов в списке нет.
```kotlin
personSuggestView.data = personDataList
```

Далее у нас 2 варианта настройки поведения компонента:
1) Стандартное поведение. Оно подразумевает показ панели при открытии клавиатуры, и скрытие при закрытии клавиатуры.

Для этого необходимо делегировать панели события клавиатуры через `AdjustResizeHelper.KeyboardEventListener`.
```kotlin
class MyFragment : Fragment(), AdjustResizeHelper.KeyboardEventListener {
    
    override fun onKeyboardOpenMeasure(keyboardHeight: Int): Boolean {
        personSuggestView.onKeyboardOpenMeasure(keyboardHeight)
        return true
    }

    override fun onKeyboardCloseMeasure(keyboardHeight: Int): Boolean {
        personSuggestView.onKeyboardCloseMeasure(keyboardHeight)
        return true
    }
}
```

А также установить значение флага `showOnKeyboard` в нужное вам состояние.
```kotlin
personSuggestView.showOnKeyboard = needToShowPanel
```
3 важных уточнения для `showOnKeyboard`:
 - Если вам необходимо всегда отображать панель при подъеме клавиатуры, то Вы можете единожды установить `showOnKeyboard` в значеие `true`.
 - Если же Вам нужно иногда показывать, а иногда нет, то можете переключать флаг в `showOnKeyboard` в значеие `false` для сценариев, когда панель не должна отображаться с поднятой клавиатурой.
 - Панель не будет отображаться с поднятой клавиатурой, если в ней нет данных для отображения.

При делегировании событий клавиатуры Вам может понадобиться еще один флаг `translateOnKeyboard`. В зависимости от `windowSoftInputMode` на Вашей активити у Вас может возникнуть 2 сценария:
 - `adjustPan` - значение флага компонента `translateOnKeyboard` должно быть `true`. С этим флагом панель будет автоматически смещаться на переданное значение высоты клавиатуры. Это дефолтное значение на момент написания документации.
 - `adjustResize` - значение флага компонента `translateOnKeyboard` должно быть `false`. С этим флагом панель не будет автоматически смещаться на переданное значение высоты клавиатуры. Смещение будет происходить за счет уменьшения View вашей Activity.
```kotlin
personSuggestView.translateOnKeyboard = isAdjustPan
```

2) Кастомное поведение компонента. 
В этом сценарии Вы можете самостоятельно размещать компонент в любом месте, смещать его, скрывать, показывать, компонент не будет Вам сопротивляться, если Вы не будете пользоваться вышеописанным API для стандартного поведения.

##### Стилизация

Стандартный стиль компонента `PersonSuggestViewDefaultStyle`

Атрибуты компонента:
 - `PersonSuggestView_backgroundColor` - цвета фона панели выбора персон.
 - `PersonSuggestView_listHorizontalPadding` - горизонтальные отступы списка.
 - `PersonSuggestView_personHorizontalPadding` - горизонтальные отступы фото персоны.
 - `PersonSuggestView_personVerticalPadding` - вертикальные отступы фото персоны.
 - `PersonSuggestView_photoSize` - размер фотографии персоны.

Доступно переопределения стандартных отступов, размера фотографий и цвета фона несколькими способами:
1) Локально через установку атрибутов компонента.
```xml
<ru.tensor.sbis.design.person_suggest.panel.PersonSuggestView
    app:PersonSuggestView_backgroundColor="@color/sbis_gray"
/>
```

2) Локально через установку переопределенного стиля.
```xml
<resources>

    <style name="MyPersonSuggestStyle" parent="PersonSuggestViewDefaultStyle">
        <item name="PersonSuggestView_backgroundColor">@color/sbis_gray</item>
    </style>

</resources>
```

```xml
<ru.tensor.sbis.design.person_suggest.panel.PersonSuggestView
    style="@style/MyPersonSuggestStyle"
/>
```

3) Глобально для всей активити или приложения с помощью атрибута темы компонента `personSuggestViewTheme`.
```xml
<resources>

    <style name="AppTheme" parent="Theme.AppCompat.Light.DarkActionBar">
        <item name="personSuggestViewTheme">@style/MyPersonSuggestTheme</item>
    </style>

</resources>
```

#### II) PersonInputLayout

Компонент предназначен для возможности отображения поисковой строки вместе с фильтром по персоне.
`PersonInputLayout` - это контейнер с дополнительным функционалом, в который помещается Ваш настроенный компонент поисковой строки `SearchInput`.

API компонента [PersonInputLayoutApi](src/main/java/ru/tensor/sbis/design/person_suggest/suggest/contract/PersonSuggestViewApi.kt).

##### Подключение

Добавление в разметку:
```xml
<ru.tensor.sbis.design.person_suggest.input.PersonInputLayout
    android:id="@+id/person_input_layout"
    android:layout_width="match_parent"
    android:layout_height="wrap_content">

    <ru.tensor.sbis.design.view.input.searchinput.SearchInput
        android:id="@+id/search_filter_panel"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        app:inputDelay="0"/>

</ru.tensor.sbis.design.person_suggest.input.PersonInputLayout>
```

После этого компонент готов к использованию, никаких дополнительных инициализаций не требуется.

Для установки фильтра по персоне необходимо передать компоненту модель с данными по персоне [PersonSuggestData](src/main/java/ru/tensor/sbis/design/person_suggest/service/PersonSuggestData.kt).
```kotlin
fun setPersonFilter(personSuggestData: PersonSuggestData) {
    personInputLayout.personFilter = personSuggestData
}
```

Также в компонент можно установить слушателя для отслеживаний кликов по персоне в фильтре и по крестику - для сброса фильтра по персоне:
```kotlin
personInputLayout.listener = object : PersonInputLayoutListener {
    override fun onPersonClick(personUuid: UUID) {
        // Здесь Вы можете открыть карточку персоны или выполнить любое другое действие.
    }

    override fun onCancelPersonFilterClick() {
        // Здесь Вам необходимо сбросить фильтр по персоне в реестре. В контейнере фильтр персоны скроется самостоятельно.
    }
}
```

##### Стилизация

Стандартный стиль компонента `PersonInputLayoutDefaultStyle`

Атрибуты компонента:
- `PersonInputLayout_personPhotoStyle` - стиль фотографии персоны в фильтре.
- `PersonInputLayout_personNameStyle` - стиль имени персоны в фильтре.
- `PersonInputLayout_clearButtonStyle` - стиль кнопки закрытия фильтра по персоне.
- `PersonInputLayout_verticalDividerColor` - цвет вертикального разделителя между фильтром персоны и поисковой строкой.
- `PersonInputLayout_hintText` - текст подсказки, который будет отображается, когда выбран фильтр по персоне и поисковая строка пустая.