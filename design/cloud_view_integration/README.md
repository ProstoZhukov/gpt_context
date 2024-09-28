# Интеграция ячейки-облака
| Ответственность | Ответственные |
|-----------------|---------------|
| Разработка | [Бубенщиков Сергей](https://online.sbis.ru/person/1fb93b8c-350f-4785-8589-b0ff2edfbfa7) |
| [QuoteLongClickSpan](src/main/java/ru/tensor/sbis/design/cloud_view_integration/QuoteLongClickSpan.kt) |
| [RichTextMessageBlockTextHolder](src/main/java/ru/tensor/sbis/design/cloud_view_integration/RichTextMessageBlockTextHolder.kt) |

## Использование в приложениях
- [Коммуникатор](https://git.sbis.ru/mobileworkspace/apps/droid/communicator)

## Описание
Модуль используется для интеграции RichText в CloudView через MessageBlockTextHolder

## Руководство по подключению и инициализации
Для подключения модуля приложение должно иметь в зависимостях richtext и cloud_view

## Руководство по использованию
Для использования достаточно в месте объявления CloudView установить TextHolder
```kotlin
cloudView.setTextHolder(RichTextMessageBlockTextHolder())
```
