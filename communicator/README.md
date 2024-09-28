# Communicator

Модуль сервиса "Коммуникатор"

## Дополнительная информация

- [ответственный Крохалев Р.В](https://online.sbis.ru/person/420fa93c-fd36-4081-b974-038c28749265)
- [ссылка на тех документацию](https://online.sbis.ru/shared/disk/26577907-852b-4c0a-92b2-c34f003a71ed)
- [ссылка на тех документацию](https://online.sbis.ru/shared/disk/df217e22-4927-4a1c-b74a-7a1c6d494b83)
- [публичное API](https://online.sbis.ru/shared/disk/9668b93e-44dd-4fff-a9eb-d01c4eb5b4a9)

## Подключение

Функционал сервиса "Коммуникатор" существует в двух вариантах, в зависимости от того какой вариант 
нужен будут подключаться разные модули.  

#### Полный функционал диалогов и чатов, который используется в приложениях Коммуникатор и Курьер

В файле `settings.gradle` проекта должны быть подключены модули коммуникатора из 
файла `$communicator_dir/settings.gradle` а так же все модули, от которых они зависят:

``` groovy
ext.communicator_dir = 'communicator'
apply from: "$communicator_dir/settings.gradle"
```

#### Функционал чатов, который используется в приложении Сабигет. 

В файле `settings.gradle` проекта должны быть подключены модули коммуникатора из 
файла `$communicator_dir/sabyget_settings.gradle` а так же все модули, от которых они зависят:

``` groovy
ext.communicator_dir = 'module/communicator'
apply from: "$communicator_dir/sabyget_settings.gradle"
```

## Использование в приложениях

- [Коммуникатор](https://git.sbis.ru/mobileworkspace/apps/droid/communicator)
- [Курьер](https://git.sbis.ru/mobileworkspace/apps/droid/courier)
- [Мобильная витрина SabyGet](https://git.sbis.ru/mobileworkspace/apps/droid/showcase)
- [SabyLite](https://git.sbis.ru/mobileworkspace/apps/droid/sabylite)
- [Brand](https://git.sbis.ru/mobileworkspace/apps/droid/brand)
- [MySaby](https://git.sbis.ru/mobileworkspace/apps/droid/mysaby)