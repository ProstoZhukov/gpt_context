package ru.tensor.sbis.modalwindows.optionscontent.universal.immutable

import android.os.Bundle
import android.os.Parcel
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.CallSuper
import androidx.fragment.app.Fragment
import ru.tensor.sbis.common.util.requireParentAs
import ru.tensor.sbis.common.util.withArgs
import ru.tensor.sbis.design_dialogs.dialogs.container.base.BaseContainerDialogFragment
import ru.tensor.sbis.modalwindows.R
import ru.tensor.sbis.modalwindows.bottomsheet.BottomSheetOption
import ru.tensor.sbis.modalwindows.bottomsheet.BottomSheetOptionsAdapter
import ru.tensor.sbis.modalwindows.optionscontent.AbstractOptionSheetContentFragment
import ru.tensor.sbis.modalwindows.optionscontent.DialogContentCreator
import ru.tensor.sbis.modalwindows.optionscontent.universal.immutable.UniversalImmutableOptionsContentFragment.OptionListener

/**
 * Универсальный фрагмент для показа заранее заготовленных опций.
 * Вызывающий фрагмент должен реализовывать [OptionListener].
 * Фрагмент для показа опций должен отображаться внутри [BaseContainerDialogFragment].
 * При создании экземпляра необходимо передать список опций для отображения.
 *
 * @author sr.golovkin
 */
class UniversalImmutableOptionsContentFragment
    : AbstractOptionSheetContentFragment<UniversalImmutableOptionsContentContract.View, UniversalImmutableOptionsContentContract.Presenter, BottomSheetOption>(),
    UniversalImmutableOptionsContentContract.View {

    companion object {

        /** @SelfDocumented **/
        const val FRAGMENT_MAIN_TAG = "UniversalImmutableOptionsContentFragment"
        private const val ARGS_OPTIONS = "$FRAGMENT_MAIN_TAG.OPTIONS"

        /**
         * @SelfDocumented
         */
        private fun newInstance(options: ArrayList<BottomSheetOption>): UniversalImmutableOptionsContentFragment {
            return UniversalImmutableOptionsContentFragment()
                .withArgs {

                putParcelableArrayList(ARGS_OPTIONS, options)
            }
        }

        /**
         * Получить список опций из [Bundle]
         */
        private fun getOptions(fragment: UniversalImmutableOptionsContentFragment): List<BottomSheetOption> {
            return fragment.arguments?.getParcelableArrayList(ARGS_OPTIONS)
                ?: throw IllegalStateException("No options specified for ${UniversalImmutableOptionsContentFragment::class.java.canonicalName}")
        }

    }

    /**
     * Класс, реализующий интерфейс [DialogContentCreator] для использования фрагмента внутри контейнера
     */
    class Creator @JvmOverloads constructor(
        val options: ArrayList<BottomSheetOption>,
        tag: String? = null
    ) : DialogContentCreator(tag) {

        constructor(parcel: Parcel) : this(
            arrayListOf<BottomSheetOption>().apply {
                parcel.readList(this as List<*>, BottomSheetOption::class.java.classLoader)
            },
            parcel.readString()
        )

        override fun createFragment(): Fragment {
            return newInstance(
                options
            )
        }

        override fun writeToParcel(parcel: Parcel, flags: Int) {
            parcel.writeList(listOf(options))
            super.writeToParcel(parcel, flags)
        }

        override fun describeContents(): Int {
            return 0
        }

        companion object CREATOR : Parcelable.Creator<Creator> {
            override fun createFromParcel(parcel: Parcel): Creator {
                return Creator(parcel)
            }

            override fun newArray(size: Int): Array<Creator?> {
                return arrayOfNulls(size)
            }
        }

    }

    /**
     * Интерфейс слушателя опций
     */
    interface OptionListener {

        /**
         * Обработать нажатие на опцию
         */
        fun onOptionClick(optionValue: Int)
    }

    @CallSuper
    override fun onCreate(savedInstanceState: Bundle?) {
        // бросаем исключение, если родителем является не диалоговое окно
        requireParentAs<BaseContainerDialogFragment>()
        super.onCreate(savedInstanceState)
    }

    override fun createOptionsAdapter(
        options: List<BottomSheetOption>,
        isLandscape: Boolean,
        listener: BottomSheetOptionsAdapter.Listener<BottomSheetOption>
    ): BottomSheetOptionsAdapter<BottomSheetOption> {
        return BottomSheetOptionsAdapter(options, listener)
    }

    override fun createPresenter(): UniversalImmutableOptionsContentContract.Presenter {
        return UniversalImmutableOptionsContentPresenter(
            getOptions(
                this
            )
        )
    }

    override fun getPresenterView(): UniversalImmutableOptionsContentContract.View {
        return this
    }

    override fun notifyOptionSelected(option: BottomSheetOption) {
        requireParentAs<BaseContainerDialogFragment>()
            // parent, из которого показывается диалог, должен реализовывать OptionListener
            .requireParentAs<OptionListener>()
            .onOptionClick(option.optionValue)
    }

    override fun closeDialog() {
        requireParentAs<BaseContainerDialogFragment>().dismissAllowingStateLoss()
    }
}