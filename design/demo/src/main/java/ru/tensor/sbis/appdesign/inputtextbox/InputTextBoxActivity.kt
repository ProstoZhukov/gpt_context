package ru.tensor.sbis.appdesign.inputtextbox

import android.os.Bundle
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.view.MotionEvent
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import ru.tensor.sbis.appdesign.R
import ru.tensor.sbis.appdesign.databinding.ActivityInputTextBoxBinding
import ru.tensor.sbis.design.text_span.text.InputTextBox

/**
 * Демо экран поля ввода.
 *
 * @author us.bessonov
 */
class InputTextBoxActivity : AppCompatActivity() {

    private var maxLines = Int.MAX_VALUE
    private var minLines = 1

    private lateinit var viewBinding: ActivityInputTextBoxBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivityInputTextBoxBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)

        viewBinding.switchDisabled.setOnCheckedChangeListener { _, isChecked ->
            applyToInputFields { isEnabled = !isChecked }
        }

        viewBinding.switchError.setOnCheckedChangeListener { _, isChecked ->
            applyToInputFields { setErrorState(isChecked) }
        }

        viewBinding.radioGroupInputType.setOnCheckedChangeListener { _, checkedId ->
            val inputTypeValue = when (checkedId) {
                R.id.input_type_multiline -> InputType.TYPE_CLASS_TEXT or
                        InputType.TYPE_TEXT_FLAG_MULTI_LINE or
                        InputType.TYPE_TEXT_FLAG_CAP_SENTENCES
                R.id.input_type_number    -> InputType.TYPE_CLASS_NUMBER
                else                      -> InputType.TYPE_CLASS_TEXT
            }
            applyToInputFields { inputType = inputTypeValue }
        }
        viewBinding.radioGroupInputType.check(R.id.input_type_multiline)

        viewBinding.decMinLines.setOnClickListener {
            minLines = Math.max(minLines - 1, 1)
            updateMinLines()
        }
        viewBinding.incMinLines.setOnClickListener {
            minLines++
            updateMinLines()
        }
        viewBinding.minLines.text = minLines.toString()

        viewBinding.decMaxLines.setOnClickListener {
            maxLines = if (maxLines <= 1 || maxLines == Int.MAX_VALUE) Int.MAX_VALUE else maxLines - 1
            updateMaxLines()
        }
        viewBinding.incMaxLines.setOnClickListener {
            maxLines = if (maxLines == Int.MAX_VALUE) 1 else maxLines + 1
            updateMaxLines()
        }
        updateMaxLines()

        configureInputFieldWithoutFrameAndClear()
        allowInputFieldsScrolling()
    }

    private fun updateMinLines() {
        viewBinding.minLines.text = minLines.toString()
        viewBinding.decMinLines.isEnabled = minLines > 1
        applyToInputFields { minLines = this@InputTextBoxActivity.minLines }
    }

    private fun updateMaxLines() {
        viewBinding.maxLines.text = if (maxLines < Int.MAX_VALUE) maxLines.toString() else "∞"
        viewBinding.decMaxLines.isEnabled = maxLines != Int.MAX_VALUE
        applyToInputFields { maxLines = this@InputTextBoxActivity.maxLines }
    }

    private fun allowInputFieldsScrolling() {
        applyToInputFields {
            findViewById<View>(R.id.text_span_edit_text_stub).run {
                setOnTouchListener { _, event ->
                    if (hasFocus()) {
                        parent.requestDisallowInterceptTouchEvent(true)
                        if (event.actionMasked == MotionEvent.ACTION_SCROLL) {
                            parent.requestDisallowInterceptTouchEvent(false)
                            return@setOnTouchListener true
                        }
                    }
                    return@setOnTouchListener false
                }
            }
        }
    }

    private fun configureInputFieldWithoutFrameAndClear() {
        viewBinding.textWithoutFrameAndClear.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                viewBinding.textWithoutFrameAndClear.setErrorState(s?.length ?: 0 > 10)
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }
        })
    }

    private fun applyToInputFields(action: InputTextBox.() -> Unit) {
        viewBinding.textWithIcon.action()
        viewBinding.textWithFrame.action()
        viewBinding.textWithoutFrame.action()
        viewBinding.textWithoutFrameAndClear.action()
    }
}