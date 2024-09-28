package ru.tensor.sbis.media.video

import android.Manifest.permission
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import android.view.WindowInsets
import androidx.annotation.StringRes
import androidx.core.content.ContextCompat
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.util.UnstableApi
import androidx.media3.common.util.Util
import androidx.media3.ui.PlayerView
import ru.tensor.sbis.base_components.TrackingActivity
import ru.tensor.sbis.common.util.FileUriUtil
import ru.tensor.sbis.common.util.PermissionUtil
import ru.tensor.sbis.design.SbisMobileIcon
import ru.tensor.sbis.design.toolbar.Toolbar
import ru.tensor.sbis.design.utils.extentions.ViewPaddings
import ru.tensor.sbis.design.utils.extentions.applyWindowInsets
import ru.tensor.sbis.design.utils.extentions.doOnApplyWindowInsets
import ru.tensor.sbis.media.MediaPlugin
import ru.tensor.sbis.media.R
import ru.tensor.sbis.media.di.MediaComponent
import ru.tensor.sbis.mediaplayer.MediaInfo
import ru.tensor.sbis.mediaplayer.MediaPlayerMediator
import ru.tensor.sbis.mediaplayer.datasource.DelegateMediaSourceFactory
import ru.tensor.sbis.mediaplayer.datasource.MediaSourceFactory
import ru.tensor.sbis.mediaplayer.sbistube.SbisTubeErrorMessageProvider
import ru.tensor.sbis.mediaplayer.sbistube.SbisTubePlayerView
import ru.tensor.sbis.storage.external.SbisExternalStorage
import ru.tensor.sbis.design.R as RDesign
import ru.tensor.sbis.mediaplayer.R as RMediaPlayer

/**
 * Константа, определяющая, следует ли включить проверку разрешения на передачу данных открытым текстом,
 * т.е. незашифрованных, т.е. по протоколу HTTP
 * Подробнее: https://developer.android.com/training/articles/security-config?hl=ru
 */
private const val CHECK_CLEARTEXT_TRAFFIC_PERMITTED = false

@UnstableApi
/**
 * Активность медиаплеера
 * Позволяет проигрывать видео и воспроизводить музыку
 * Основан на ExoPlayer
 *
 * @author sa.nikitin
 */
internal class SbisTubeActivity : TrackingActivity(), PlayerView.ControllerVisibilityListener {

    private var toolbar: Toolbar? = null
    private var playerView: SbisTubePlayerView? = null
    private lateinit var playerMediator: MediaPlayerMediator<SbisTubePlayerView>

    companion object {

        //webm - DASH формат, нужно подкачать и поддержать часть ExoPlayer для проигрывания Dash
        internal val invalidVideoExtensions = arrayOf("avi", "mpg", "webm", "mpeg", "ogv", "vob", "m4v")
        internal val supportedVideoTypes = intArrayOf(C.CONTENT_TYPE_HLS, C.CONTENT_TYPE_OTHER)

        private const val MEDIA_URI_EXTRA_KEY = "media_uri_extra_key"
        private const val MEDIA_NAME_EXTRA_KEY = "media_name_extra_key"
        private const val REPLACE_CLOSE_BY_BACK_ARROW_EXTRA_KEY = "replace_close_by_back_arrow"

        private const val EXTERNAL_STORAGE_PERMISSION_REQUEST_CODE = 1353

        fun newIntent(context: Context, mediaUri: Uri, mediaName: String?, replaceCloseByBackArrow: Boolean): Intent =
            Intent(context, SbisTubeActivity::class.java).also { intent ->
                intent.putExtra(MEDIA_URI_EXTRA_KEY, mediaUri)
                if (mediaName != null) {
                    intent.putExtra(MEDIA_NAME_EXTRA_KEY, mediaName)
                }
                intent.putExtra(REPLACE_CLOSE_BY_BACK_ARROW_EXTRA_KEY, replaceCloseByBackArrow)
                if (context !is Activity) {
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                }
            }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        playerMediator = MediaPlayerMediator(
            context = this,
            mediaSourceFactory = createMediaSourceFactory(),
            errorMessageProvider = SbisTubeErrorMessageProvider(this@SbisTubeActivity),
            hasBackgroundPlay = true
        )
        lifecycle.addObserver(playerMediator)

        setContentView(R.layout.activity_sbis_tube)
        initToolbar(intent.getBooleanExtra(REPLACE_CLOSE_BY_BACK_ARROW_EXTRA_KEY, false))
        initMediaPlayerView()

        if (savedInstanceState != null) {
            playerMediator.restoreState(savedInstanceState)
        } else {
            processIntentUri()
        }
    }

    private fun initToolbar(replaceCloseByBackArrow: Boolean) {
        toolbar = findViewById(R.id.toolbar)
        toolbar!!.apply {
            setMainColor(ContextCompat.getColor(this@SbisTubeActivity, RDesign.color.palette_color_transparent))
            val whiteColor = ContextCompat.getColor(this@SbisTubeActivity, RDesign.color.palette_color_white1)
            leftIcon.setTextColor(whiteColor)
            leftText.setTextColor(whiteColor)
            leftIcon.text =
                if (replaceCloseByBackArrow) {
                    SbisMobileIcon.Icon.smi_arrowBack.character.toString()
                } else {
                    SbisMobileIcon.Icon.smi_navBarClose.character.toString()
                }
            leftPanel.setOnClickListener { onBackPressedDispatcher.onBackPressed() }
            divider.visibility = View.GONE
            doOnApplyWindowInsets { toolbar: View, windowInsets: WindowInsets, viewPaddings: ViewPaddings ->
                toolbar.applyWindowInsets(windowInsets, viewPaddings)
            }
        }
    }

    private fun initMediaPlayerView() {
        playerView = findViewById<SbisTubePlayerView>(RMediaPlayer.id.player_view).apply {
            playerMediator.setPlayerView(this)
            setControllerVisibilityListener(this@SbisTubeActivity)
        }
        playerView!!.showController()
    }

    private fun createMediaSourceFactory(): MediaSourceFactory {
        val mediaComponent: MediaComponent = MediaComponent.fromContext(this)
        return DelegateMediaSourceFactory(
            this,
            mediaComponent.dependency.loginInterface,
            mediaComponent.dependency.apiService(),
            mediaComponent.sbisInternalStorage.mediaCacheDir()
        )
    }

    public override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        processIntentUri()
    }

    //region Controls visibility
    override fun updateFullscreen() {
        //do nothing
    }

    override fun onVisibilityChanged(visibility: Int) {
        playerView?.overlayFrameLayout?.visibility = visibility
        toolbar?.visibility = visibility
        if (visibility == View.VISIBLE) {
            showSystemUI()
        } else {
            hideSystemUI()
        }
    }

    private fun hideSystemUI() {
        window.decorView.systemUiVisibility =
            (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_FULLSCREEN
                or View.SYSTEM_UI_FLAG_IMMERSIVE)
    }

    private fun showSystemUI() {
        window.decorView.systemUiVisibility =
            (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN)
    }
    //endregion

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == EXTERNAL_STORAGE_PERMISSION_REQUEST_CODE) {
            if (PermissionUtil.verifyPermissions(grantResults)) {
                processIntentUri()
            } else {
                playerView!!.setCustomErrorMessage(
                    getString(RMediaPlayer.string.media_sbis_tube_denied_reading_external_storage)
                )
            }
        }
    }

    override fun dispatchKeyEvent(event: KeyEvent): Boolean =
        playerView!!.dispatchKeyEvent(event) || super.dispatchKeyEvent(event)

    private fun processIntentUri() {
        val uri: Uri? = intent.getParcelableExtra(MEDIA_URI_EXTRA_KEY) as? Uri
        if (uri == null) {
            finishOnError(RMediaPlayer.string.media_sbis_tube_unknown_error)
            return
        }
        if (CHECK_CLEARTEXT_TRAFFIC_PERMITTED && !Util.checkCleartextTrafficPermitted(MediaItem.fromUri(uri))) {
            finishOnError(RMediaPlayer.string.media_sbis_tube_cleartext_traffic_not_permitted_error)
            return
        }
        val path: String? = FileUriUtil.getPath(this, uri)
        val externalStorage: SbisExternalStorage = MediaPlugin.externalStorageProvider.get().externalStorage
        val externalStorageDirPath: String = externalStorage.getExternalStorageDir().dir.absolutePath
        val externalStoragePermission: String =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                permission.READ_MEDIA_VIDEO
            } else {
                permission.READ_EXTERNAL_STORAGE
            }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
            path != null &&
            path.contains(externalStorageDirPath) &&
            checkSelfPermission(externalStoragePermission) != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissions(arrayOf(externalStoragePermission), EXTERNAL_STORAGE_PERMISSION_REQUEST_CODE)
            return
        }

        val mediaName: String? = intent.getStringExtra(MEDIA_NAME_EXTRA_KEY)
        toolbar!!.leftText.text = mediaName?.substringBeforeLast('.', mediaName)
        playerMediator.setMediaInfo(MediaInfo(uri))
    }

    private fun finishOnError(@StringRes errorMessageResId: Int) {
        showToast(errorMessageResId)
        finish()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        playerMediator.saveState(outState)
    }

    override fun onDestroy() {
        super.onDestroy()
        toolbar = null
        playerView = null
        playerMediator.release()
    }
}