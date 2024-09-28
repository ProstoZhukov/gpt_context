# Модуль трекинга событий

| Ответственность | Ответственные                                                                        |  
|-----------------|--------------------------------------------------------------------------------------|
| Разработка      | [Мартышенко К.В](https://online.sbis.ru/person/7ae2600c-8e7c-4c7a-aafe-7ff6f2fd34ea) |

## Описание
Модуль позволяет записывать события аналитики о переходе между экранами и логировать события с информацией о текущем стеке экранов (реализация довольно наивная и не учитывает сценариев восстановления процесса).
**НЕ** рекомендуется использовать [EventsTracker](src/main/java/ru/tensor/sbis/events_tracker/EventsTracker.java) - используйте [StatisticService](../statistic/src/main/java/ru/tensor/sbis/statistic/StatisticService.kt).

## Руководство по подключению и инициализации
Модуль **НЕ** должен использоваться в прикладных модулях - только на уровне приложения.

1. Добавляем в сборочный скрипт `settings.gradle` модуль статистики.
```groovy
include ':events_tracker'
project(':events_tracker').projectDir = new File("path_to_file")
```
2. Синхронизируем.
3. Регистрируем в `app`-модуле в плагинной системе [EventsTrackerPlugin](src/main/java/ru/tensor/sbis/events_tracker/EventTrackerPlugin.kt).