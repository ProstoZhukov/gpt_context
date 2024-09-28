package ru.tensor.sbis.appdesign.input_view

import android.os.Bundle
import android.view.inputmethod.EditorInfo
import androidx.appcompat.app.AppCompatActivity
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.SerialDisposable
import ru.tensor.sbis.appdesign.databinding.ActivityInputViewBinding
import ru.tensor.sbis.appdesign.extensions.showToast
import ru.tensor.sbis.appdesign.R
import ru.tensor.sbis.design.view.input.base.ValidationStatus
import ru.tensor.sbis.design.view.input.money.MoneyInputViewFraction
import java.util.concurrent.TimeUnit

/**
 * Демо экран для полей ввода
 * http://axure.tensor.ru/MobileStandart8/#p=%D0%BF%D0%BE%D0%BB%D1%8F_%D0%B2%D0%B2%D0%BE%D0%B4%D0%B0_v2&g=1
 *
 * @author aa.sviridov
 */
class InputViewDemoActivity : AppCompatActivity() {

    private lateinit var passwordValidationDisposable: SerialDisposable

    private lateinit var binding: ActivityInputViewBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        passwordValidationDisposable = SerialDisposable()

        binding = ActivityInputViewBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.inputViewText.onEditorActionListener = { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_GO)
                showToast(getString(R.string.input_view_single_line_simple_action_go))
            true
        }

        binding.inputViewTextReadOnly.setOnCheckedChangeListener { _, isChecked ->
            binding.inputViewText.readOnly = isChecked
        }

        binding.inputViewTextIsClear.setOnCheckedChangeListener { _, isChecked ->
            binding.inputViewText.isClearVisible = isChecked
        }

        binding.inputViewTextLength.onLinkClickListener = {
            val newMaxLength = try {
                binding.inputViewTextLength.value.toInt()
            } catch (e: Exception) {
                binding.inputViewTextLength.value = "0"
                0
            }
            binding.inputViewText.maxLength = newMaxLength
            showToast(newMaxLength.toString())
        }

        binding.inputViewPassword.onLinkClickListener = {
            showToast(getString(R.string.input_view_password_link_toast))
        }

        binding.inputViewPasswordValidationDefault.setOnClickListener {
            passwordValidationProgress(0L) { ValidationStatus.Default("") }
        }

        binding.inputViewPasswordValidationError.setOnClickListener {
            passwordValidationProgress(500L) {
                ValidationStatus.Error(getString(R.string.input_view_password_validation_error_message))
            }
        }

        binding.inputViewPasswordValidationWarning.setOnClickListener {
            passwordValidationProgress(1000L) {
                ValidationStatus.Warning(getString(R.string.input_view_password_validation_warning_message))
            }
        }

        binding.inputViewPasswordValidationSuccess.setOnClickListener {
            passwordValidationProgress(1500L) {
                ValidationStatus.Success(getString(R.string.input_view_password_validation_success_message))
            }
        }

        binding.inputViewMoneyDecorated.setOnCheckedChangeListener { _, isChecked ->
            binding.inputViewMoney.isDecorated = isChecked
        }

        binding.inputViewMoneyZeroFraction.setOnClickListener {
            binding.inputViewMoneyOneFraction.isChecked = false
            binding.inputViewMoneyTwoFraction.isChecked = false
        }
        binding.inputViewMoneyZeroFraction.setOnCheckedChangeListener { _, isChecked ->
            if (!isChecked) return@setOnCheckedChangeListener
            binding.inputViewMoney.fraction = MoneyInputViewFraction.OFF
        }

        binding.inputViewMoneyOneFraction.setOnClickListener {
            binding.inputViewMoneyZeroFraction.isChecked = false
            binding.inputViewMoneyTwoFraction.isChecked = false
        }
        binding.inputViewMoneyOneFraction.setOnCheckedChangeListener { _, isChecked ->
            if (!isChecked) return@setOnCheckedChangeListener
            binding.inputViewMoney.fraction = MoneyInputViewFraction.ONLY_TENS
        }

        binding.inputViewMoneyTwoFraction.setOnClickListener {
            binding.inputViewMoneyZeroFraction.isChecked = false
            binding.inputViewMoneyOneFraction.isChecked = false
        }
        binding.inputViewMoneyTwoFraction.setOnCheckedChangeListener { _, isChecked ->
            if (!isChecked) return@setOnCheckedChangeListener
            binding.inputViewMoney.fraction = MoneyInputViewFraction.ON
        }

        binding.inputViewValueSelection.onListIconClickListener = {
            showToast(getString(R.string.input_view_value_selection_toast))
        }
    }

    override fun onDestroy() {
        passwordValidationDisposable.dispose()
        super.onDestroy()
    }

    private fun passwordValidationProgress(milliseconds: Long, getStatus: () -> ValidationStatus) {
        if (milliseconds > 0L) {
            passwordValidationDisposable.set(
                Single.timer(milliseconds, TimeUnit.MILLISECONDS)
                    .map { getStatus() }
                    .observeOn(AndroidSchedulers.mainThread())
                    .doOnSubscribe { binding.inputViewPassword.isProgressVisible = true }
                    .doFinally { binding.inputViewPassword.isProgressVisible = false }
                    .subscribe { it, _ -> binding.inputViewPassword.validationStatus = it }
            )
        } else {
            passwordValidationDisposable.set(null)
            binding.inputViewPassword.validationStatus = getStatus()
        }
    }
}