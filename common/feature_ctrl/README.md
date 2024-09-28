# Модуль "Управление функционалом feature-ctrl"

Модуль содержит комплекс компонентов для работы с серверами удаленного управлениями фичами (feature-toggle)

## Дополнительная информация

- [ответственный Круглова Марина](https://online.sbis.ru/person/8a7248e7-b4b2-4c2e-a988-3534eab414f8)

## Использование в приложениях

## Подключение

Для добавления модуля в проект нужна зависимость LocalInterface. 

```kotlin
fun provideSbisFeatureService(dependency: Dependency): SbisFeatureServiceProvider {
    return SbisFeatureServiceProvider(dependency.getLoginInterface())
}
```

## Описание публичного API

- isActive(featureName: String) - метод, который возвращает статус включенности фичи по её идентификатору 
для пользователя и его клиента.

- getValue(featureName: String) - метод, который возвращает значение фичи по её идентификатору для пользователя и его клиента.

- getFeatureInfoObservable(featureList: List<String>) - optional, Observable для подписки на получение информации о 
фичах. Необходимо не забыть добавить к Disposable и вызвать dispose(), если подписка больше не нужна.

- getFeatureInfoFlow(featureList: List<String>) - optional, метод, аналогичный getFeatureInfoObservable, только 
возращающий Flow, в котором публикуются результаты загрузки информации о фичах.

Также описание интерфейса можно найти в файле [SbisFeatureService.kt](src/main/java/ru/tensor/sbis/feature_ctrl/SbisFeatureService.kt)

N.B. Методы getFeatureInfoObservable/getFeatureInfoFlow не являются обязательными и в них нет необходимости, если нужно 
получить информацию активна/неактивна фича или её значение, - для этого нужно использовать isActive(featureName: String) и 
getValue(featureName: String). getFeatureInfoObservable/getFeatureInfoFlow служат для получения более специфичной информации
о фиче.

### Описание SbisFeatureInfo
[SbisFeatureInfo](src/main/java/ru/tensor/sbis/feature_ctrl/SbisFeatureInfo.kt) можно получить в результате методов 
getFeatureInfoObservable или getFeatureInfoFlow. Он представляет из себя копию класса Specification из контроллера и 
содержит более специфичную и детальную информацию о фиче.

- feature - Идентификатор функционала.
- client - Идентификатор клиента.
- user - Идентификатор пользователя.
- lastUpdate - Время, когда данные были записаны в эту структуру, используется для инвалидации кэша.
- lastGenError - Время последней отправки сообщения об ошибке для функционала находящегося в архиве.
- data - Значение функционала.
- type - Тип действующей спецификации функционала.
- typeV2 - Текущая спецификация для второй версии формата ответа
- invalidation - Время инвалидации данных функционала, после истечения которого данные о функционале будут перечитаны из сервиса feature и актуализированы в кэше.
- state - Состояние функционала включен/выключен.
- needUpdate - Требуется ли обновить данные по спецификации из Облака.
- sendEvent - Нужно ли отправлять событие.