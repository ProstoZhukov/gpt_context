#### Разделитель с датой
|Ответственные|
|-------------|
[Петров Роман](https://online.sbis.ru/person/6246f9f1-03a4-4d02-aecf-29fcc9a0d0ff)| 

#### Использование в приложениях
- коммуникатор (см., к примеру, реестр сообщений)

##### Внешний вид
![List header](doc_resources/img/list_header.png)

[Стандарт внешнего вида](http://axure.tensor.ru/MobileStandart8/#p=%D1%80%D0%B0%D0%B7%D0%B4%D0%B5%D0%BB%D0%B8%D1%82%D0%B5%D0%BB%D0%B8_%D1%81_%D0%B4%D0%B0%D1%82%D0%BE%D0%B9&g=1)  

##### Описание
Разделитесь с датой — компонент, включающий в себя вью для отображения дат в ячейках списка и "приклеенный" заголовок (обычно вверху списка). Обеспечивает форматирование дат в ячейках и заголовке, взаимодействие списка и заголовка.

Сценарии использования:
* Дата и время последнего сообщения в реестре диалогов
* Дата и время сообщения в переписке
* Дата и время поступления задачи
* Дата загрузки файла в СБИС диск
* Дата и время публикации новости

Поддерживает несколько вариантов форматирования дат:

###### DateTime
Форматирует дату и время в соответствии с правилами:

| Дата             | Заголовок   |  Первая в дне ячейка  | Другие ячейки |
| ---------------- |-------------|-----------------------|
| Текущий день     |             | 23.04 13:43           | 13:43
| Текущий год      |  23.04      | 23.04 13:43           | 13:43
| Прошлый год      |  23.04.19   | 23.04.19 13:43        | 13:43

###### DateTimeWithToday
Форматирует дату и время в соответствии с правилами:

| Дата             | Заголовок   |  Первая в дне ячейка  | Другие ячейки |
| ---------------- |-------------|-----------------------|
| Текущий день     |  Сегодня    | 13:43                 | 13:43
| Текущий год      |  23.04      | 23.04                 | 23.04
| Прошлый год      |  23.04.19   | 23.04.19              | 23.04.19


###### DateTimeWithTodayStandard
Форматирует дату и время в соответствии с правилами:

| Дата             | Заголовок   |  Первая в дне ячейка  | Другие ячейки |
| ---------------- |-------------|-----------------------|
| Текущий день     | Сегодня     |  13:43                | 13:43
| Текущий год      | 23 апр      |  23 апр 13:43         | 13:43
| Прошлый год      | 23 апр 2019 |  23 апр 2019 13:43    | 13:43


###### DateWithMonth
Форматирует дату и время в соответствии с правилами:

| Дата             | Заголовок   |  Первая в дне ячейка  | Другие ячейки |
| ---------------- |-------------|-----------------------|
| Текущий день     |             |  13:43                | 13:43
| Текущий год      | 23.04       |  23.04                | 23.04
| Прошлый год      | 23.04.2019  |  23.04.2019           | 23.04.2019

###### DateTimeWithTodayCellsWithTime
Форматирует дату и время в соответствии с правилами:

| Дата             | Заголовок   |  Первая в дне ячейка  | Другие ячейки |
| ---------------- |-------------|-----------------------|
| Текущий день     | Сегодня     |  13:43                | 13:43
| Текущий год      | 23 апр      |  23.04 13:43          | 13:43
| Прошлый год      | 23.04.19    |  23.04.2019  13:43    | 13:43

###### DatesOnlyWithToday
Форматирует дату и время в соответствии с правилами:

| Дата             | Заголовок   |  Первая в дне ячейка  | Другие ячейки |
| ---------------- |-------------|-----------------------|
| Текущий день     | Сегодня     |  Сегодня              | 
| Текущий год      | 23.04       |  23.04                | 
| Прошлый год      | 23.04.19    |  23.04.19             | 

#### Использование - отображение заголовка
1. Реализовать `DateTimeAdapter` - метод `getItemDateTime`

     ```kotlin
    class SampleAdapter() : RecyclerView.Adapter<NewsViewHolder>(), DateTimeAdapter {
    
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewsViewHolder {
            ...
        }
    
        override fun onBindViewHolder(holder: NewsViewHolder, position: Int) {
            ...
        }
    
        override fun getItemCount(): Int = content.size
        
        /**
         * Возвращает дату для ячейки
         */
        override fun getItemDateTime(position: Int): LocalDateTime = content[position].date
    }
    ```

2. Добавить HeaderDateView в layout
    ```xml
    <?xml version="1.0" encoding="utf-8"?>
    <androidx.constraintlayout.widget.ConstraintLayout xmlns:android="http://schemas.android.com/apk/res/android"
        xmlns:app="http://schemas.android.com/apk/res-auto"
        xmlns:tools="http://schemas.android.com/tools"
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        tools:context=".listheader.ui.news.NewsFragment">
    
        <androidx.recyclerview.widget.RecyclerView
            android:id="@+id/recycler_view"
            android:layout_width="match_parent"
            android:layout_height="match_parent"/>
    
        <ru.tensor.sbis.design.list_header.HeaderDateView
            android:padding="8dp"
            android:id="@+id/header"
            app:layout_constraintTop_toTopOf="parent"
            app:layout_constraintRight_toRightOf="parent"
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"/>
    
    </androidx.constraintlayout.widget.ConstraintLayout>
    ```

3. Создать ListDateViewUpdater и забиндить его на RecyclerView:
    ```kotlin
    val listDateViewUpdater = ListDateViewUpdater(formatter) // один из вышеописанных форматтеров
    listDateViewUpdater.bind(rcyclerView, header)
    ```

#### Использование в ячейках списка
1. Добавление `ItemDateView` в разметку ячейки
    ```xml
    <?xml version="1.0" encoding="utf-8"?>
    <androidx.constraintlayout.widget.ConstraintLayout xmlns:android="http://schemas.android.com/apk/res/android"
                                                       xmlns:app="http://schemas.android.com/apk/res-auto"
                                                       xmlns:tools="http://schemas.android.com/tools"
                                                       android:layout_width="match_parent"
                                                       android:layout_height="72dp">
    
        <TextView
            android:id="@+id/body"
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:ellipsize="end"
            android:maxLines="1"
            android:paddingBottom="16dp"
            android:textAppearance="?textAppearanceCaption"
            android:textSize="12sp"
            app:layout_constraintTop_toTopOf="parent"
            app:layout_constraintStart_toStartOf="parent"
            tools:text="..."/>
    
        <ru.tensor.sbis.design.list_header.ItemDateView
            android:id="@+id/date"
            android:layout_width="wrap_content"
            android:layout_height="20dp"
            android:layout_margin="12dp"
            app:ItemDateView_dateViewMode="dateAndTime"
            app:layout_constraintEnd_toEndOf="parent"
            app:layout_constraintTop_toTopOf="parent"/>
    
    </androidx.constraintlayout.widget.ConstraintLayout>
    ```

2. Заполнение форматированными данными ячейки в адаптере:
    ```kotlin
        override fun onBindViewHolder(holder: NewsViewHolder, position: Int) {
            val news = content[position]
            holder.title.text = news.title
            holder.body.text = news.body
            holder.date.setFormattedDateTime(formattedDateProvider.getFormattedDate(position))
        }
    ```
Для установки форматированной даты и времени используется метод `setFormattedDateTime`. Метод getFormattedDate возвращает `FormattedDateTime` - пару дата + время по позиции. Необходиимость отображения месяца ListDateViewUpdater определяет сам по соседним ячейкам.

##### xml атрибуты ItemDateView и HeaderDateView
- `ItemDateView_dateViewMode` - Режим отображения даты и времени во view. Позволяет определить для view, какой именно аспект данных FormattedDateTime нужно отображать. К примеру, если разметка ячейки предполагает расположение даты и времени в разных местах, то их можно форматировать отдельно друг от друга.
см. [NewsAdapterWithSeparatedDateTime](https://git.sbis.ru/mobileworkspace/android-design/-/blob/rc-21.3100/demo/src/main/java/ru/tensor/sbis/appdesign/listheader/ui/news/NewsAdapterWithSeparatedDateTime.kt)

|                  |             |  
| ---------------- |-------------|
| dateAndTime   | По умолчанию - дата + время 
| dateOnly      | Отображение только даты
| timeOnly      | Отображение только времени

##### Стилизация
Атрибуты для определения темы компонента в составе темы приложения:

    listHeaderDateTheme - для заголовка
    listItemDateTheme - для ячейки

##### Трудозатраты внедрения
0.7 ч/д
