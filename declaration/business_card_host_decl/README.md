# business-card-host-decl

Модуль содержит публичное API функционала компонента хоста визиток, который предоставляет знание, 
нужно ли отображать блок с визитками в профиле и какой экран открыть при клике (одна визитка или реестр)
- [ответственный Московчук А.Д.](https://online.sbis.ru/person/f6fa6997-bb39-4e71-ba27-e998125c9e74)
- [ссылка на проект Электронная визитка сотрудника](https://project.sbis.ru/uuid/43ba4bf6-9bc0-42b3-9038-da032ec28e31/page/project-main)
- [ссылка на макет](http://axure.tensor.ru/project-time8/%D0%BF%D1%80%D0%BE%D1%84%D0%B8%D0%BB%D1%8C_%D0%B2%D0%B8%D0%B7%D0%B8%D1%82%D0%BA%D0%B0.html)

#### Подключение

Для добавления модуля в проект в declaration_settings.gradle проекта должны быть подключены
следующие модули:
`include ':business_card_host_decl'`
`project(':business_card_host_decl').projectDir = new File(declarationDir, 'business_card_host_decl')`

Далее в модуле, который требует компоненты бизнеса добавить.
`implementation project(':business_card_host_decl')`