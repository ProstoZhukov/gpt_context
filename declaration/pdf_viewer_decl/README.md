# Модуль pdf_viewer_decl
| Ответственность | Ответственные                                                                    |
|-----------------|----------------------------------------------------------------------------------|
| Разработка | [Никитин Игнат](https://dev.saby.ru/person/2375f1dd-4469-470a-96ce-ff417a132e6e) |  

## Описание
Модуль содержит публичное API для просмотра ".pdf" файлов.

## Подключение
Для добавления модуля в проект необходимо подключить модуль pdf_viewer_decl в settings.gradle:

# 1. Подключите модуль pdf_viewer_decl в settings.gradle
```
include ':pdf_viewer_decl'
project(':pdf_viewer_decl').projectDir = new File(settingsDir, 'disk/pdf_viewer_decl')
```

# 2. Подключите модуль pdf_viewer_decl в build.gradle.

```
implementation project(':pdf_viewer_decl')
```


# Проверьте наличие в приложении модулей-зависимостей.

- Требуемые зависимости указаны в разделе `dependencies` файла [build.gradle]
- Проверьте наличие репозитория модуля в приложении, смотрите `.gitmodules`.
- Если репозиторий отсутствует, то добавьте его через регламент [Добавление репозитория исходного кода](https://online.sbis.ru/instructdoc/23efd388-8d37-4dc0-be99-5a88406a6260?viewMode=true).