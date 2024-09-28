Для установки шаблона нужно:
	1) скопировать всю папку TensorModule и TensorCommonModule в ~\Android Studio\plugins\android\lib\templates\other\
	2) перезапустить Android Studio
	3) если нужно обновить шаблон, то проделать те же самые действия

Для того, чтобы воспользоваться шаблоном, нужно:
	1) создать папку для модуля в проекте
	2) скопировать туда все из папки ~\toCopy
	    2.1) добавить нужные зависимости в файле build.gradle
	    2.2) удалить package
	3) в папке создать директорию src\main\java\ru\tensor\sbis\app_name\module_name
	    (достаточно вставить эту строку, будут созданы все папки автоматически),
	    заменив module_name на название модуля и app_name на название приложения
	4) сделать из созданной папки модуль (можно через File -> New -> New Module -> Import Gradle Project и выбрать созданную папку). Все ошибки можно игнорировать.
	6) после того, как произойдет sync, щелкнуть правой кнопкой мыши по пакету (по папке вида ~\src\main\java\ru\tensor\sbis\app_name\module_name): New -> Other ->
	    6.1) Если нужен коммон модуль Tensor VIPER Base Common module
	    6.2) Если нужен обычный модуль Tensor VIPER module
	7) ввести необходимые названия
	8) перенести файл AndroidManifest.xml в папку main (просто на уровень выше)

	Если был выполнен пункт 6.1 {
	9) profit!
	} иначе {
	9) помимо папок модуля, будут сгенерированы еще и папки для переноса в common
    		9.1) из папки crud перенести все в /crud в common
    		9.2) из папки di перенести все в папку /di/repository в common
    		9.3) из папки model перенести все в папку model в common
    		9.4) из папки feature перенести все в папку feature в common
    	10) в build.gradle app добавить implementation project(': название модуля ')
    	    10.1) в settings.gradle добавить поле include(': название модуля ') над строчкой project(': название модуля ').projectDir = new File('feature/settings/ название модуля ')
    	    10.2) убрать в первой строчке имя модуля
    	11) в SettingsCommonDependency добавить FragmentProvider
    	    11.1) в FeatureDependencies добавить Dependency и переопределить геттер фрагмента
    	    11.2) в FeaturesDaggerApplication добавить ComponentHolder и реализовать поле Dependency, Component и fun buildComponent
    	12) в SettingsCommonComponent добавить Component и Module
    	13) ...
    	14) profit!
	}