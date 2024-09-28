# Модуль crypto_operation_decl

| Модуль                                                                 |
|------------------------------------------------------------------------|
| [Подписание мобильным сертификатом](declaration/crypto_operation_decl) |

| Модуль реализации                                            |
|--------------------------------------------------------------|
| [Подписание мобильным сертификатом](crypto/crypto_operation) |

## Описание
Предоставляет доступ к интенту на запуск процесса подписания документа с помощью сертификата, 
находящегося в CryptoPRO.

## Использование
Параметры описаны в интерфейсе ```DocumentSignProvider```. Возвращается ```Intent```, который надо 
будет запустить как отдельную ```Activity```. Причем можно запустить с результатом, в этом случае 
вернется либо ```Activity.RESULT_OK``` либо ```Activity.RESULT_CANCELED```, которые можно будет 
обработать в точке вызова.
```kotlin
package ru.tensor.sbis.document.sign.decl
getDocumentSignIntent(context: Context, isReject: Boolean, requestUuid: UUID, thumbprint: String?)
```