package ru.tensor.sbis.hallscheme.v2.presentation.model.row

import android.content.Context
import android.graphics.BitmapShader
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.LinearLayout.HORIZONTAL
import android.widget.LinearLayout.VERTICAL
import android.widget.TableRow
import ru.tensor.sbis.design.sbis_text_view.SbisTextView
import ru.tensor.sbis.hallscheme.R
import ru.tensor.sbis.hallscheme.v2.HallSchemeV2
import ru.tensor.sbis.hallscheme.v2.business.model.hallschemeitem.rows.Row
import ru.tensor.sbis.hallscheme.v2.business.model.hallschemeitem.rows.RowPlace
import ru.tensor.sbis.hallscheme.v2.presentation.model.HallSchemeItemUi
import ru.tensor.sbis.hallscheme.v2.widget.RotatableTableLayout
import java.lang.ref.WeakReference

/**
 * Класс для отображения ряда в зале.
 * @author aa.gulevskiy
 */
internal class RowUi(private val row: Row) : HallSchemeItemUi(row) {

    override fun get3dView(viewGroup: ViewGroup): View =
        getView(viewGroup)

    override fun draw(
        viewGroup: ViewGroup,
        onItemClickListener: HallSchemeV2.OnHallSchemeItemClickListener?
    ) {
        val view = getView(viewGroup)
        viewReference = WeakReference(view)
        setElementZ(view)
        view.isClickable = false
    }

    override fun getView(viewGroup: ViewGroup): View {
        val tableLayout = RotatableTableLayout.newInstance(viewGroup.context, row)
        tableLayout.orientation = VERTICAL

        for (rowPlaceList in row.items) {
            tableLayout.addView(getTableRow(tableLayout.context, rowPlaceList))
        }

        viewGroup.addView(tableLayout)

        return tableLayout
    }

    override fun draw3D(
        viewGroup: ViewGroup,
        pressedShader: BitmapShader,
        unpressedShader: BitmapShader,
        onItemClickListener: HallSchemeV2.OnHallSchemeItemClickListener?
    ) {
        val view = get3dView(viewGroup)
        viewReference = WeakReference(view)
        setElementZ(view)
        view.isClickable = false
    }

    private fun getTableRow(context: Context, rowPlaces: List<RowPlace>): TableRow {
        val tableRow = TableRow(context)
        tableRow.orientation = HORIZONTAL
        tableRow.layoutParams = ViewGroup.LayoutParams(WRAP_CONTENT, WRAP_CONTENT)

        val rowNumber = rowPlaces[0].rowNumber
        addLeftLabelIfNeeded(tableRow, rowNumber)

        for (item in rowPlaces) {
            addRowPlaceView(context, tableRow, item)
        }

        addRightLabelIfNeeded(tableRow, rowNumber)

        return tableRow
    }

    private fun addRowPlaceView(context: Context, tableRow: TableRow, item: RowPlace) {
        val rowPlaceView = LayoutInflater.from(context).inflate(
            R.layout.hall_scheme_item_row_place,
            tableRow,
            false
        )
        setLayoutParams(rowPlaceView)

        val rowPlaceContourView = rowPlaceView.findViewById<FrameLayout>(R.id.contourLayout)

        val rowPlaceTextView = rowPlaceContourView.findViewById<SbisTextView>(R.id.rowPlaceTextView)
        rowPlaceTextView.text = item.placeNumber
        rowPlaceTextView.rotation = -row.itemRotation.toFloat()

        tableRow.addView(rowPlaceView)
    }

    private fun setLayoutParams(rowPlaceView: View) {
        val lp = rowPlaceView.layoutParams as LinearLayout.LayoutParams
        lp.width = row.labelSize
        lp.height = row.labelSize
    }

    private fun addLeftLabelIfNeeded(tableRow: TableRow, rowNumber: String) {
        if (row.showLeftLabel) {
            addLabelView(tableRow, rowNumber)
        }
    }

    private fun addRightLabelIfNeeded(tableRow: TableRow, rowNumber: String) {
        if (row.showRightLabel) {
            addLabelView(tableRow, rowNumber)
        }
    }

    private fun addLabelView(tableRow: TableRow, rowNumber: String) {
        val labelView = LayoutInflater.from(tableRow.context)
            .inflate(R.layout.hall_scheme_item_row_label, tableRow, false) as SbisTextView
        labelView.text = tableRow.context.getString(R.string.hall_scheme_row_number, rowNumber)
        setLayoutParams(labelView)
        labelView.rotation = -row.itemRotation.toFloat()

        tableRow.addView(labelView)
    }
}