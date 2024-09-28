# Модуль certificate_request_decl

## Описание
Модуль содержит интерфейс для работы с заявками на продление.
Реализация хранится в модуле certificate_request_impl.
Реализовано по [проекту](https://online.sbis.ru/opendoc.html?guid=cda55f07-4921-41a1-a986-31e2f36898bf)

## Использование в приложениях
- [Коммуникатор](https://git.sbis.ru/mobileworkspace/apps/droid/communicator)
- [Сбис на складе](https://git.sbis.ru/mobileworkspace/apps/droid/storekeeper)

## Внешний вид
[Макет](http://axure.tensor.ru/MobileAPP/настройки_и_профиль_электронные_подписи.html)

## Подключение
Для добавления модуля в проект необходимо выполнить следующие шаги:

# 1. Подключите модуль certificate_request_decl в settings.gradle.

```
include ':certificate_request_decl'
project(':certificate_request_decl').projectDir = new File(declarationDir, 'certificate_request_decl')
```

# 2. Подключите модуль certificate_request_decl в build.gradle.

```
implementation project(':certificate_request_decl')
```