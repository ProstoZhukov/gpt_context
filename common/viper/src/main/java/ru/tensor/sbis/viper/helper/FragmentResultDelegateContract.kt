package ru.tensor.sbis.viper.helper

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import ru.tensor.sbis.viper.helper.FragmentResultDelegateContract.Presenter

/**
 * Интерфейс для взаимодействия с экранами, возвращающими результат
 */
interface FragmentResultDelegateContract {

    interface View {

        /**
         * Установить слушателя результатов работы экранов
         */
        fun Fragment.setResultListener(callback: Presenter, resultKeyList: List<String>)
    }

    interface Presenter {

        /**
         * Обработка результата работы фрагмента
         */
        fun onFragmentResult(requestKey: String, bundle: Bundle)
    }
}

/**
 * Имплементация [FragmentResultDelegateContract]
 */
class FragmentResultDelegateImpl : FragmentResultDelegateContract.View {

    override fun Fragment.setResultListener(callback: Presenter, resultKeyList: List<String>) {
        resultKeyList.forEach {
            setFragmentResultListener(it) { requestKey: String, bundle: Bundle ->
                callback.onFragmentResult(requestKey, bundle)
            }
        }
    }

}