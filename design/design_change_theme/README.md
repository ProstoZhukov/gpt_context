#### Смена темы приложения

| Класс                                                                                      | Ответственные                                                                          |
|--------------------------------------------------------------------------------------------|----------------------------------------------------------------------------------------|
| [ChangeThemePlugin](src/main/java/ru/tensor/sbis/design/change_theme/ChangeThemePlugin.kt) | [Золотарев Даниил](https://online.sbis.ru/person/fb135a4c-c712-4f6e-b52a-6a36dfbdff5e) |

#### Использование в приложениях
- [Коммуникатор](https://git.sbis.ru/mobileworkspace/apps/droid/communicator)

##### Описание
Модуль для реализации смены темы в приложении.

##### Подключение
Для того чтобы использовать данный функционал для смены темы приложения, необходимо:
- Подключить [ChangeThemePlugin](src/main/java/ru/tensor/sbis/design/change_theme/ChangeThemePlugin.kt).
- Реализовать [ThemesProvider](src/main/java/ru/tensor/sbis/design/change_theme/contract/ThemesProvider.kt), передав туда список доступных тем.
- Подписаться на [ChangeThemeCallback](src/main/java/ru/tensor/sbis/design/change_theme/util/ChangeThemeCallback.kt) в Application.
- Если в приложении предполагается использование темизации на основе системной дневной/ночной темы, то в конструктор [ThemesProviderImpl](src/main/java/ru/tensor/sbis/design/change_theme/contract/ThemesProviderImpl.kt) следует передать объект [SystemThemes](src/main/java/ru/tensor/sbis/design/change_theme/util/SystemThemes.kt) с темами которые будут использованы для системной дневной и ночной темы. В противном случае функциональность работать не будет.

