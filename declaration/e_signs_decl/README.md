# Модуль e_signs_decl
| Ответственность | Ответственные                                                                        |
|-----------------|--------------------------------------------------------------------------------------|
| Разработка      | [Малыхин Владислав](https://dev.saby.ru/person/929170f3-0f13-460e-8f28-b8299515f062) |

| Модуль реализации                             |
|-----------------------------------------------|
| [Модуль электронных подписей](crypto/e_signs) |

## Использование в приложениях
- [Коммуникатор](https://git.sbis.ru/mobileworkspace/apps/droid/communicator)
- [SabyLite](https://git.sbis.ru/mobileworkspace/apps/droid/sabylite)
- [SabyDisk](https://git.sbis.ru/mobileworkspace/apps/droid/sabydisk)
- [Storekeeper](https://git.sbis.ru/mobileworkspace/apps/droid/storekeeper)
- [Business](https://git.sbis.ru/mobileworkspace/apps/droid/business)
- [Courier](https://git.sbis.ru/mobileworkspace/apps/droid/courier)
- [MySaby](https://git.sbis.ru/mobileworkspace/apps/droid/mysaby)
- [Sabyknow](https://git.sbis.ru/mobileworkspace/apps/droid/sabyknow)
- [Sabybrand](https://git.sbis.ru/mobileworkspace/apps/droid/sabybrand)
- [Sabyclients](https://git.sbis.ru/mobileworkspace/apps/droid/sabyclients)
- [Sabyhostess](https://git.sbis.ru/mobileworkspace/apps/droid/sabyhostess)
- [Sabyprofile](https://git.sbis.ru/mobileworkspace/apps/droid/sabyprofile)
- [CRM](https://git.sbis.ru/mobileworkspace/apps/droid/crm)

## Внешний вид
[Макет](http://axure.tensor.ru/crypto-uc-8/электронные_подписи_в_мп_версия3.html)

## Техническая документация
[ТД "Лица с правом подписи"](https://online.sbis.ru/shared/disk/f0cac49a-b036-4148-8cc6-9ee478ab5a93)
[ТД "Вывод информации о заявках на ЭП в мобильных приложениях СБИС"](https://online.saby.ru/shared/disk/4957bf87-582c-40a9-a938-b6b365013882)

## Описание
Модуль описывает публичное API для запуска экрана электронной подписи.
Реализация модуля находится в модуле [e_signs].
Состоит из:
 - Интерфейс для запуска экрана деталей сертификата (CertificateDetailsIntentFactory) — deprecated.
 - Интерфейс для запуска экрана с заявкой на НЭП (CertificateWebSubmissionIntentFactory) — deprecated.
 - Интерфейс для запуска карточки сертификата/заявки (CertificateEntityCardIntentFactory).

### API для получения интента для запуска экрана деталей сертификата:
`CertificateEntityCardIntentFactory`
``` kotlin
    fun newIntent(
        context: Context,
        config: CertificateEntityCardConfig
    ): Intent
```

# Подключение

Для подключения модуля необходимо выполнить следующие шаги:

## Подключение

# 1. Подключите модуль e_signs_decl (declaration/app_gradle_module/submodule)
```
include ':e_signs_decl'
project(':e_signs_decl').projectDir = new File(declarationDir, 'e_signs_decl')
```

# 2. Подключите модуль e_signs_decl в build.gradle.

```
implementation project(':e_signs_decl')
```

## 3. Проверьте наличие в приложении модулей-зависимостей.

- Требуемые зависимости указаны в разделе `dependencies` файла `build.gradle`.
- Проверьте наличие репозитория модуля в приложении, смотрите `.gitmodules`.
- Если репозиторий отсутствует, то добавьте его через регламент [Добавление репозитория исходного кода](https://online.sbis.ru/instructdoc/23efd388-8d37-4dc0-be99-5a88406a6260?viewMode=true).