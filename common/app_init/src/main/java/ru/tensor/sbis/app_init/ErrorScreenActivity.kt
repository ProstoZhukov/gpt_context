package ru.tensor.sbis.app_init

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import ru.tensor.sbis.design.stubview.ImageStubContent
import ru.tensor.sbis.design.stubview.StubView
import ru.tensor.sbis.design.stubview.StubViewImageType
import ru.tensor.sbis.design.utils.getDataFromAttrOrNull

private const val ERROR_TEXT_KEY = "ERROR_TYPE_KEY"
private const val DETAILS_TEXT_KEY = "DETAILS_TEXT_KEY"
private const val STUB_IMAGE_KEY = "STUB_IMAGE_KEY"

/**
 * Экран отображающий только заглушку с картинкой, тектом ошибки и кнопкой перезагрузки приложения.
 *
 * @author du.bykov
 */
class ErrorScreenActivity : AppCompatActivity() {

    companion object {
        /**
         * Создать экран для оторажения сообщения о нехватке физ. памяти на устройстве.
         */
        fun createIntentWithMemoryErrorStub(packageContext: Context): Intent {
            val intent = Intent(packageContext, ErrorScreenActivity::class.java)
            intent.putExtra(
                ERROR_TEXT_KEY,
                packageContext.resources.getString(R.string.app_init_no_memory_title_msg)
            )
            intent.putExtra(
                DETAILS_TEXT_KEY,
                packageContext.resources.getString(R.string.app_init_no_memory_details_msg)
            )
            intent.putExtra(STUB_IMAGE_KEY, StubViewImageType.ERROR.name)
            return intent
        }

        /**
         * Создать экран для оторажения сообщения о ошибке с текстом [message].
         */
        fun createIntentWithRestartSub(packageContext: Context, message: String): Intent {
            val intent = Intent(packageContext, ErrorScreenActivity::class.java)
            intent.putExtra(
                ERROR_TEXT_KEY,
                message
            )
            intent.putExtra(STUB_IMAGE_KEY, StubViewImageType.RESTART_APP.name)
            return intent
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.error_screen_activity)

        val stubContent = createResourceImageStubContent()
        findViewById<StubView>(R.id.stub_view)?.setContent(stubContent)
        findViewById<View>(R.id.no_memory_button_close)?.setOnClickListener { finish() }
    }

    /**
     * Два вариант заглушки, оба с картинкой и заголовком, но один еще и с детальным описанием ошибки, если его передали
     * в Extra в intent.
     */
    private fun createResourceImageStubContent(): ImageStubContent {
        val details = intent.getStringExtra(DETAILS_TEXT_KEY)
        val imageTypeName = intent.getStringExtra(STUB_IMAGE_KEY) ?: StubViewImageType.ERROR.name
        return if (details.isNullOrBlank()) ImageStubContent(
            imageType = StubViewImageType.valueOf(imageTypeName),
            message = intent.getStringExtra(ERROR_TEXT_KEY),
            details = null
        )
        else ImageStubContent(
            imageType = StubViewImageType.valueOf(imageTypeName),
            message = intent.getStringExtra(ERROR_TEXT_KEY),
            details = details
        )
    }

    private fun setTheme() {
        var themeId = this.getDataFromAttrOrNull(R.attr.BaseAppComponentsErrorScreen, false)
        if (themeId == null) {
            themeId = R.style.BaseAppComponentsErrorScreenTheme_Light
        }
        setTheme(themeId)
    }
}