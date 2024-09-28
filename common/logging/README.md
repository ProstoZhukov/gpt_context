# Logging(Логирование)
| Ответственность | Ответственные |
|-----------------|---------------|
| Разработка | [Быков Дмитрий](https://online.sbis.ru/person/1aee1e1d-892b-480e-8131-b6386b5b7bc0) |
| Проектирование | [Белоконь Дарья](https://online.sbis.ru/person/02f7bcd0-51ab-4c30-9505-a300360046b4) |

## Документация
[ТЗ](https://online.sbis.ru/shared/disk/f508bf9d-bf7f-4652-8c0d-f9fcab28dab9)
[Макет](http://axure.tensor.ru/platform_8/#g=1&p=%D0%BB%D0%BE%D0%B3%D0%B8%D1%80%D0%BE%D0%B2%D0%B0%D0%BD%D0%B8%D0%B5_%D0%B2_%D0%BC%D0%BF)

## Описание
Модуль используется для отправки логов с устройства на облако. Все Timber логи перенаправляются в
контроллер автоматически. Все этапы жизненного цикла фрагментов и активити автоматически логируются.
Для логирования дополнительной информации можно имплементировать ScreenMarker для фрагмента или
активити.

## Руководство по подключению и инициализации
Зависимости модуля:

| Репозиторий | модуль |
|-----------------|---------------|
|<https://git.sbis.ru/mobileworkspace/android-utils>|app_file_browser|
|<https://git.sbis.ru/mobileworkspace/android-utils>|base_components|
|<https://git.sbis.ru/mobileworkspace/android-utils>|common|
|<https://git.sbis.ru/mobileworkspace/android-utils>|common_filters|
|<https://git.sbis.ru/mobileworkspace/android-utils>|controller_utils|
|<https://git.sbis.ru/mobileworkspace/android-utils>|crud3|
|<https://git.sbis.ru/mobileworkspace/android-design>|design|
|<https://git.sbis.ru/mobileworkspace/android-design>|design_buttons|
|<https://git.sbis.ru/mobileworkspace/android-design>|design_checkbox|
|<https://git.sbis.ru/mobileworkspace/android-design>|design_dialogs|
|<https://git.sbis.ru/mobileworkspace/android-design>|design_notification|
|<https://git.sbis.ru/mobileworkspace/android-design>|design_stubview|
|<https://git.sbis.ru/mobileworkspace/android-design>|design_utils|
|<https://git.sbis.ru/mobileworkspace/android-design>|design_view_ext|
|<https://git.sbis.ru/mobileworkspace/android-utils>|list|
|<https://git.sbis.ru/mobileworkspace/android-utils>|list_utils|
|<https://git.sbis.ru/mobileworkspace/android-utils>|modalwindows|
|<https://git.sbis.ru/mobileworkspace/android-utils>|mvp|
|<https://git.sbis.ru/mobileworkspace/android-utils>|mvvm|
|<https://git.sbis.ru/mobileworkspace/android-utils>|swipeablelayout|
|<https://git.sbis.ru/mobileworkspace/android-design>|toolbar|

После подключения всех зависимостей необходимо подключить `LoggingPlugin` к вашему приложению.
Если ваше приложение уже использует `BaseSabyApp`, то ничего подключать не нужно.

## Описание публичного API
Модуль рекомендуется использоваться в связке с модулем `settings_screen`, который поставляет нужный
пункт настроек из коробки! Если вы не используете `settings_screen`, но вам нужно использовать
логирование, то стоит задуматься: возможно вы делаете что-то не так.

Получение фрагментов модуля происходит через `LoggingFragmentProvider`, который располагается
в модуле `toolbox_decl`. Его можно подключить в ваш модуль через плагинную систему.

У плагина есть 2 кастомные опции:
- enableForceLogDeliveryLauncher — отвечает за доступность лаунчера для принудительной
отправки логов
- enableMemoryLogWhileScreenTracking — отвечает за логирование свободного места на диске 
при переходах между экранами