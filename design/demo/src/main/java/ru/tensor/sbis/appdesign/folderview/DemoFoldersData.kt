package ru.tensor.sbis.appdesign.folderview

private var folderId = 0L
private const val MAX_RANDOM_FOLDER_LEN = 60

/**
 * Модель папки.
 */
internal data class Folder(
    val id: String,
    val title: String,
    val parent: Folder? = null,
    val isGenerated: Boolean = false,
    val children: MutableList<Folder> = mutableListOf()
)

/**
 * Получить root папку.
 */
internal fun getDemoHierarchyRoot() = demoHierarchy

/**
 * Получить id папки.
 */
internal fun getFolderById(id: String) = folders[id]!!

/**
 * Добавить случайную дочернюю папку.
 */
internal fun Folder.addRandomChildren() {
    for (len in 1 until MAX_RANDOM_FOLDER_LEN) {
        addFolder(generateRandomString(len), true)
    }
}

private val folders = HashMap<String, Folder>()

private val demoHierarchy = Folder(folderId.toString(), "ROOT")
    .also { folders[it.id] = it }
    .apply {
        addFolder("Платформа").apply {
            addFolder("Методическая поддержка").apply {
                addFolder("Инфраструктура представления").apply {
                    addFolder("Каркас представления")
                    addFolder("Контролы").apply {
                        addFolder("Контролы ядра").apply {
                            addFolder("Система вызовов")
                        }
                        addFolder("Расширенный набор контролов").apply {
                            addFolder("Подразделение из сотрудников, входящих в подразделение").apply {
                                addFolder("Развертывание приложений")
                            }
                        }
                    }
                }
            }
            addFolder("Анализ данных и извлечение знаний").apply {
                addFolder("Системы анализа и представления статистики")
                addFolder("Сервис истории")
            }
        }
        addFolder("Машинное обучение")
        addFolder("Документооборот и УЦ").apply {
            addFolder("Системы хранения и передачи документов")
            addFolder("Платформа ЭДО").apply {
                addFolder("Мобильная разработка ЭДО")
                addFolder("Прикладная криптография")
            }
        }
        addFolder("Отчетность").apply {
            addFolder("Рабочее время и выездные сотрудники").apply {
                addFolder("Управление персоналом и расчёт ЗП").apply {
                    addFolder("CRM").apply {
                        addFolder("Розница, Presto и Салон красоты")
                    }
                    addFolder("Биллинг").apply {
                        addFolder("Клиентские плагины")
                    }
                }
            }
        }
        addFolder("Прочее").apply {
            addFolder("Подразделение, которое входит в подразделение, которое входит в подразделение, которое входит в подразделение, которое входит в подразделение, которое входит в подразделение, которое входит в подразделение и не выходит").apply {
                addFolder("Крун Тхеп Маханакхон Амон Раттанакосин Махинтараюттхая Махадилок Пхоп Ноппарат Ратчатхани Буриром Удомратчанивет Махасатан Амон Пиман Аватан Сатит Саккатхаттийя Витсанукам Прасит")
            }
            addFolder("Общество с ограниченной ответственностью").apply {
                addFolder("Администрация Атамановского сельского поселения Брединского муниципального района Челябинской области")
            }
            addFolder("\u2753\u2757\u26A1\u2B50")
            addFolder("Белый кот, ООО")
            addFolder("107-Питер, ООО")
            addFolder("18092015 \"Приорбанк\" (Республика Беларусь), ОАО")
            addFolder("АЛЬФА-БАНК, ОАО")
            addFolder(".")
            addFolder("…")
        }
        addRandomChildren()
    }

private fun Folder.addFolder(title: String, isGenerated: Boolean = false): Folder {
    return Folder((++folderId).toString(), title, this, isGenerated).also {
        children.add(it)
        folders.put(it.id, it)
    }
}

private fun generateRandomString(length: Int): String {
    val characters = ('а'..'я')
        .minus('ъ')
        .minus('ь')
        .minus('ы')
    val c1 = characters.random()
    val c2 = characters.minus(c1).random()
    return (1..length)
        .map {
            val rand = Math.random()
            when {
                it == 1 && rand <= 0.5 -> c1.toUpperCase()
                it == 1                -> c2.toUpperCase()
                rand <= 0.45           -> c1
                rand <= 0.9            -> c2
                else                   -> ' '
            }
        }
        .joinToString("")
        .trim()
}