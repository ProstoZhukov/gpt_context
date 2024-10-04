#### Скролл в самый верх

|Класс|Ответственные|Добавить|
|-----|-------------|--------|
|[ScrollToTop](src/main/java/ru/tensor/sbis/design/scroll_to_top/ScrollToTop.java)|[Бубенщиков С.В.](https://online.sbis.ru/person/1fb93b8c-350f-4785-8589-b0ff2edfbfa7)|

[Стандарт внешнего вида](http://axure.tensor.ru/MobileStandart8/#p=скролл_в_самый_верх)  

#### Использование в приложениях
- [Коммуникатор](https://git.sbis.ru/mobileworkspace/apps/droid/communicator)
- [Курьер](https://git.sbis.ru/mobileworkspace/apps/droid/courier)
- [Сбис на складе](https://git.sbis.ru/mobileworkspace/apps/droid/storekeeper)
- [Мобильный официант](https://git.sbis.ru/mobileworkspace/apps/droid/waiter2)
- [Бизнес](https://git.sbis.ru/mobileworkspace/apps/droid/business)

##### Описание
Скролл в самый верх используется для быстрого перехода к началу скролируемой страницы.

##### Стилизация
Стиль компонента задаётся атрибутом `ScrollToTop_style`. По умолчанию используется стиль [ScrollToTopStyle](src/main/res/values/styles_scroll_to_top.xml).

###### Переопределение темы
При необходимости можно оформить собственную тему, задав требуемые значения следующих атрибутов:

|Атрибут|Описание|
|-------|--------|
|android:background|Цвет фона|  
|titleTextColor|Цвет текста заголовка|  
|strokeColor|Цвет нижней линии|  
|arrowColor|Цвет стрелки|  
