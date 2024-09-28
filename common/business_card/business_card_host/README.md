# Экран хоста визиткок

Модуль для экрана хоста визиток
- [ответственный Московчук А.Д.](https://online.sbis.ru/person/f6fa6997-bb39-4e71-ba27-e998125c9e74)
- [ссылка на проект Электронная визитка сотрудника](https://project.sbis.ru/uuid/43ba4bf6-9bc0-42b3-9038-da032ec28e31/page/project-main)
- [ссылка на проект макет](http://axure.tensor.ru/project-time8/%D0%BF%D1%80%D0%BE%D1%84%D0%B8%D0%BB%D1%8C_%D0%B2%D0%B8%D0%B7%D0%B8%D1%82%D0%BA%D0%B0.html)

#### Подключение

Для добавления модуля в проект в common_settings.gradle проекта должны быть подключены следующие
модули:
`include ':business_card_host'`
`project(':business_card_host').projectDir = new File(commonDir, 'business_card/business_card_host')`

Далее в модуле, который требует компоненты бизнеса добавить.
`implementation project(':business_card_host')`