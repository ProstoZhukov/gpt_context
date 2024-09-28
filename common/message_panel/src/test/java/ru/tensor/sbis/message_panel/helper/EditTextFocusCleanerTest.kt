package ru.tensor.sbis.message_panel.helper

import android.content.Context
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import org.mockito.kotlin.*
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject
import junitparams.JUnitParamsRunner
import junitparams.Parameters
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnit
import org.mockito.junit.MockitoRule
import org.mockito.quality.Strictness
import ru.tensor.sbis.message_panel.viewModel.livedata.keyboard.*
import java.lang.Exception

/**
 * Тест вспомогательного класса для очистки фокуса [EditText] по событиям клавиатуры
 *
 * @author vv.chekurda
 */
@RunWith(JUnitParamsRunner::class)
class EditTextFocusCleanerTest {

    @get:Rule
    val mockitoRule: MockitoRule = MockitoJUnit.rule().strictness(Strictness.STRICT_STUBS)

    @Mock
    private lateinit var editText: EditText
    @Mock
    private lateinit var inputMethodManager: InputMethodManager

    private val focusCleaner = EditTextFocusCleaner(Schedulers.trampoline())

    /**
     * Fix https://online.sbis.ru/opendoc.html?guid=0e78213d-1d52-4b90-9c29-7a94b188431f
     */
    @Test
    @Parameters("true", "false")
    fun `When keyboard ClosedByRequest, then focus should be cleared`(ignoreAdjustHelperEvents: Boolean) {
        val eventSubject = PublishSubject.create<KeyboardEvent>()
        focusCleaner.setIgnoreAdjustHelperEvents(ignoreAdjustHelperEvents)
        focusCleaner.subscribeOnFocusClearing(editText, eventSubject)

        eventSubject.onNext(ClosedByRequest)

        verify(editText, only()).clearFocus()
    }

    @Test
    fun `When keyboard ClosedByAdjustHelper and edit text doesn't have focus, then focus should not be cleared`() {
        val eventSubject = PublishSubject.create<KeyboardEvent>()
        whenever(editText.hasFocus()).thenReturn(false)
        focusCleaner.subscribeOnFocusClearing(editText, eventSubject)

        eventSubject.onNext(ClosedByAdjustHelper(123))

        verifyNoMoreInteractions(editText)
    }

    @Test
    fun `When keyboard ClosedByAdjustHelper, edit text has focus and it is an active input, then focus should not be cleared`() {
        whenever(editText.hasFocus()).thenReturn(true)
        mockInputMethodActivityForEditText(true)

        val eventSubject = PublishSubject.create<KeyboardEvent>()
        focusCleaner.subscribeOnFocusClearing(editText, eventSubject)

        eventSubject.onNext(ClosedByAdjustHelper(123))

        verifyNoMoreInteractions(editText)
    }

    @Test
    fun `When keyboard ClosedByAdjustHelper, edit text has focus and it is not an active input, then focus should be cleared`() {
        whenever(editText.hasFocus()).thenReturn(true)
        mockInputMethodActivityForEditText(false)

        val eventSubject = PublishSubject.create<KeyboardEvent>()
        focusCleaner.subscribeOnFocusClearing(editText, eventSubject)

        eventSubject.onNext(ClosedByAdjustHelper(123))

        verify(editText).clearFocus()
    }

    @Test
    fun `When keyboard ClosedByAdjustHelper, but AdjustHelper events are ignored, then focus should not be cleared`() {
        val eventSubject = PublishSubject.create<KeyboardEvent>()
        focusCleaner.subscribeOnFocusClearing(editText, eventSubject)
        focusCleaner.setIgnoreAdjustHelperEvents(true)

        eventSubject.onNext(ClosedByAdjustHelper(123))

        verifyNoMoreInteractions(editText)
    }

    @Test
    @Parameters(method = "getIgnoredByFocusEvents")
    fun `When keyboard opened or closed by focus, then clearFocus() method should not be called`(event: KeyboardEvent) {
        val eventSubject = PublishSubject.create<KeyboardEvent>()
        focusCleaner.subscribeOnFocusClearing(editText, eventSubject)

        eventSubject.onNext(event)

        verifyNoMoreInteractions(editText)
    }

    /**
     * Fix https://online.sbis.ru/opendoc.html?guid=975090b8-b2ed-436a-a089-4f075deb8971
     */
    @Test
    fun `When keyboard observable emit an exception, then it should be caught by subscription`() {
        val eventObservable = Observable.error<KeyboardEvent> { Exception("Test exception") }

        focusCleaner.subscribeOnFocusClearing(editText, eventObservable)
    }

    private fun getIgnoredByFocusEvents() =
        arrayOf(ClosedByFocus, OpenedByFocus, OpenedByRequest, OpenedByAdjustHelper(321))

    private fun mockInputMethodActivityForEditText(isActive: Boolean) {
        val context: Context = mock {
            on { getSystemService(Context.INPUT_METHOD_SERVICE) } doReturn inputMethodManager
        }
        whenever(editText.context).thenReturn(context)
        whenever(inputMethodManager.isActive(editText)).thenReturn(isActive)
    }

}