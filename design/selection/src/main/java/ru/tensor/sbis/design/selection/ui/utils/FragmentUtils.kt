package ru.tensor.sbis.design.selection.ui.utils

import android.content.Context
import android.os.Parcelable
import android.view.LayoutInflater
import androidx.annotation.StyleRes
import androidx.appcompat.view.ContextThemeWrapper
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import ru.tensor.sbis.design.selection.R
import ru.tensor.sbis.design.selection.ui.contract.list.ListMapper
import ru.tensor.sbis.design.selection.ui.di.multi.DaggerMultiSelectionComponent
import ru.tensor.sbis.design.selection.ui.di.multi.DaggerMultiSelectionSbisListComponent
import ru.tensor.sbis.design.selection.ui.di.multi.MultiSelectionComponent
import ru.tensor.sbis.design.selection.ui.di.multi.MultiSelectionSbisListComponent
import ru.tensor.sbis.design.selection.ui.factories.FilterFactory
import ru.tensor.sbis.design.selection.ui.fragment.MultiSelectorFragment
import ru.tensor.sbis.design.selection.ui.fragment.SelectorContentFragment
import ru.tensor.sbis.design.selection.ui.fragment.SubSelectorContentFragment
import ru.tensor.sbis.design.selection.ui.list.recipients.RecipientMultiFilterFactory
import ru.tensor.sbis.design.selection.ui.list.recipients.RecipientsMapperFunction
import ru.tensor.sbis.design.selection.ui.model.SelectorItemModel
import ru.tensor.sbis.design.selection.ui.model.recipient.RecipientSelectorItemModel
import ru.tensor.sbis.design.selection.ui.utils.vm.SearchViewModel
import ru.tensor.sbis.list.base.data.ResultHelper
import java.io.Serializable
import kotlin.reflect.KClass
import ru.tensor.sbis.design.R as RDesign

/**
 * Метод для упрощённого извлечения сериализуемой реализации из аргументов
 *
 * @param default сериализуемая реализация по умолчанию. Должна иметь конструктор без аргументов
 */
internal fun <T : Serializable> Fragment.serializableArg(
    key: String,
    default: KClass<out T>
): Lazy<T> = serializableArg(key, default.java::newInstance)

/**
 * Метод для упрощённого извлечения сериализуемой реализации с типовыми параметрами из аргументов
 *
 * @param instanceFactory фабрика для создания параметризованных реализаций по умолчанию
 */
internal fun <T : Serializable> Fragment.serializableArg(
    key: String,
    instanceFactory: (() -> T)? = null
): Lazy<T> = lazy(LazyThreadSafetyMode.NONE) {
    @Suppress("UNCHECKED_CAST")
    requireArguments().getSerializable(key) as T?
        ?: requireNotNull(instanceFactory) {
            "Argument $key is null. You should provide default implementation for nullable arguments"
        }.invoke()
}

/**
 * Метод для упрощённого извлечения [Parcelable] реализации с типовыми параметрами из аргументов
 *
 * @param instanceFactory фабрика для создания параметризованных реализаций по умолчанию
 */
internal fun <T : Parcelable> Fragment.parcelableArg(
    key: String,
    instanceFactory: (() -> T)? = null
): Lazy<T> = lazy(LazyThreadSafetyMode.NONE) {
    requireArguments().getParcelable(key)
        ?: requireNotNull(instanceFactory) {
            "Argument $key is null. You should provide default implementation for nullable arguments"
        }.invoke()
}

/**
 * Метод обеспечивает стандартное добавление [topFragment] над [currentFragment]. Фрагменты добавляются в контейнер с
 * идентификатором [R.id.contentContainer]
 */
internal fun FragmentManager.addTopFragment(currentFragment: Fragment, topFragment: Fragment) {
    beginTransaction()
        .setCustomAnimations(RDesign.anim.right_in, RDesign.anim.fade_out, RDesign.anim.fade_in, RDesign.anim.right_out)
        /*
        Ограничиваем максимальное состояние жизненного цикла фрагмента снизу - это позволяет получать событие onPause
        при открытии нового экрана и onResume при возврате назад
        */
        .setMaxLifecycle(currentFragment, Lifecycle.State.STARTED)
        .addToBackStack(null)
        .add(R.id.contentContainer, topFragment)
        .commit()
}

/**
 * Свойство-расширение для [FragmentManager], которое позволяет проверить "пуст ли back stack"
 */
internal val FragmentManager.isBackStackNotEmpty: Boolean get() = backStackEntryCount > 0

/**
 * Создаёт копию [LayoutInflater], где контекст будет содержать тему [themeRes]
 */
internal fun LayoutInflater.cloneWithTheme(context: Context, @StyleRes themeRes: Int): LayoutInflater =
    cloneInContext(ContextThemeWrapper(context, themeRes))

/**
 * Открывает следующий уровень вложенности для [data] с использованием фрагмента выбора [SelectorContentFragment.multiSelectorFragment]
 */
internal fun SelectorContentFragment.openHierarchy(data: SelectorItemModel) {
    multiSelectorFragment.childFragmentManager.addTopFragment(this, SubSelectorContentFragment.newInstance(data))
}

/**
 * Выполняет возврат на предыдущий уровень (родительский список)
 */
internal fun performGoBack(hostFragmentManager: FragmentManager, searchVm: SearchViewModel) {
    searchVm.cancelSearch()
    hostFragmentManager.popBackStackImmediate()
}

/**
 * Подготавливает [MultiSelectionComponent] в зависимости от окружения в аргументах
 */
internal fun MultiSelectorFragment.createMultiSelectionComponent(): MultiSelectionComponent {
    val arguments = requireArguments()
    return if (arguments.isRecipientCommonAPI)
        DaggerMultiSelectionComponent.factory().create(
            this,
            arguments.recipientsMultiSelectionLoader,
            arguments.itemHandleStrategy,
            arguments.counterFormat
        )
    else
        DaggerMultiSelectionComponent.factory().create(
            this,
            arguments.multiDependenciesFactory.getSelectionLoader(requireContext().applicationContext),
            arguments.itemHandleStrategy,
            requireArguments().counterFormat
        )
}

/**
 * Подготавливает [MultiSelectionSbisListComponent] в зависимости от окружения в аргументах
 */
internal fun SelectorContentFragment.createMultiListComponent(): MultiSelectionSbisListComponent {
    val appContext = requireContext().applicationContext
    val arguments = componentArguments
    return if (arguments.isRecipientCommonAPI)
        DaggerMultiSelectionSbisListComponent.factory().create(
            this,
            null,
            componentArguments.recipientsMultiSelectionLoader,
            RecipientMultiFilterFactory<RecipientSelectorItemModel>() as FilterFactory<SelectorItemModel, Any, Any>,
            componentArguments.recipientsResultHelper as ResultHelper<Any, Any>,
            RecipientsMapperFunction<RecipientSelectorItemModel>() as ListMapper<Any, SelectorItemModel>,
            multiSelectorFragment.selectorStrings,
            componentArguments.enableRecentSelectionCaching,
            multiSelectorFragment.selectionComponent
        )
    else with(arguments.multiDependenciesFactory) {
        DaggerMultiSelectionSbisListComponent.factory().create(
            this@createMultiListComponent,
            getServiceWrapper(appContext),
            getSelectionLoader(appContext),
            getFilterFactory(appContext),
            getResultHelper(appContext),
            getMapperFunction(appContext),
            multiSelectorFragment.selectorStrings,
            componentArguments.enableRecentSelectionCaching,
            multiSelectorFragment.selectionComponent
        )
    }
}
