# Компонент текста SbisTextView

| Модуль                  | Ответственные                                                                          |
|-------------------------|----------------------------------------------------------------------------------------|
| [design_sbis_text_view] | [Чекурда Владимир](https://online.sbis.ru/person/0fe3e077-6d50-431c-9353-f630fc789877) |

#### Описание
Модуль содержит реализацию SbisTextView - компонент для отображения текста на базе компонента текстовой разметки TextLayout,
созданный для ускорения создания и обновления интерфейсной части текста.
- SbisTextView - компонент
- SbisTextViewApi - функционал компонента

#### Подключение
Модуль подключается к проекту в settings.gradle следующим образом:  
`include ':design_sbis_text_view'`  
`project(':design_sbis_text_view').projectDir = new File(settingsDir, 'design/design_sbis_text_view')`

#### Использование
Для использования компонента в вашем модуле должна быть объявлена зависимость в файле build.gradle  
`implementation project(':design_sbis_text_view')`

#### Отображение SbisTextView

Компонент можно отобразить двумя способами:
1) Добавление SbisTextView в xml разметку:
```xml
<ru.tensor.sbis.design.sbis_text_view.SbisTextView
    android:id="@+id/sbis_text_view"
    android:layout_width="match_parent"
    android:layout_height="match_parent"/>
```
2) Программно, путем создания view.
```kotlin
val sbisTextView1 = SbisTextView(context, attrs, R.attr.yourAttr, R.style.yourStyle)
val sbisTextView2 = SbisTextView(context, sbisTextViewConfig)
val sbisTextView3 = SbisTextView(context, R.style.yourStyle, sbisTextViewConfig)
```

##### Стилизация
Стандартная тема компонента SbisTextView: `SbisTextViewDefaultTheme`.
Атрибут для установки темы компонента SbisTextView: `sbisTextViewTheme`(src/main/res/values/attrs.xml)
здесь же можно найти атрибуты стилизации.

Способы применения стилей и темы аналогичны стандартному View.
Дополнительно предоставлен конструктор, который позволяет программно создать SbisTextView по стилю.
```kotlin
val sbisTextView3 = SbisTextView(context, R.style.yourStyle, sbisTextViewConfig)
```

##### Описание особенностей работы
SbisTextView - оптимизированный вариант TextView, поэтому в нем отсутствуют некоторые возможности
оригинального компонента.
Компонент может расширяться, поэтому если вам 
не хватает какого-то API для вашей интеграции - обратитесь к ответственному за компонент.
**Не могут быть поддержаны**
-`MovementMethod`
-`Функционал скролла внутри View`

**Особенности установки значений некоторых атрибутов**
- app:text принимает @StringRes
- Вместо app:textStyle используем app:textAppearance
- Доступ через layout.paint:  
  - было: letterSpacing = -0.05f
  - стало: layout.paint.letterSpacing = -0.05f
**Для выделения текста цветом использовать TextHighlights**
```kotlin
val textHighlights = TextHighlights(
  highlightedRanges.map { HighlightSpan(it.first, it.last) },
  resources.getColor(R.color.text_search_highlight_color)
)
setTextWithHighlights(text, textHighlights)
```
##### Использование в приложениях

Все приложения, т.к. SbisTextView внедрен в общие компоненты.