package ru.tensor.sbis.message_panel.view

import io.reactivex.disposables.CompositeDisposable
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner
import ru.tensor.sbis.message_panel.viewModel.livedata.MessagePanelDataControls
import ru.tensor.sbis.message_panel.viewModel.livedata.initLiveData

/**
 * @author vv.chekurda
 */
@RunWith(MockitoJUnitRunner.StrictStubs::class)
class NewDialogModeHelperTest {

    private val liveData: MessagePanelDataControls = initLiveData()
    private val helper = NewDialogModeHelper(liveData, CompositeDisposable())

    private val newDialog = liveData.newDialogModeEnabled.test()

    @Test
    fun `When record started and message panel is in new dialog mode, then message panel should switch from new dialog mode`() {
        liveData.newDialogModeEnabled(true)

        helper.onRecordStarted()

        newDialog.assertValues(false, true, false)
    }

    @Test
    fun `When record started and message panel is not in new dialog mode, then no interactions should be`() {
        helper.onRecordStarted()

        newDialog.assertValue(false)
    }

    @Test
    fun `When record completed and message panel is not in new dialog mode, then no interactions should be`() {
        helper.onRecordCompleted()

        newDialog.assertValue(false)
    }

    @Test
    fun `When record started and message panel is in new dialog mode, then message panel should be in new dialog mode after record completion`() {
        liveData.newDialogModeEnabled(true)

        helper.onRecordStarted()
        helper.onRecordCompleted()

        newDialog.assertValues(false, true, false, true)
    }
}