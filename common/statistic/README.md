# Модуль статистики

| Ответственность | Ответственные                                                                        |  
|-----------------|--------------------------------------------------------------------------------------|
| Разработка      | [Мартышенко К.В](https://online.sbis.ru/person/7ae2600c-8e7c-4c7a-aafe-7ff6f2fd34ea) |

## Документация
- Проект [Микросервис аналитики для МП](https://online.sbis.ru/opendoc.html?guid=a3672a4d-4788-4f01-aa1c-fd184f87ead5&client=3)
- [ТЗ](https://online.sbis.ru/shared/disk/945e39d0-36b4-4ce2-85f1-3467f30a4331)

## Описание
Модуль предоставляет доступ к [StatisticService](src/main/java/ru/tensor/sbis/statistic/StatisticService.kt), через который прикладные модули могут отправлять статистику.
Для корректной работы требуется при инициализации приложения установить реализацию [хранилища статистики](src/main/java/ru/tensor/sbis/statistic/StatisticStorage.kt).
Предусмотрено несколько вариантов работы.

### Отправка события
```kotlin
StatisticService.report(event)
```

### Трассировка
В некоторых случаях требуется замерить время события.
```kotlin
val trace = StatisticService.startTrace(event)
doWork()
trace.stop()
```
или упрощенная версия:
```kotlin
val workResult = StatisticService.trace(event) {
    doWork()
}
```

## Руководство по подключению и инициализации

### Уровень прикладного модуля
1. Добавляем зависимость от модуля статистики в сборочный скрипт `build.gradle`
```groovy
depencies {
    implementaion project(':statistic')
}
```
2. Синхронизиируем и пользуемся.

### Уровень приложения
1. Добавляем в сборочный скрипт `settings.gradle` модуль статистики.
```groovy
include ':statistic'
project(':statistic').projectDir = new File("path_to_file")
```
2. Синхронизируем.
3. Устанавливаем в `app`-модуле нужную реализацию хранилища статистики.