#### Компонент Чекбокс

| Класс                                                                                | Ответственные                                                                       |
|--------------------------------------------------------------------------------------|-------------------------------------------------------------------------------------|
| [SbisCheckboxView](src/main/java/ru/tensor/sbis/design/checkbox/SbisCheckboxView.kt) | [Круглова М.Б.](https://online.sbis.ru/person/8a7248e7-b4b2-4c2e-a988-3534eab414f8) |

#### Использование в приложениях

##### Внешний вид

[Стандарт внешнего вида](https://www.figma.com/proto/VK0uvII8ioHnSd1Gy7r44w/%D0%A7%D0%B5%D0%BA%D0%B1%D0%BE%D0%BA%D1%81%D1%8B-(Web%2C-Mobile%2C-Retail)?node-id=632%3A12288)

##### Описание

Чекбокс — компонент, позволяющий управлять параметром с двумя состояниями — выбран и не выбран.

##### xml атрибуты

ru/tensor/sbis/design/checkbox

- 'SbisCheckbox_size' - атрибут для указания размера чекбокса.
    - 's' - маленький, соответствует iconSize_2xl
    - 'l' - большой, соответствует iconSize_7xl

- 'SbisCheckbox_mode' - атрибут для указания вида чекбокса
    - 'standard' - по умолчанию
    - 'accent' - акцентный

- 'SbisCheckbox_text' - атрибут для указания текста для текстовой метки чекбокса. Если указан еще и атрибут
  SbisCheckbox_icon, то текстовая метка не будет отрисована, а метка-иконка будет.

- 'SbisCheckbox_textColor' - атрибут для указания цвета текста для текстовой метки.

- 'SbisCheckbox_isMaxLines' - атрибут для указания включение максимального количества строк для текстового поля.

- 'SbisCheckbox_icon' - атрибут для указания шрифтовой иконки для метки-иконки чекбокса.

- 'SbisCheckbox_iconColor' - атрибут для указания цвета иконки для метки-иконки чекбокса.

- 'SbisCheckbox_contentPosition' - атрибут для указания положения метки относительно чекбокса
  - 'right' - чекбокс слева, метка справа (по умолчанию)
  - 'left' - чекбокс справа, метка слева

- 'SbisCheckbox_useVerticalOffset' - атрибут для указания использования дефолтных отступов сверху и снизу (offset_2xs)
  
##### Стилизация
Тема компонента задаётся атрибутом `sbisCheckboxDefaultsTheme`. По умолчанию используется тема [SbisCheckboxDefaultsTheme](src/main/res/values/theme_sbis_checkbox.xml).

| Тема                             | Описание                                                                         |
|----------------------------------|----------------------------------------------------------------------------------|
| `SbisCheckboxDefaultsTheme`      | Обычная тема (по умолчанию)                                                      |  
| `SbisCheckboxOutlinedTheme`      | Тема с использованием фона без заливки                                           |  
| `SbisCheckboxDefaultsLargeTheme` | Обычная тема с использованием размеров **LargeMobile**                           |  
| `SbisCheckboxOutlinedLargeTheme` | Тема с использованием фона без заливки с использованием размеров **LargeMobile** |

###### Переопределение темы
При необходимости можно оформить собственную тему, задав требуемые значения следующих атрибутов:

| Атрибут                                  | Описание                                                                    |
|------------------------------------------|-----------------------------------------------------------------------------|
| `sbisCheckboxMarkerColor`                | Цвет "галки" для вида по умолчанию или цвет фона для акцентного вида        |  
| `sbisCheckboxBorderColor`                | Цвет окошка чекбокса                                                        |  
| `sbisCheckboxReadOnlyMarkerColor`        | Цвет "галки" в режиме readOnly                                              |  
| `sbisCheckboxAccentMarkerColor`          | Цвет "галки" в акцентном стиле                                              |  
| `sbisCheckboxReadOnlyBorderColor`        | Цвет окошка чекбокса в режиме readOnly                                      |
| `sbisCheckboxPresetBackgroundColor`      | Цвет фона для предустановленного значения, если чекбокс выбран/не определен |
| `sbisCheckboxPresetOffBackgroundColor`   | Цвет фона для предустановленного значения, если чекбокс не выбран           |
| `sbisCheckboxValidationBorderColorError` | Цвет окошка чекбокса при включенной валидации типа Error                    |
| `sbisCheckboxValidationTopMargin`        | Размер отступа между текстом валидации и самим чекбоксом                    |
| `sbisCheckboxContentStartMargin`         | Размер отступа между чекбоксом и контентом сбоку                            |

##### Описание особенностей работы
- Пример использования:
```xml
<ru.tensor.sbis.design.checkbox.SbisCheckboxView
    android:id="@+id/design_demo_checkbox"
    android:layout_width="wrap_content"
    android:layout_height="wrap_content"
    app:SbisCheckbox_size="l"
    app:SbisCheckbox_mode="accent"    
    app:SbisCheckbox_text="Hello" 
    app:SbisCheckbox_textColor="?labelTextColor" 
    app:SbisCheckbox_isMaxLines="true"
    app:SbisCheckbox_icon="\uE93C"
    app:SbisCheckbox_position="right" 
    app:SbisCheckbox_useVerticalOffset="true"/>
```
```kotlin
viewBinding.designDemoCheckbox.value = SbisCheckboxValue.CHECKED
viewBinding.designDemoCheckbox.value = SbisCheckboxValue.UNCHECKED
viewBinding.designDemoCheckbox.value = SbisCheckboxValue.UNDEFINED
viewBinding.designDemoCheckbox.isEnabled = true
viewBinding.designDemoCheckbox.size = SbisCheckboxSize.LARGE
viewBinding.designDemoCheckbox.mode = SbisCheckboxMode.ACCENT
viewBinding.designDemoCheckbox.presetState = true
viewBinding.designDemoCheckbox.position = SbisCheckboxPosition.RIGHT
viewBinding.designDemoCheckbox.useVerticalOffset = true
viewBinding.designDemoCheckbox.isMaxLines = true
viewBinding.designDemoCheckbox.validationState = SbisCheckboxValidationState.Error("Обязательное поле")

viewBinding.designDemoCheckbox.content = TextContent("Hello")
viewBinding.designDemoCheckbox.content = TextContent("Hello", Color.parseColor("#DD2044"), true)
val colorAttr = TypedValue()
context.theme.resolveAttribute(ru.tensor.sbis.design.R.attr.labelTextColor, colorAttr, true)
viewBinding.designDemoCheckbox.content = TextContent("Hello", colorAttr.data)

viewBinding.designDemoCheckbox.content = IconContent("\uE9EB")
val icon = ResourcesCompat.getDrawable(context.resources, ru.tensor.sbis.design.R.drawable.ic_sbis_bird, context.theme)
viewBinding.designDemoCheckbox.content = IconContent(icon)
viewBinding.designDemoCheckbox.content = IconContent(icon, Color.parseColor("#DD2044"))
```

##### Трудозатраты внедрения
0.6 ч/д