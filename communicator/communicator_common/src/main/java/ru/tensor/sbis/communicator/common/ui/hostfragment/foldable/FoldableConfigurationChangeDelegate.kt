package ru.tensor.sbis.communicator.common.ui.hostfragment.foldable

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import ru.tensor.sbis.common.util.DeviceConfigurationUtils
import ru.tensor.sbis.communicator.common.util.doIf
import ru.tensor.sbis.communicator.declaration.MasterFragment
import ru.tensor.sbis.design.R as RDesign

/**
 * Делегат для перемещения контента host фрагментов
 * при специфичных изменениях конфигураций на foldable девайсах
 *
 * @author vv.chekurda
 */
interface FoldableConfigurationChangeDelegate {

    /**
     * Инициализация делегата при создании host фрагмента
     *
     * @param config параметры конфигурации host фрагмента
     */
    fun onCreateFoldableHostFragment(config: FoldableHostConfig)

    /**
     * Очистка делегата при уничтожении host фрагмента
     */
    fun onDestroyFoldableHostFragment()

    /**
     * Сохранение состояния делегата
     *
     * @param outState [Bundle], в котором сохраняется состояние фрагмента
     */
    fun onSaveFoldableHostFragmentState(outState: Bundle)

    companion object {

        /**
         * Создание экземпляра делегата
         *
         * @return новый экземпляр делегата
         */
        val NEW_INSTANCE: FoldableConfigurationChangeDelegate
            get() = FoldableConfigurationChangeDelegateImpl()
    }
}

/**
 * Реализация делегата [FoldableConfigurationChangeDelegate]
 */
class FoldableConfigurationChangeDelegateImpl : FoldableConfigurationChangeDelegate {

    private var currentConfig: FoldableHostConfig? = null

    private val fragmentManager: FragmentManager
        get() = currentConfig!!.fragmentManager

    private val masterContainerId: Int
        get() = currentConfig!!.masterContainerId

    private val detailContainerId: Int
        get() = currentConfig!!.detailContainerId

    private val isTablet: Boolean
        get() = currentConfig?.context?.let(DeviceConfigurationUtils::isTablet) ?: false

    override fun onCreateFoldableHostFragment(config: FoldableHostConfig) {
        currentConfig = config
        config.savedInstanceState?.let(::checkFoldableStateChanged)
    }

    override fun onDestroyFoldableHostFragment() {
        currentConfig = null
    }

    override fun onSaveFoldableHostFragmentState(outState: Bundle) {
        outState.putBoolean(LAST_CONFIGURATION_IS_TABLET_KEY, isTablet)
    }

    /**
     * Проверка конфигурации на предмет изменения foldable состояний
     *
     * @param savedInstanceState сохраненные параметры фрагмента
     */
    private fun checkFoldableStateChanged(savedInstanceState: Bundle) {
        when {
            !isFoldableConfigurationChanged(savedInstanceState) -> return
            isTablet -> handleTabletFoldableConfigurationChanged()
            else -> handlePhoneFoldableConfigurationChanged()
        }
    }

    /**
     * Проверка на изменение конфигурации телефон/планшет
     * @return true, если конфигурация изменилась
     */
    private fun isFoldableConfigurationChanged(savedInstanceState: Bundle): Boolean =
        savedInstanceState.getBoolean(LAST_CONFIGURATION_IS_TABLET_KEY) != isTablet

    /**
     * Обработка измеения конфигурации телефон -> планшет.
     * После обработки оповещаются слушатели.
     */
    private fun handleTabletFoldableConfigurationChanged() {
        transferFragmentsToDetailContainer().let { withTransfers ->
            currentConfig?.listener?.onFoldableStateChanged(
                newState = FoldableState.UNFOLDED,
                withFragmentTransfers = withTransfers
            )
        }
    }

    /**
     * Обработка измеения конфигурации планшет -> телефон.
     * После обработки оповещаются слушатели.
     */
    private fun handlePhoneFoldableConfigurationChanged() {
        transferFragmentsToMasterContainer().let { withTransfers ->
            currentConfig?.listener?.onFoldableStateChanged(
                newState = FoldableState.FOLDED,
                withFragmentTransfers = withTransfers
            )
        }
    }

    /**
     * Перемещение detail-фрагментов в master-контейнер при переходе от планшета к телефону
     * на foldable девайсах
     *
     * @return true, если были осуществлены транзакции по перемещению фрагментов в другой контейнер
     */
    private fun transferFragmentsToMasterContainer(): Boolean {
        val detailFragmentList: MutableList<Fragment> = mutableListOf()

        // Собираем все фрагменты из стека, которые находятся в detail контейнере
        do {
            fragmentManager.findFragmentById(detailContainerId)
                ?.let { fragment -> detailFragmentList.add(fragment) }
        } while (fragmentManager.popBackStackImmediate())

        // Прибиваем последний фрагмент, который находится вне стека
        fragmentManager.findFragmentById(detailContainerId)
            ?.let { lastDetailFragment ->
                fragmentManager.beginTransaction()
                    .remove(lastDetailFragment)
                    .commit()
                fragmentManager.executePendingTransactions()
            }

        // Перемещаем все найденные фрагменты в master контейнер
        detailFragmentList.reversed().forEach {
            fragmentManager.beginTransaction()
                .setCustomAnimations(0, 0, 0, RDesign.anim.right_out)
                .add(masterContainerId, it::class.java, it.arguments, it.tag)
                .addToBackStack(it::class.java.simpleName)
                .commit()
        }
        return detailFragmentList.isNotEmpty()
    }

    /**
     * Перемещение detail фрагментов обратно в details контейнер при переходе от телефона к планшету
     * на foldable девайсах
     *
     * @return true, если были осуществлены транзакции по перемещению фрагментов в другой контейнер
     */
    private fun transferFragmentsToDetailContainer(): Boolean {
        val detailFragmentList: MutableList<Fragment> = mutableListOf()

        // Собираем все фрагменты из стека, кроме фрагмента самого реестра, которые находятся в master контейнере
        do {
            fragmentManager.findFragmentById(masterContainerId)
                ?.takeIf { fragment -> fragment !is MasterFragment }
                ?.let { fragment -> detailFragmentList.add(fragment) }
        } while (fragmentManager.popBackStackImmediate())

        // Перемещаем все найденные фрагменты в details контейнер, первый фрагмент вне стека
        detailFragmentList.reversed().forEachIndexed { index, fragment ->
            fragmentManager.beginTransaction()
                .replace(detailContainerId, fragment::class.java, fragment.arguments, fragment.tag)
                .doIf(index != 0) { addToBackStack(fragment::class.java.simpleName) }
                .commit()
        }
        return detailFragmentList.isNotEmpty()
    }
}

/**
 * Ключ для сохранения предыдущей конфигурации "isTablet"
 */
private const val LAST_CONFIGURATION_IS_TABLET_KEY = "LAST_CONFIGURATION_IS_TABLET_KEY"