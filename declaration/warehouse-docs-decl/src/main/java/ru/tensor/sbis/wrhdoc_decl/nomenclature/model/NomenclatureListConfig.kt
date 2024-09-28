package ru.tensor.sbis.wrhdoc_decl.nomenclature.model

import ru.tensor.sbis.wrhdoc_decl.nomenclature.NomenclatureListComponent

/**
 * Настройки отображения компонента "Список наименований" [NomenclatureListComponent]
 *
 * @property needSearch поиск в списке наименований
 * @property needScanByCamera сканирование камерой
 * @property acceptanceModeByDefault включить режим приемки по умолчанию
 * @property useCustomStub если данных нет, то компонент вернет пустой список,
 * без добавления своей заглушки
 *
 * @author as.mozgolin
 */
data class NomenclatureListConfig(
    val needSearch: Boolean = true,
    val needScanByCamera: Boolean = true,
    val acceptanceModeByDefault: Boolean = false,
    val useCustomStub: Boolean = false
)
