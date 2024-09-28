# Модуль certificate_master_decl

| Модули реализации                                              |
|----------------------------------------------------------------|
| [Генерация и установка сертификата](crypto/certificate_master) |

## Описание модуля генерации и установки  
Используется как обертка над CryptoPRO что создания приватной части ключа и установки созданного УЦ сертификата в соотвествующий контейнер.
Реализация хранится в модуле certificate_master_impl.

## Подключение
Для добавления модуля в проект необходимо выполнить следующие шаги:

# 1. Подключите модуль certificate_master_decl в settings.gradle.

```
include ':certificate_master_decl'
project(':certificate_master_decl').projectDir = new File(declarationDir, 'certificate_master_decl')
```

# 2. Подключите модуль certificate_master_decl в build.gradle.

```
implementation project(':certificate_master_decl')
```

## Использование
Вызов мастера сертификатов может быть только для двух операций: генерация приватной части ключа и установка выпущенного сертификата в контейнер. Параметры описаны в интерфейсе ```MasterCertificateProvider```. Возвращается ```Intent```, который надо будет запустить как отдельную ```Activity```.
```kotlin
package ru.tensor.sbis.master.certificate.decl
getGenerateIntent(context: Context, requestUuid : UUID)
getInstallIntent(context: Context, requestUuid : UUID)
```