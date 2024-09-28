# Форматтеры с учетом локализации
| Ответственные                                                                    |
|----------------------------------------------------------------------------------|
| [Смирных Павел](https://dev.sbis.ru/person/9bbcd3ea-ccea-4c94-a883-19c0d1d0ce0f) |

## Описание
Форматтеры предназначены для форматирования значений с учетом региона и локали пользователя.
Сейчас реализованы форматтеры валюты [CurrencyFormatter](src/main/kotlin/ru/tensor/sbis/formatter/currency/CurrencyFormatter.kt) и
даты [DateTimeFormatter](src/main/kotlin/ru/tensor/sbis/formatter/dateTime/DateTimeFormatter.kt).

## Руководство по подключению и инициализации
Для добавления модуля в проект, в `settings.gradle` проекта должны быть подключены следующие модули:

| Репозиторий                                            | модуль   |
|--------------------------------------------------------|----------|
| https://git.sbis.ru/mobileworkspace/android-design.git | design   |
| https://git.sbis.ru/mobileworkspace/android-utils.git  | common   |


## Руководство по использованию компонента
Для получения локализованного формата валюты нужно вызвать метод `getMoney` у `CurrencyFormatter`.
Метод вернет значение типа `CharSequence`, которое будет содержать текстовое представление значение в формате,
указанном при вызове метода `getMoney`.

Для получения локализованного формата даты нужно вызвать метод `getFormatter` у `DateTimeFormatter`.
Метод вернет форматтер даты с нужным шаблоном форматирования. Варианты форматирования указываются
при вызове метода `getFormatter`.

##### Пример конфигурации компонента
```kotlin
val textView = SbisTextView(context)
val currency = SbisFormatter.current.currencyFormatter.getMoney(context, 100, CurrencyTranslationMode.SEPARATED, CurrencyTranslationSize.FULL)
textView.text = currency
```

```kotlin
val textView = SbisTextView(context)
val formatter = SbisFormatter.current.dateFormatter.getFormatter(mode, res)
textView.text = formatter.format(Date())
```

### Примечания и особенности использования компонента
Для использования форматов даты `QUARTER`, `SHORT_QUARTER`, `FULL_QUARTER` нужна версия Android API не ниже 24.
При использовании данного формата на Android API ниже 24 будет возвращаться `null`.

