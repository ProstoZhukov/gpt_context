package ru.tensor.sbis.cadres_docs_decl.achievements.achievements_section


import ru.tensor.sbis.base_components.adapter.sectioned.content.ListSectionAdapter
import ru.tensor.sbis.base_components.adapter.universal.UniversalBindingItem
import ru.tensor.sbis.base_components.adapter.universal.UniversalTwoWayPaginationAdapter
import ru.tensor.sbis.base_components.adapter.vmadapter.ViewModelAdapter

/** Класс-контракт адаптера секции ПиВ */
abstract class AchievementsSectionAdapter : UniversalTwoWayPaginationAdapter<UniversalBindingItem>(),
    ListSectionAdapter<UniversalBindingItem>