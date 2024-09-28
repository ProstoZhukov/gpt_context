package ru.tensor.sbis.sale_point_qr_binder.internal.screens

import android.content.Context
import android.content.Intent
import android.os.Bundle
import ru.tensor.sbis.android_ext_decl.args.KeySpec
import ru.tensor.sbis.android_ext_decl.args.getKeySpec
import ru.tensor.sbis.android_ext_decl.args.nonNull
import ru.tensor.sbis.android_ext_decl.args.parcelable
import ru.tensor.sbis.android_ext_decl.args.putKeySpec
import ru.tensor.sbis.base_components.BaseActivity
import ru.tensor.sbis.sale_point_qr_binder.databinding.SpqrbActivitySalePointQrBinderBinding
import ru.tensor.sbis.sale_point_qr_binder.internal.screens.binder.SalePointQrBinderFragment
import ru.tensor.sbis.sale_point_qr_binder.internal.screens.binder.model.SalePointBindInfo

/**
 * Реализация [Activity] для выполнения привязки QR-кода к ТП.
 *
 * @author kv.martyshenko
 */
internal class SalePointQrBinderActivity : BaseActivity() {

    private val salePointBindInfo by lazy {
        intent.getKeySpec(salePointBindSpec)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding = SpqrbActivitySalePointQrBinderBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (savedInstanceState == null) {
            supportFragmentManager
                .beginTransaction()
                .add(
                    binding.spqrbContentContainer.id,
                    SalePointQrBinderFragment.newInstance(salePointBindInfo)
                )
                .commitNow()
        }
    }

    companion object {
        private val salePointBindSpec = KeySpec.parcelable<SalePointBindInfo>("sale_point_bind_info").nonNull()

        /**
         * Метод для получения [Intent] на экран привязки QR-кода к ТП.
         *
         * @param context
         */
        fun createIntent(
            context: Context,
            bindInfo: SalePointBindInfo
        ): Intent {
            return Intent(context, SalePointQrBinderActivity::class.java).apply {
                putKeySpec(salePointBindSpec, bindInfo)
            }
        }

    }

}