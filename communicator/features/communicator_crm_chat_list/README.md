#### Реестр чатов CRM
| Ответственность | Ответственные                                                                        |
|-----------------|:-------------------------------------------------------------------------------------|
| Разработка      | [Жуков Дмитрий](https://online.sbis.ru/person/6148dfb3-2e78-4328-89f3-6cff9625ceae)  |
| Разработка      | [Баранов Даниил](https://online.sbis.ru/person/9ec1d410-7a2c-40f0-bf00-7d1db5d1c30f) |

## Документация
- ТЗ по проекту Saby Crm на Android, смотреть раздел *Разработка и подключение модуля "Чаты"*
  (https://online.sbis.ru/shared/disk/60731874-fb07-4e81-ab1c-cb4a95a1d038)
- ТЗ Легкие чаты (мобилка) (https://online.sbis.ru/shared/disk/d2b1bb3c-12f1-4c98-bd1c-34d454075794)
- Мобильный CRM/Чаты- основные БП (https://online.sbis.ru/shared/disk/72e171eb-36ac-432c-9bec-a1f7b8c5d475)

## Описание
Реестр чатов CRM - список, поддерживающий поиск, папки и фильтры,
который будет отвечать за отображение консультаций оператора и за чаты клиентов с заведениями saby get.

## Руководство по подключению и инициализации

Для добавления модуля в проект необходимо:
- В settings.gradle проекта должны быть подключены модули,
  указанные в файле (communicator/features/communicator_crm_chat_list/build.gradle)
- В settings.gradle указать:
```groovy
include ':communicator_crm_chat_list'
project(':communicator_crm_chat_list').projectDir = new File(communicatorDir, "features/communicator_crm_chat_list")
```
- После чего подключить в файл build.gradle модуля, где планируется использовать данный модуль, следующим образом:
```groovy
implementation project(':communicator_crm_chat_list')
```
- В PluginSystem проекта добавить CRMChatListPlugin, чтобы модуль мог получить необходимые зависимости
  CRMChatListDependency.

## Описание публичного API
CRMChatListFeatureFacade:
- [CRMChatListFragmentFactory] - создание фрагмента списка чатов CRM.
- [CRMChatListHostFragmentFactory] - создание хост фрагмента списка чатов CRM.
- [CRMHostRouter.Provider] - провайдер роутера списка чатов CRM.
- [CrmChannelListFragmentFactory] - создание экрана с выбором каналов crm для сценариев фильтрации, переназанчения чата.

## Использование в приложениях
[Используется в Saby CRM](https://git.sbis.ru/mobileworkspace/apps/droid/crm)
[Используется в Saby Brand](https://git.sbis.ru/mobileworkspace/apps/droid/sabybrand)
[Используется в Sabyclients](https://git.sbis.ru/mobileworkspace/apps/droid/sabyclients)
[Используется в Saby Hostess](https://git.sbis.ru/mobileworkspace/apps/droid/sabyhostesspresto)