#### Компонент для отображения всех видов ячеек сообщений.

| Модуль       | Ответственные                                                                          |
|--------------|----------------------------------------------------------------------------------------|
|[message_view]| [Баранов Даниил](https://online.sbis.ru/person/9ec1d410-7a2c-40f0-bf00-7d1db5d1c30f)   |
|[message_view]| [Чекурда Владимир](https://online.sbis.ru/person/0fe3e077-6d50-431c-9353-f630fc789877) |

#### Описание
[MessageView] используется для отображения всех видов ячеек сообщений в любой переписке (диалоги, каналы, лента событий).
Компонент способен сам определить вид отображаемой ячейки по переданной [MessageViewData], которую можно получить
из модели контроллера [Message] при помощи [MessageViewDataMapper], который также содержится в данном модуле.

### API компонента - [MessageViewAPI]

## Дополнительная информация
- [ссылка на тех документацию](https://online.sbis.ru/shared/disk/26577907-852b-4c0a-92b2-c34f003a71ed)
- [ссылка на тех документацию](https://online.sbis.ru/shared/disk/df217e22-4927-4a1c-b74a-7a1c6d494b83)

# Подключение.

Для добавления модуля design_message_view в проект необходимо выполнить шаги ниже:

## 1. Зависимости
В файле `settings.gradle` проекта должен быть сам модуль, а так же все модули, от которых он зависит:

`include ':design_message_view'`
`project(':design_message_view').projectDir = new File(designDir, "design_message_view")`

...

## 2. Использование

Перед установкой [MessageViewData] в компонент нужно передать [MessageViewPool].
- Пример использования:
```xml
    <ru.tensor.sbis.design.message_view.ui.MessageView
        android:id="@+id/design_demo_message_view"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        app:messageViewPool="@{viewModel.messageViewPool}"
        app:viewData="@{viewModel.viewData}"
        app:updateSendingState="@{viewModel.sendingState}"/>
```
```kotlin
MessageView(context).apply {
    id = R.id.design_demo_message_view
    setMessageViewPool(messageViewPool)
    viewData = messageViewData
}
```

## 3. Описание особенностей работы
- Для MessageView не предусмотрена своя собственная тема, стили, для каждой ячейки используются свои собственные стили.
- Перед установкой [MessageViewData] в компонент нужно передать [MessageViewPool].
- Установка слушателей происходит через changeEventListeners - нужно передать класс события, на которое нужно действие
и непосредственно само действие:
```kotlin
messageView.changeEventListeners {
    set(MessageViewEvent.CRMEvent.OnGreetingClicked::class) {
        actionsListener.onGreetingClicked(it.title)
    }
}
```
- Для удаления конкретного слушателя передаем класс события:
```kotlin
messageView.changeEventListeners {
    remove(MessageViewEvent.CRMEvent.OnRateRequestButtonClicked::class)
}
```
- Удаление всех слушателей:
```kotlin
messageView.changeEventListeners {
    clear()
}
```

## 4. Как добавить новый тип ячейки.
- В MessageViewData добавляем новый тип в перечисление MessageType, создаем класс даты для новой ячейки, наследуемый от
CloudViewData, если используем CloudView для новой ячейки, иначе BaseMessageViewData, CoreMessageData реализуем через
передаваемый параметр messageData.
- Реализуем биндер, наследуемый от BaseMessageViewContentBinder для новой даты. Вносим новый биндер в список биндеров
в MessageViewController.
- В MessageViewDataMapper в методе map() реализуем маппинг для новой даты.
- Если нужны специфичные слушатели событий, то добавляем новые события в MessageViewEvent по аналогии с имеющимися.
Далее не забываем установить слушатели, как описано выше.

## Использование в приложениях
[Во всех Android приложениях, в которых присутствует переписка.](https://git.sbis.ru/mobileworkspace/apps/droid/...)