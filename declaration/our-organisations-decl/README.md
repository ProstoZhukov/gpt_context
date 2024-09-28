# Модуль our-organisations-decl
| Ответственность | Ответственные                                                                            |
|-----------------|------------------------------------------------------------------------------------------|
| Разработка      | [Вершинин Дмитрий](https://online.sbis.ru/person/32b47a94-feaa-4a37-b480-55053acb7528)   |
| Разработка      | [Ильин Максим](https://online.sbis.ru/person/8bc4d2ab-bab3-4ba6-bc3c-180bfede9bec)       |

## Документация
[Техническая документация](https://online.sbis.ru/shared/disk/e886369d-a2b6-4ec6-84b4-178dbf8128d8)

## Описание
Модуль содержит компоненты выбора наших организаций из списка.
Реализация хранится в модуле our-organisations-impl.

## Руководство по подключению и инициализации
Для добавления модуля в проект необходимо что бы в settings.gradle были подключены следующие модули:

| Репозиторий                                             | модуль        |
|---------------------------------------------------------|---------------|
| <https://git.sbis.ru/mobileworkspace/android-utils.git> | plugin_struct |

Для добавления модуля в проект необходимо в settings.gradle указать:

```groovy
include ':our-organisations-decl'
project(':our-organisations-decl').projectDir = new File(settingsDir, 'declaration/our-organisations-decl/')
```

После чего подключить в файл build.gradle модуля, где планируется использовать our-organisations-decl,
следующим образом:
```groovy
implementation project(':our-organisations-decl')
```

## Использование в приложениях
- [СБИС.Доки](https://git.sbis.ru/mobileworkspace/apps/droid/storekeeper)
- [СБИС.Бизнес](https://git.sbis.ru/mobileworkspace/apps/droid/business)