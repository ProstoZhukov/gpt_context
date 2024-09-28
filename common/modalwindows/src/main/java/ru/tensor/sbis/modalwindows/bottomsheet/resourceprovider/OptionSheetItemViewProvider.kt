package ru.tensor.sbis.modalwindows.bottomsheet.resourceprovider

import android.view.View
import androidx.annotation.LayoutRes
import ru.tensor.sbis.design.sbis_text_view.SbisTextView
import ru.tensor.sbis.modalwindows.R

/**
 * Поставщик компонентов отображения для ViewHolder'ов опций.
 *
 * @author sr.golovkin
 */
interface OptionSheetItemViewProvider {

    /**
     * Предоставить идентификатор представления для ячейки опции.
     */
    @LayoutRes
    fun provideOptionLayoutRes(): Int

    /**
     * Предоставить вью, отображающую имя опции.
     */
    fun provideTitleView(root: View): SbisTextView

    /**
     * Предоставить вью, отображающую иконку опции.
     */
    fun provideIconView(root: View): SbisTextView
}

/**
 * Стандартная реализация поставщика вью для ViewHolder'ов опций.
 */
@Deprecated("Будет удалено по https://online.sbis.ru/opendoc.html?guid=4f5ff4ec-2c38-4e09-92e9-89c7809bb3c8&client=3")
internal class ModalWindowsOptionSheetItemViewProvider : OptionSheetItemViewProvider {

    override fun provideOptionLayoutRes(): Int = R.layout.modalwindows_bottom_sheet_option_item

    override fun provideTitleView(root: View): SbisTextView = root.findViewById(R.id.modalwindows_menu_item_text)

    override fun provideIconView(root: View): SbisTextView = root.findViewById(R.id.modalwindows_menu_icon)
}