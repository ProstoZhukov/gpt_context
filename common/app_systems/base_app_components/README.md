#### Base App Components

|Модуль|Ответственные|
|------|-------------|
|[base_app_components]|[Быков Дмитрий](https://online.sbis.ru/person/1aee1e1d-892b-480e-8131-b6386b5b7bc0)

#### Общие классы корневого модуля приложений
Модуль содержит базовые Android-компоненты головного модуля для разработки:
- BaseSbisApplication
- BaseMasterContainerFragment
- BaseLaunchActivity
Вспомогательные классы которые можно переиспользовать в app модуле:
- BaseSettingsHostFragment
- SettingsViewModel
- SettingsViewModelFactory
#### Проверка свободной памяти
Модуль содержит функционал, позволяющий проверить наличие свободной памяти перед запуском приложения.
Если памяти недоступно, отображается блокирующее работу сообщение.
Полноэкранную зашглуки для показа ошибки при старте приложения.
Функционал задействован всегда при использовании 
- ErrorScreenActivity
##### Темизация
Тема должна иметь атрибут BaseAppComponentsErrorScreen со ссылко наследуемой от темы BaseAppComponentsErrorScreenTheme.
Темизация не тестировалась, с ней могут быть проблемы, лучше известить ответственного о ее необходимости. 

#### Использование в приложениях
- [Communicator](https://git.sbis.ru/mobileworkspace/apps/droid/communicator)
- [DemoCommunicator](https://git.sbis.ru/mobileworkspace/apps/droid/demo-communicator)
- [SabyLite](https://git.sbis.ru/mobileworkspace/apps/droid/sabylite)
- [Business](https://git.sbis.ru/mobileworkspace/apps/droid/business)
- [Crm](https://git.sbis.ru/mobileworkspace/apps/droid/crm)
- [SabyMy](https://git.sbis.ru/mobileworkspace/apps/droid/mysaby)
- [Sabyadmin](https://git.sbis.ru/mobileworkspace/apps/droid/sabyadmin)
- [Sabydisk](https://git.sbis.ru/mobileworkspace/apps/droid/sabydisk)
- [SabyKnow](https://git.sbis.ru/mobileworkspace/apps/droid/sabyknow)

