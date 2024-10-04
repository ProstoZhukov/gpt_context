package ru.tensor.sbis.design.cylinder.picker.value

import android.content.Context
import android.util.AttributeSet
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSnapHelper
import androidx.recyclerview.widget.RecyclerView
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import ru.tensor.sbis.design.cylinder.picker.plusAssign

/**
 * Компонент для кругового выбора значения из списка.
 *
 * @author ae.noskov
 */
class CylinderLoopValuePicker @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0
) : RecyclerView(context, attrs, defStyle) {

    private val disposer = CompositeDisposable()
    private val linearLayoutManager get() = layoutManager as LinearLayoutManager

    /** @SelfDocumented */
    fun <TYPE> init(liveData: ValueLiveData<TYPE>) {
        disposer.clear()
        layoutManager = LinearLayoutManager(context)
        val valuesAdapter = CylinderLoopValuePickerAdapter(liveData.values, liveData.comparator, liveData.bind)
        adapter = valuesAdapter
        linearLayoutManager.scrollToPositionWithOffset(INIT_POSITION, 0)

        LinearSnapHelper().attachToRecyclerView(this)

        val scrollListener = object : OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                if (isVisible || dy == 0) {
                    val position = (layoutManager as LinearLayoutManager).findFirstVisibleItemPosition() + CENTER_OFFSET
                    valuesAdapter.getValueForPosition(position)?.let {
                        if (liveData.valueSetter != it) {
                            liveData.cylinder = id
                            valuesAdapter.currentValue = it
                            liveData.valueSetter = it
                        }
                    }
                }
            }
        }

        liveData.valueSetter?.let {
            scrollToValue(liveData, valuesAdapter, it)
        }
        addOnScrollListener(scrollListener)

        disposer += liveData.collectionChangeObservable
            .observeOn(AndroidSchedulers.mainThread())
            .filter { liveData.cylinder != id && it.size != valuesAdapter.actualValuesList.size }
            .subscribe { list ->
                var newValue: TYPE = valuesAdapter.currentValue ?: return@subscribe
                val currentList = valuesAdapter.actualValuesList
                if (list.size < currentList.size &&
                    currentList.subList(list.size, currentList.size).contains(valuesAdapter.currentValue)
                ) {
                    newValue = list.last()
                }
                valuesAdapter.collection(list)
                scrollToValue(liveData, valuesAdapter, newValue)
            }

        var isFirst = true
        disposer += liveData.valueChangeObservable
            .observeOn(AndroidSchedulers.mainThread())
            .filter { liveData.cylinder != id || valuesAdapter.currentValue != it }
            .subscribe {

                /**
                 * Костыль с обновлением layoutManager, т.к. при первой загрузке данных происходит баг описанный
                 * в [CylinderTypePicker]. Использование такого же костыля, как в приведённом ранее классе,
                 * результатов не дало.
                 */
                if (isFirst) {
                    layoutManager = LinearLayoutManager(context)
                    isFirst = false
                }

                scrollToValue(liveData, valuesAdapter, it)
            }
    }

    private fun <TYPE> scrollToValue(
        liveData: ValueLiveData<TYPE>,
        adapter: CylinderLoopValuePickerAdapter<TYPE>,
        item: TYPE,
    ) {
        adapter.currentValue = item
        val targetPosition = adapter.getPositionForValue(
            item,
            adapter.getPositionForValue(item, INIT_POSITION)
        ) - CENTER_OFFSET
        linearLayoutManager.scrollToPosition(targetPosition)
        liveData.valueSetter = item
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        disposer.clear()
    }

}
