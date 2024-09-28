package ru.tensor.sbis.list.view.container

import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import androidx.annotation.AnyThread
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import io.reactivex.subjects.PublishSubject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import ru.tensor.sbis.design.stubview.ResourceImageStubContent
import ru.tensor.sbis.list.base.presentation.StubLiveData
import ru.tensor.sbis.list.base.presentation.StubViewContentFactory
import ru.tensor.sbis.list.view.container.State.LIST
import ru.tensor.sbis.list.view.container.State.PROGRESS
import ru.tensor.sbis.list.view.container.State.STUB

/**
 * Вью модель контейнера списка, содержит примитивную логику показа только одного элемента единовременно.
 *
 * @property _stubViewVisibility для тестов.
 * @property _progressVisibility для тестов.
 * @property _stubContent для тестов.
 */
class ListContainerViewModelImpl internal constructor(
    private val _stubViewVisibility: MutableLiveData<Int> = MutableLiveData<Int>(INVISIBLE),
    private val _listVisibility: MutableLiveData<Int> = MutableLiveData<Int>(INVISIBLE),
    private val _progressVisibility: MutableLiveData<Int> = MutableLiveData<Int>(INVISIBLE),
    private val _stubContent: StubLiveData = StubLiveData(getEmptyStub()),
    private val progressIsVisible: PublishSubject<Boolean> = PublishSubject.create()
) : ListContainerViewModel {

    private val state = MutableLiveData<State>()
    private val debounceNotListLiveData = state.debounceNotList()

    init {
        debounceNotListLiveData.observeForever {
            when (it!!) {
                PROGRESS -> {
                    _progressVisibility.show()
                    _listVisibility.hide()
                    _stubViewVisibility.hide()
                    progressIsVisible.onNext(true)
                }
                LIST -> {
                    _listVisibility.show()
                    _progressVisibility.hide()
                    _stubViewVisibility.hide()
                    progressIsVisible.onNext(false)
                }
                STUB -> {
                    _stubViewVisibility.show()
                    _progressVisibility.hide()
                    _listVisibility.hide()
                    progressIsVisible.onNext(true)
                }
            }
        }
    }

    override val stubViewVisibility: LiveData<Int> = _stubViewVisibility
    override val listVisibility: LiveData<Int> = _listVisibility
    override val progressVisibility: LiveData<Int> = _progressVisibility
    override val stubContent: LiveData<StubViewContentFactory> = _stubContent

    override fun showOnlyProgress() {
        state.postValue(PROGRESS)
    }

    override fun showOnlyStub(immediate: Boolean) {
        if (immediate) {
            debounceNotListLiveData.setValueImmediate(STUB)
        } else {
            state.postValue(STUB)
        }
    }

    override fun showOnlyList() {
        state.postValue(LIST)
    }

    override fun setStubContentFactory(factory: StubViewContentFactory) {
        _stubContent.postValue(factory)
    }

    private fun MutableLiveData<Int>.hide() {
        postValue(INVISIBLE)
    }

    private fun MutableLiveData<Int>.show() {
        postValue(VISIBLE)
    }
}

private fun getEmptyStub(): StubViewContentFactory =
    { ResourceImageStubContent(message = "", details = "") }


/**
 * При показе экрана в первый раз, контроллер шлет пустой список из кеша, пока идет в сеть.
 * Нужно отсеивать этот ложный показ заглушки до прихода данных, которые за короткое время
 * чаще всего приходят.
 * Второй случай - это показ индикатора прогресса, его можно показывать только с задержкой.
 * При этом сам список показываем сразу.
 */
private fun LiveData<State>.debounceNotList() = object : MediatorLiveData<State>() {

    private val source = this@debounceNotList
    private val coroutineScope = CoroutineScope(Dispatchers.Main)
    private var job: Job? = null
    private var previousState: State? = null

    init {
        val mld = this
        addSource(source) {
            cancelJob()
            if (source.value == value) return@addSource
            val value = source.value
            val needDebounce = (previousState == LIST || previousState == null) && value != LIST
            cancelJob()
            if (needDebounce) {
                job = coroutineScope.launch {
                    delay(value!!.timeDelayBeforeShowMs)
                    mld.postValue(value)
                    previousState = value
                }
            } else {
                previousState = value
                postValue(value)
            }
        }
    }

    /** @SelfDocumented */
    @AnyThread
    fun setValueImmediate(state: State) {
        cancelJob()
        postValue(state)
        previousState = state
    }

    private fun cancelJob() {
        job?.cancel()
        job = null
    }
}