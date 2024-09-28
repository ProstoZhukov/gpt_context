#### Инструменты для упрощённой работы с компонентом папок

Класс [FoldersViewModel](src/main/java/ru/tensor/sbis/design/folders/support/FoldersViewModel.kt) берёт на себя большую часть рутинной работы:
* Отображает диалоги, обрабатывает их результаты и предоставляет слушатели;
* Отображает компонент заголовка при клике на папку, и предоставляет слушатели открытия\закрытия папки;
* Обрабатывает клик на кнопку "ещё" и отображает выезжающую панель со всеми папками;
* Позволяет отобразить выезжающую панель со всеми папками, например, для перемещения объекта в выбранную папку;
* Сохраняет состояние при смене конфигурации.

##### Инструкция

В лайауте фрагмента или активити должны быть:
1. FoldersView
2. MovablePanel c заданным атрибутом MovablePanel_contentContainerId

```xml
<FrameLayout
    android:layout_width="match_parent"
    android:layout_height="match_parent">

    <ru.tensor.sbis.design.folders.FoldersView
        android:id="@+id/folders"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        tools:layout_height="50dp"/>

    <ru.tensor.sbis.design_dialogs.movablepanel.MovablePanel
        android:id="@+id/more_folders_panel"
        android:layout_gravity="bottom"
        android:focusable="true"
        app:MovablePanel_contentContainerId="@+id/more_folders_container"
        app:MovablePanel_shadowBackgroundEnabled="false"
        tools:visibility="gone"/>
</FrameLayout>
```

Далее во фрагменте или активити нужно инициализировать вьюмодель и в метод attach() передать все параметры. Для Activity в методе onCreate(), а для фрагмента - в onViewCreated().
В конструктор вьюмодели нужно передать ResourceProvider и реализацию интерфейса FoldersProvider.

```kotlin
class FoldersViewModelActivity : AppCompatActivity() {

    private val viewModel: FoldersViewModel by viewModels {
        object : ViewModelProvider.Factory {
            override fun <VM : ViewModel> create(modelClass: Class<VM>): VM =
                FoldersViewModel(
                    ResourceProvider(this@FoldersViewModelActivity),
                    DemoFoldersProvider(),
                ) as VM
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding = ActivityFoldersViewModelBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        val actionsListener = object : FolderActionListener {
        	override fun open(id: String) = showToast("Folder $id open")
        	override fun closed() = showToast("Folder closed")
        	override fun selected(id: String) = showToast("Folder $id selected")
        	override fun additionalCommandClicked() = showToast("Additional command clicked")
        }
        
        viewModel.attach(
            foldersView = binding.folders,
            fragmentManager = supportFragmentManager,
            lifecycleOwner = this,
            allFoldersPanel = binding.moreFoldersPanel,
            allFoldersPanelContainerId = R.id.more_folders_container,
            actionsListener = actionsListener,
        )
        
        binding.moveToFolderButton.setOnClickListener {
        	viewModel.showFolderSelection(supportFragmentManager, binding.moreFoldersPanel, actionsListener)
        }
    }
}
```

Для прослушивания изменения данных нужно передать параметр `dataUpdateListener` в метод `attach()`
