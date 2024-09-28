package ru.tensor.sbis.red_button.ui.stub

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.databinding.DataBindingUtil
import ru.tensor.sbis.common.util.ContextReplacer
import ru.tensor.sbis.entrypoint_guard.EntryPointGuard
import ru.tensor.sbis.red_button.R
import ru.tensor.sbis.red_button.RedButtonPlugin
import ru.tensor.sbis.red_button.data.RedButtonStubType
import ru.tensor.sbis.red_button.databinding.RedButtonActivityStubBinding
import ru.tensor.sbis.red_button.ui.stub.di.DaggerRedButtonStubComponent
import javax.inject.Inject

/**
 * Активность для отображения заглушки "Красной Кнопки" сигнализирующей о том, что требуется перезагрузить приложение
 *
 * @author ra.stepanov
 */
class RedButtonStubActivity : AppCompatActivity(), EntryPointGuard.LegacyEntryPoint {

    /** Вью модель */
    @Inject
    lateinit var viewModel: RedButtonStubViewModel

    /**@SelfDocumented */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        inject()
        //Извлечение из [Intent] типа заглушки и установка его в вью-модель
        viewModel.refreshStubContent(intent.getSerializableExtra(RED_BUTTON_STUB_TYPE_KEY) as RedButtonStubType)

        DataBindingUtil.setContentView<RedButtonActivityStubBinding>(this, R.layout.red_button_activity_stub)
            .also { it.viewModel = viewModel }

        window.let {
            WindowInsetsControllerCompat(it, it.decorView)
        }.run {
            if (resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
                systemBarsBehavior =
                    WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
                hide(WindowInsetsCompat.Type.statusBars())
            } else {
                systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_BARS_BY_TOUCH
                show(WindowInsetsCompat.Type.statusBars())
            }
        }
    }

    /**@SelfDocumented */
    override fun attachBaseContext(base: Context?) {
        EntryPointGuard.activityAssistant.interceptAttachBaseContextLegacy(this, ContextReplacer.replace(base)) {
            super.attachBaseContext(it)
        }
    }

    /**@SelfDocumented */
    override fun onBackPressed() {
        startActivity(Intent(Intent.ACTION_MAIN).apply {
            addCategory(Intent.CATEGORY_HOME)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        })
    }

    /** @SelfDocumented */
    private fun inject() {
        DaggerRedButtonStubComponent.factory()
            .create(RedButtonPlugin.redButtonComponent, this)
            .inject(this)
    }

    companion object {

        /** Ключ в [Intent] для доступа к типу заглушки */
        const val RED_BUTTON_STUB_TYPE_KEY = "RED_BUTTON_STUB_TYPE_KEY"

        /**@SelfDocumented */
        @JvmStatic
        fun openStub(activity: Activity, stubType: RedButtonStubType) {
            val intent = Intent(activity, RedButtonStubActivity::class.java)
            intent.putExtra(RED_BUTTON_STUB_TYPE_KEY, stubType)
            activity.startActivity(intent)
            activity.finish()
        }
    }
}