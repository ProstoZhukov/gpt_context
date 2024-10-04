# Оценки в мобильном приложении 
| Ответственность | Ответственные |
|-----------------|---------------|
| Разработка | [Колпаков михаил](https://online.sbis.ru/person/6b7e7802-6118-4fe4-9ec3-1db87bc0853c) |  

## Использование в приложениях
- [Коммуникатор](https://git.sbis.ru/mobileworkspace/apps/droid/communicator)
- [СБИС Бизнес](https://git.sbis.ru/mobileworkspace/apps/droid/business)
- [СБИС СМС](https://git.sbis.ru/mobileworkspace/apps/droid/sms)
- [СБИС Мобильный официант](https://git.sbis.ru/mobileworkspace/apps/droid/waiter2)

## Документация
[API компонента](https://n.sbis.ru/shared/disk/e903f2a9-9aac-4d86-b54b-fcf81faac849)

## Описание

Компонент предназначен для запроса оценки внутри приложения при наступлении позитивного сценария.
Прикладные модули генерируют события которые отслеживает компонент, на уровне приложения описывается набор позитивных сценариев. В случае выполнения сценария пользователю будет предложено оставить оценку в магазине приложений.

## Руководство по подключению и инициализации
Компонент подключается через плагинную систему с помощью класса ```ReviewPlugin``` Плагин имеет 2 опции 

- customizationOptions.trigger - набор позитивных сценариев, по которым будет предложено оценить приложение 
- customizationOptions.reviewType - в каком виде запросить оценку. Например, для дебаг версии можно показывать фейковый диалог (```ReviewType.DEMO```). 
```kotlin
 ReviewPlugin.apply {
                customizationOptions.trigger = createTrigger()

                if (BuildConfig.DEBUG) {
                    customizationOptions.reviewType = ReviewType.DEMO
                }
            }
```  
Подключение в gradle
```gradle
    implementation project(':design_review')
```

## Руководство по использованию

На уровне приложения необходимо описать набор правил. Делается это с помощью триггеров, это может быть как один триггер, если в приложении только одно правило, так и их набор.

Набор правил можно задать с помощью составных триггеров(```OrTrigger```/```AndTrigger```) которые принимают другие триггеры в качестве параметра. 

Описание конкретных триггеров можно посмотреть в [документации API](https://n.sbis.ru/shared/disk/e903f2a9-9aac-4d86-b54b-fcf81faac849)
```kotlin
private fun createTrigger(): Trigger {
    return OrTrigger(
        CountTrigger(WrhDocReviewEvent.ON_NAVIGATION_DOCUMENT_TYPE, 20),
        AndTrigger(
            CountTrigger(WrhDocReviewEvent.ON_SUCCESS_CREATE_DOCUMENT, 10),
            CountTrigger(WrhDocReviewEvent.ON_SUCCESS_POST_DOCUMENT, 10)
        ),
        CountTrigger(CatalogCardReviewEvent.ON_SUCCESS_SHOW_CATALOG_CARD, 20)
    )
}

```

На уровне прикладного модуля необходимо объявить перечисление (наследовавшись от маркерного интерфейса ```ReviewEvent```) в котором описать все события предоставляемые модулем.
```kotlin
enum class MyEvents : ReviewEvent {
    EVENT_1,
    EVENT_2
}
```

Затем в момент срабатывания события из прикладного кода вызвать метод ```reviewFeature.onEvent()```, для регистрации события в системе.
```kotlin
        reviewFeature.onEvent(MyEvents.EVENT_1)
  ```

##### Трудозатраты внедрения
1 ч/д