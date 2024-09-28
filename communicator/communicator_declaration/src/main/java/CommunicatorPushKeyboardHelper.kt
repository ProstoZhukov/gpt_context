import kotlinx.coroutines.flow.MutableSharedFlow
import ru.tensor.sbis.plugin_struct.feature.Feature

/**
 * Помощник для опускания клавиатуры, при переход по пушу.
 *
 * @author da.zhukov
 */
interface CommunicatorPushKeyboardHelper {

    /** SelfDocumented */
    val hideKeyboard: MutableSharedFlow<Boolean>

    interface Provider : Feature {

        /** SelfDocumented */
        fun getCommunicatorPushKeyboardHelper(): CommunicatorPushKeyboardHelper
    }
}