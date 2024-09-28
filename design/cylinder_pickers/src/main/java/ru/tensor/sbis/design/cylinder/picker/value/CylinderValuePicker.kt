package ru.tensor.sbis.design.cylinder.picker.value

import android.content.Context
import android.util.AttributeSet
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSnapHelper
import androidx.recyclerview.widget.RecyclerView
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import ru.tensor.sbis.design.cylinder.picker.plusAssign
import java.util.concurrent.TimeUnit

const val CENTER_OFFSET = 2

/**
 * @author Subbotenko Dmitry
 */

class CylinderTypePicker @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0
) : RecyclerView(context, attrs, defStyle) {

    private val disposer = CompositeDisposable()
    private val linearLayoutManager get() = layoutManager as LinearLayoutManager

    private var onScrollPause = false

    fun <TYPE> init(liveData: LiveData<TYPE>) {
        disposer.clear()
        layoutManager = LinearLayoutManager(context)
        val valuesAdapter = CylinderValuePickerAdapter(liveData.values, liveData.comparator, liveData.stringConverter)
        adapter = valuesAdapter

        liveData.cylinder = -1

        LinearSnapHelper().attachToRecyclerView(this)

        val scrollListener = object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                if (!isVisible || onScrollPause || dy == 0)
                    return
                val position = (layoutManager as LinearLayoutManager)
                    .findFirstVisibleItemPosition() + CENTER_OFFSET
                valuesAdapter.getValueForPosition(position)?.let {
                    if (liveData.valueSetter != it) {
                        liveData.cylinder = id
                        liveData.valueSetter = it
                    }
                }
            }
        }

        liveData.valueSetter?.let {
            valuesAdapter.currentValue = it
            linearLayoutManager.scrollToPositionWithOffset(valuesAdapter.getPositionForValue(it) - CENTER_OFFSET, 0)
        }
        addOnScrollListener(scrollListener)

        disposer += liveData.valueChangeObservable
            .throttleLast(150, TimeUnit.MILLISECONDS)
            .observeOn(AndroidSchedulers.mainThread())
            .skip(1)
            /*
             При выставлении значения барабана при вервой загрузке с помощью метода scrollToPositionWithOffset
             ресайклер внросит значение скролла в pendingPosition. В результате чего  stopScrollListener = false происходит
             раньше чем барабан начинает вращение.

             в результате onScrolled получает первое значение и сбрасывает счетчики барабанов на минимум.

             skip(1) симптоматически фиксит проблему, но это не значит что она не повториться впредь.

             Для полного фикса нужен кастомный layout manager который сможет правильно делать scrollToPositionWithOffset без вызова onScrolled()
             */
            .filter { liveData.cylinder != id }
            .filter { type ->
                val position =
                    (layoutManager as LinearLayoutManager).findFirstVisibleItemPosition() + CENTER_OFFSET
                liveData.comparator.compare(valuesAdapter.getValueForPosition(position), type) != 0
            }
            .subscribe {
                onScrollPause = true
                stopScroll()
                valuesAdapter.currentValue = it
                linearLayoutManager.scrollToPositionWithOffset(valuesAdapter.getPositionForValue(it) - CENTER_OFFSET, 0)
                onScrollPause = false
            }
    }

    @Suppress("UNCHECKED_CAST")
    fun <TYPE> updateValues(collection: Collection<TYPE>) {
        (adapter as CylinderValuePickerAdapter<TYPE>).run {
            values = collection
            notifyDataSetChanged()
        }
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        disposer.clear()
    }

    interface LiveData<TYPE> {
        var cylinder: Int
        val valueChangeObservable: Observable<TYPE>
        var valueSetter: TYPE
        val values: Collection<TYPE>
        val comparator: Comparator<TYPE>
        val stringConverter: (TYPE) -> String
    }
}
