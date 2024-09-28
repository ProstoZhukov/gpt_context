# Обёртка над микросервисом декорирования ссылок
| Ответственность | Ответственные |
|-----------------|---------------|
| Участок работ | [Компонент для открытия ссылок Android (LinkOpener)](https://online.sbis.ru/area/997231a7-2092-40fa-9fee-9a10a73c1534) |

## Описание
Модуль предназначен для осуществления централизованного взаимодействия с микросервисом декорирования ссылок (`LinkDecoratorService`). Использование микросервиса напрямую и преобразование моделей контроллера в прикладном коде не допускается.

## Руководство по подключению и инициализации

Для добавления модуля в проект, в `settings.gradle` проекта должны быть подключены следующие модули:

| Репозиторий | модуль |
|-----------------|---------------|
|https://git.sbis.ru/mobileworkspace/android-serviceAPI.git |toolbox-decl|
|https://git.sbis.ru/mobileworkspace/android-design.git |design_utils|

## Описание публичного API
Для использования модуля, необходимо зарегистрировать в приложении плагин [DecoratedLinkPlugin](src/main/java/ru/tensor/sbis/decorated_link/DecoratedLinkPlugin.kt).
Плагин модуля предоставляет реализацию [DecoratedLinkFeature](https://git.sbis.ru/mobileworkspace/android-serviceapi/-/blob/rc-22.7262/toolbox-decl/src/main/java/ru/tensor/sbis/toolbox_decl/linkopener/service/DecoratedLinkFeature.kt), которая позволяет получить реализацию [LinkDecoratorServiceRepository](https://git.sbis.ru/mobileworkspace/android-serviceapi/-/blob/rc-22.7262/toolbox-decl/src/main/java/ru/tensor/sbis/toolbox_decl/linkopener/service/LinkDecoratorServiceRepository.kt), предназначенную для взаимодействия с микросервисом декорирования ссылок. 
