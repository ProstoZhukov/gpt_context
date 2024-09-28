# Переписка чатов техподдержки.

Модуль содержит реализацию интерфейсной части экрана переписки для чатов техподдержки.

#### Описание
Модуль содержит реализацию интерфейсной части:
1) Реестр сообщений чата техподдержки.

## Дополнительная информация
- [ответственный Жуков Д.А](https://online.sbis.ru/person/6148dfb3-2e78-4328-89f3-6cff9625ceae)
- [ссылка на макет](http://axure.tensor.ru/mobile_crm/#g=1&p=служба_поддержки_клиента__провал_&c=1)

# Подключение.

Для добавления модуля переписки по чатам техпоодержки в проект необходимо выполнить шаги ниже:
## 1. Зависимости

В файле `settings.gradle` проекта должны быть подключены модули коммуникатора из
файла `$communicator_dir/consultation_settings.gradle` а так же все модули, от которых они зависят:
```
include ':communicator_support_channel_list'
project(':communicator_support_channel_list').projectDir = new File(settingsDir, "$communicator_dir/features/communicator_support_channel_list")

include ':communicator_support_consultation_list'
project(':communicator_support_consultation_list').projectDir = new File(settingsDir, "$communicator_dir/features/communicator_support_consultation_list")

include ':communicator_crm_conversation'
project(':communicator_crm_conversation').projectDir = new File(settingsDir, "$communicator_dir/features/communicator_crm_conversation")
```

## 2. Добавить зависимость в список плагинов приложения:
```
pluginManager.registerPlugins(
...
    CRMConversationPlugin
)
```

## 3. Использовать фичу:
Для доступа к функционалу модуля извне следует использовать интерфейсы, указанные в `CRMConversationFeature`

Для доступа к реализации (`CRMConversationFeatureFacade`) интерфейсов модуля следует определить их как зависимости в вызывающем модуле, а на уровне приложения иметь экземпляр такой реализации.

## Использование в приложениях
- [Коммуникатор](https://git.sbis.ru/mobileworkspace/apps/droid/communicator)