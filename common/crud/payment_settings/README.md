# Утилитарный модуль

Модуль содержит набор классов-моделей данных для настроек продажи и оплаты.

- Ответственный: [Вершинин Д. С.](https://online.sbis.ru/person/32b47a94-feaa-4a37-b480-55053acb7528)

## Руководство по подключению и инициализации
1. При необходимости объявляем модуль в соответствующем `settings.gradle`:
```gradle
include ':crud_payment_settings'
project(':crud_payment_settings').projectDir = new File("<путь до модуля>/crud/payment_settings")
```
2. Добавляем в раздел зависимостей `build.gradle` модуля-потребителя:
```gradle
dependencies {
    implementation project(':crud_payment_settings')
    ...
}
```