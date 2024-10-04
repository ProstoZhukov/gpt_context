package ru.tensor.sbis.appdesign.pincode

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import ru.tensor.sbis.appdesign.R
import ru.tensor.sbis.appdesign.databinding.ActivityPinCodeHostBinding
import ru.tensor.sbis.appdesign.extensions.showToast
import ru.tensor.sbis.pin_code.decl.*

class PinCodeHostActivity : AppCompatActivity() {

    private val pinCodeFeature: PinCodeFeature<ResultData> by createLazyPinCodeFeature(this) {
        object : PinCodeRepository<ResultData> {
            override fun onCodeEntered(digits: String): ResultData {
                //запрос в облако или какое-то локальное действие
                Thread.sleep(1000)
                return ResultData("Hello")
            }

            override fun onRetry() {
                //запрос в облако
                Thread.sleep(1000)
            }

            override fun needCleanCode(error: Throwable): Boolean {
                //проверить тип возникшего исключения и если помимо отображения сообщения необходимо очищать поле ввода, то вернуть true
                return true
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding: ActivityPinCodeHostBinding = DataBindingUtil.setContentView(this, R.layout.activity_pin_code_host)

        pinCodeFeature.onRequestCheckCodeResult.observe(this, {
            //операция проверки кода прошла успешно, компонент был автоматически закрыт
            showToast(it.data.text)
        })

        pinCodeFeature.onCanceled.observe(this, {
            //пользователь самостоятельно закрыл окно ввода пин-кода
        })

        binding.btnOpenInActivity.setOnClickListener {
            pinCodeFeature.show(this, PinCodeUseCase.Create("Введите пин-код"))
        }

        binding.btnOpenInFragment.setOnClickListener {
            supportFragmentManager
                .beginTransaction()
                .replace(R.id.fragment_container, PinCodeHostFragment())
                .commit()
        }
    }
}

data class ResultData(val text: String)