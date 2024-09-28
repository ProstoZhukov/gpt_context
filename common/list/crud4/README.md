# Списочный компонент для микросервиса на основе обозреваемой коллекции(CRUD 3)
| Ответственность | Ответственные |
|-----------------|---------------|
| Разработка | Быков Д. https://online.sbis.ru/person/1aee1e1d-892b-480e-8131-b6386b5b7bc0 |  
| Проектирование | Белоконь Д. https://online.sbis.ru/person/02f7bcd0-51ab-4c30-9505-a300360046b4 |  

## Документация
Страница стандарта http://axure.tensor.ru/MobileStandart8/#p=%D1%82%D0%B0%D0%B1%D0%BB%D0%B8%D1%87%D0%BD%D0%BE%D0%B5_%D0%BF%D1%80%D0%B5%D0%B4%D1%81%D1%82%D0%B0%D0%B2%D0%BB%D0%B5%D0%BD%D0%B8%D0%B5__%D0%B2%D0%B5%D1%80%D1%81%D0%B8%D1%8F_02_&g=1
ТЗ проекта https://online.sbis.ru/shared/disk/99074958-ed6c-4a15-a2f3-f730bf7a288f

## Описание
Компонент представляет собой готовое решения для отображения плоского списка данных, источником которого является микросервис контроллера, реализованный по парадигме CRUD 3. 
Компонент реализует логику работы с выборкой данных, отслеживания их изменения, отображения индикаторов подгрузки данных и заглушки.
Со стороны прикладного разработчика остается реализация минимальных обвязок для микросервиса и опциональное задание необходимых фильтров.
Визуальный компонент поддерживает стандартизированное поведение и отображения элементов списка, такие как фон, эффект нажатии, анимации добавления и удаления ячеек и прочие.

## Руководство по подключению и инициализации
Для использования необходимо добавит зависимости от модулей common/list/list и common/list/crud3
```gradle
implementation project(':list')
implementation project(':crud3')
```  
### Варианты использования компонента
Компонент состоит из двух частей: элемент интерфейса `ListComponentView` и модель представления, инкапсулирующая логику работы с контроллером. Эти две части слабо связанны друг 
с другом, поэтому модель представления может быть использована отдельно.  

#### Использование компонента целиком
В верстке необходимо добавить визуальный компонент ListComponentView
```
<androidx.constraintlayout.widget.ConstraintLayout
xmlns:android="http://schemas.android.com/apk/res/android"
xmlns:app="http://schemas.android.com/apk/res-auto"
xmlns:tools="http://schemas.android.com/tools"
style="@style/LoggingFragmentContainer"
android:layout_width="match_parent"
android:layout_height="match_parent"
tools:theme="@style/AppGlobalTheme">

    <ru.tensor.sbis.crud3.ListComponentView
        android:id="@+id/list_component"
        android:layout_width="match_parent"
        android:layout_height="match_parent"/>

</androidx.constraintlayout.widget.ConstraintLayout>
```
В методе onCreateView необходимо проинициализировать компонент, передав ему необходимые обвязки контроллера.
```
LogPackagesFragmentBinding
            .inflate(inflater)
            .also {
                it.listComponent.inject(
                    this,
                    lazy { Crud3LogServiceWrapper(LogCollectionProvider.instance()) },
                    lazy { Mapper() },
                    lazy { StubFactoryOneForAll(factory) }
                )
            }
            .root
```
#### Использование только модели представления списка
```
ru.tensor.sbis.crud3.createCollectionViewModel
```
#### Использование полной модели представления компонента
Позволяет выполнять маппингом элементов из данных микросервиса в элементы слоя представления и обрабатывать нажатие на элементы списка.
```
ru.tensor.sbis.crud3.createComponentViewModel
```
