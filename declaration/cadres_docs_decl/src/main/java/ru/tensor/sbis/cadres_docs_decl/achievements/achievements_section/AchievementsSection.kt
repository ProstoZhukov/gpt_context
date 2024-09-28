package ru.tensor.sbis.cadres_docs_decl.achievements.achievements_section

import ru.tensor.sbis.base_components.adapter.sectioned.content.ListSection
import ru.tensor.sbis.base_components.adapter.universal.UniversalBindingItem
import ru.tensor.sbis.cadres_docs_decl.achievements.achievements_section.contract.AchievementsSectionContract
import ru.tensor.sbis.mvp.presenter.loadcontent.LoadContentView

/**
 * Класс-контракт секции ПиВ для предоставления в модули потребители.
 *
 *  Подразумевается, что секции шапки ПиВ является "ведущей" на экране, то есть предоставляет
 * основную информацию, при отсутствии которой показ остальной информации нецелесообразен.
 *  Поэтому секция с помощью интерфейсов [AchievementsStubListener] и [LoadContentView]
 * умеет уведомлять потребителя секции о необходимости показывать/скрывать заглушки, показывать прогресс бар.
 * При этом [LoadContentView.showLoadingProcess] вызывается каждый раз, когда секции начала
 * обновлять либо завершила обновлять данные. Показывать ли прогресс бар каждый раз либо только
 * при первой загрузке решает потребитель.
 *
 * ВАЖНО - [LoadContentView.showLoadingProcess] при первой загрузке данных будет вызван через 2 сек
 * для соответствия стандарту подгрузки данных.
 *
 * @param sectionController - контроллер (ViewModel) секции.
 * @param adapter - адаптер ячеек секции.
 *
 * @author ra.temnikov
 */
abstract class AchievementsSection(
    sectionController: AchievementsSectionContract.ViewModel,
    adapter: AchievementsSectionAdapter
) : ListSection<UniversalBindingItem, AchievementsSectionContract.ViewModel, AchievementsSectionAdapter>(
    sectionController,
    adapter
), AchievementsSectionContract.View