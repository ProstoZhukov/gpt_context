# Модуль our-organisations-impl
| Ответственность | Ответственные                                                                            |
|-----------------|------------------------------------------------------------------------------------------|
| Разработка      | [Вершинин Дмитрий](https://online.sbis.ru/person/32b47a94-feaa-4a37-b480-55053acb7528)   |  
| Разработка      | [Ильин Максим](https://online.sbis.ru/person/8bc4d2ab-bab3-4ba6-bc3c-180bfede9bec)       |

## Документацияhttps://online.sbis.ru/shared/disk/e886369d-a2b6-4ec6-84b4-178dbf8128d8
[Техническая документация]()

## Описание
Предоставляет компоненты выбора наших организаций из списка.

## Руководство по подключению и инициализации
Для добавления модуля в проект необходимо в settings.gradle проекта должны быть подключены следующие
модули:

| Репозиторий                                                      | модуль                 |
|------------------------------------------------------------------|------------------------|
| <https://git.sbis.ru/mobileworkspace/android-serviceAPI.git>     | our-organisations-decl |
| <https://git.sbis.ru/mobileworkspace/apps/droid/storekeeper.git> | controller             |
| <https://git.sbis.ru/mobileworkspace/android-utils.git>          | controller_utils       |
| <https://git.sbis.ru/mobileworkspace/android-design.git>         | design                 |
| <https://git.sbis.ru/mobileworkspace/android-utils.git>          | common                 |
| <https://git.sbis.ru/mobileworkspace/android-catalog.git>        | commonstorekeeper      |
| <https://git.sbis.ru/mobileworkspace/android-utils.git>          | mvp                    |
| <https://git.sbis.ru/mobileworkspace/android-utils.git>          | common_filters         |
| <https://git.sbis.ru/mobileworkspace/android-utils.git>          | base_components        |
| <https://git.sbis.ru/mobileworkspace/android-design.git>         | design_dialogs         |
| <https://git.sbis.ru/mobileworkspace/android-design.git>         | list_utils             |
| <https://git.sbis.ru/mobileworkspace/android-design.git>         | design_stubview        |
| <https://git.sbis.ru/mobileworkspace/android-design.git>         | text_span              |

Для добавления модуля в проект необходимо в settings.gradle указать:

```groovy
include ':our-organisations-impl'
project(':our-organisations-impl').projectDir = new File(settingsDir, 'common/our-organisations-impl/')
```

Далее подключить плагин модуля наших организаций в плагинную систему приложения:
```kotlin
object PluginSystem : BaseSabyApp() {
    
    override fun registerPlugins(app: Application, pluginManager: PluginManager) {
        OurOrgPlugin
    }
}
```
## Описание публичного API

Модуль поставляет следующие реализации в публичный доступ:
- [OurOrgFeature](https://git.sbis.ru/mobileworkspace/android-serviceapi/-/tree/rc-23.1200//our-organisations-decl/src/main/java/ru/tensor/sbis/our_organisations/feature/di/OurOrgFeature.kt)
фича для доступа к компонентам выбора наших организаций из различный списков.

## Использование в приложениях
- [СБИС.Доки](https://git.sbis.ru/mobileworkspace/apps/droid/storekeeper)