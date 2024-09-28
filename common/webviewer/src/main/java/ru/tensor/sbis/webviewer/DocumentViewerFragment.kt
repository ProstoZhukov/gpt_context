package ru.tensor.sbis.webviewer

import android.annotation.SuppressLint
import android.app.Activity
import android.content.ComponentCallbacks2
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.content.res.Configuration
import android.graphics.Bitmap
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.IBinder
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebViewClient
import android.widget.ProgressBar
import androidx.annotation.IdRes
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ru.tensor.sbis.base_components.BaseFragment
import ru.tensor.sbis.base_components.fragment.FragmentBackPress
import ru.tensor.sbis.common.util.FileUtil
import ru.tensor.sbis.design.stubview.StubView
import ru.tensor.sbis.design.stubview.StubViewCase
import ru.tensor.sbis.event_bus.EventBus
import ru.tensor.sbis.storage_utils.openFromInternalStorage
import ru.tensor.sbis.webviewer.DocumentWebView.LoadFileByLinkInterface
import ru.tensor.sbis.webviewer.FileLoadingService.FileLoadingBinder
import ru.tensor.sbis.webviewer.WebViewerPlugin.webViewerComponent
import ru.tensor.sbis.webviewer.data.FileLoadedEvent
import ru.tensor.sbis.webviewer.utils.GuestSidDetector
import ru.tensor.sbis.webviewer.utils.TitleLoadedListener
import timber.log.Timber
import java.io.File

/**
 * Фрагмент для открытия документа через WebView
 *
 * @author ma.kolpakov
 */
open class DocumentViewerFragment<W : DocumentWebView?> : BaseFragment(), FragmentBackPress, DocumentWebView.Listener,
    LoadFileByLinkInterface, WebViewRendererDeathListener {
    private enum class PageState {
        STARTED,
        FINISHED,
        ERROR
    }

    @JvmField
    protected var mWebView: W? = null
    private var stubView: StubView? = null
    private var mPageLoadingProgress: ProgressBar? = null
    private var mFileLoadingDialog: AlertDialog? = null
    private var mActionPerforming = false
    private var mPageState: PageState? = null
    private val mLoadingUrls: MutableList<String> = ArrayList()
    private var mConnectedToFileLoadingService = false

    @IdRes
    private var pageLoadingProgressId = R.id.docwebviewer_page_loading_progress

    @IdRes
    private var stubViewId = R.id.docwebviewer_stub

    @IdRes
    private var webViewId = R.id.docwebviewer_document_web_view
    fun setPageLoadingProgressId(@IdRes pageLoadingProgressId: Int) {
        this.pageLoadingProgressId = pageLoadingProgressId
    }

    fun setStubViewId(@IdRes stubViewId: Int) {
        this.stubViewId = stubViewId
    }

    fun setWebViewId(@IdRes webViewId: Int) {
        this.webViewId = webViewId
    }

    private var mDocumentUrl: String? = null
    private var mDocumentId: String? = null
    private var allowReplaceHttpToHttps = true
    private val memoryTrimCallback = WebViewMemoryTrimCallback()
    @JvmField
    protected val mHandler = Handler()
    private val mOnPageFinishedCallback = Runnable {
        if (isPageFinished && isAdded) {
            hideLoadingProgress()
            hideEmptyView()
            showWebView()
            updateViewTitle()
            hideStubView()
        }
    }
    private val mDialogCancelingAction = Runnable {
        closeFileLoadingDialog()
        mActionPerforming = false
    }
    private val mFileLoadingServiceConnection: ServiceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName, service: IBinder) {
            if (isAdded) {
                updateLoadingUrls((service as FileLoadingBinder).loadingUrls)
                if (mConnectedToFileLoadingService) {
                    unbindFromFileLoadingService()
                }
            }
        }

        override fun onServiceDisconnected(name: ComponentName) {
            Timber.d("File loading service disconnected (%s)", name)
        }
    }

    /**
     * Интерфейс для взаимодействия с вызывающим компонентом
     */
    interface DocumentViewerHolder {
        /**
         * Заголовок страницы загружен
         *
         * @param title заголовок веб страницы
         */
        fun onPageTitleLoaded(title: String?)
    }

    /**
     * Обновление списка ссылок на загрузку
     * Если в актуальном списке, полученном извне, отсутствуют ссылки из [.mLoadingUrls],
     * значит загрузка этих файлов уже завершена
     *
     * @param loadingUrls актуальный список ссылок для загрузки
     */
    private fun updateLoadingUrls(loadingUrls: ArrayList<String>) {
        val targetList = mLoadingUrls.iterator()
        while (targetList.hasNext()) {
            val value = targetList.next()
            if (!loadingUrls.contains(value)) {
                targetList.remove()
            }
        }
    }

    private fun bindFromFileLoadingService() {
        mConnectedToFileLoadingService = requireContext().bindService(
            Intent(context, FileLoadingService::class.java),
            mFileLoadingServiceConnection,
            Context.BIND_AUTO_CREATE
        )
    }

    private fun unbindFromFileLoadingService() {
        requireContext().unbindService(mFileLoadingServiceConnection)
        mConnectedToFileLoadingService = false
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        context.registerComponentCallbacks(memoryTrimCallback)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        retainInstance = true
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val mainView = inflater.inflate(layoutRes, container, false)
        mPageLoadingProgress = mainView.findViewById(pageLoadingProgressId)
        stubView = mainView.findViewById(stubViewId)
        initWebView(inflateWebView(mainView))
        return mainView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewLifecycleOwner.lifecycleScope.launch {
            EventBus.subscribe(FileLoadedEvent::class.java) {
                launch(Dispatchers.Main) {
                    onEventMainThread(it)
                }
            }
        }
    }

    protected open fun inflateWebView(root: View): W {
        return root.findViewById(webViewId)
    }

    private fun initStubView(errorCode: Int) {
        val map: MutableMap<Int, Function0<Unit>> = HashMap()
        map[ru.tensor.sbis.design.stubview.R.string.design_stub_view_no_connection_details_clickable] = {
            if (mWebView != null) {
                mPageState = PageState.STARTED
                showLoadingProgress()
                hideStubView()
                mWebView!!.reload()
            }
            Unit
        }
        if (errorCode == WebViewClient.ERROR_CONNECT || errorCode == WebViewClient.ERROR_HOST_LOOKUP) {
            stubView!!.setContent(StubViewCase.NO_CONNECTION.getContent(map))
        } else {
            stubView!!.setContent(StubViewCase.SBIS_ERROR.getContent(map))
        }
        showStubView()
    }

    @get:LayoutRes
    protected open val layoutRes: Int
        get() = R.layout.docwebviewer_viewer_fragment

    fun setUrl(documentUrl: String, documentId: String?, allowReplaceHttpToHttps: Boolean = true) {
        if (activity != null && !TextUtils.isEmpty(documentUrl)) {
            mDocumentUrl = documentUrl
            mDocumentId = documentId
            this.allowReplaceHttpToHttps = allowReplaceHttpToHttps
            loadDocumentUrl()
        }
    }

    override fun onBackPressed(): Boolean {
        val view = mWebView ?: return false
        val handled = !view.onBackPressed()
        if (handled && view.isSupportMultipleWindows) {
            onPageFinished()
        }
        return handled
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (mWebView != null) mWebView!!.onActivityResult(requestCode, resultCode, data)
    }

    override fun onResume() {
        super.onResume()
        if (mWebView != null) {
            mWebView!!.setFileLoadingListener(this)
            mWebView!!.setTitleLoadedListener(object : TitleLoadedListener {
                override fun onTitleLoaded() {
                    updateViewTitle()
                }
            })
            mWebView!!.onResume()
        }
        if (mFileLoadingDialog != null && !mFileLoadingDialog!!.isShowing) {
            mFileLoadingDialog!!.show()
        }
        if (isBindFileLoadingServiceNeeded) bindFromFileLoadingService()
    }

    override fun onPause() {
        if (mFileLoadingDialog != null && mFileLoadingDialog!!.isShowing) {
            mFileLoadingDialog!!.dismiss()
        }
        if (mWebView != null) {
            mWebView!!.setFileLoadingListener(null)
            mWebView!!.setTitleLoadedListener(null)
            mWebView!!.onPause()
        }
        if (mConnectedToFileLoadingService) {
            unbindFromFileLoadingService()
        }
        super.onPause()
    }

    override fun onDestroy() {
        if (mWebView != null) {
            mWebView!!.setListener(null as Activity?, null)
            mWebView!!.setRendererDeathListener(null)
            mWebView!!.onDestroy()
        }
        mWebView = null
        mFileLoadingDialog = null
        mPageLoadingProgress = null
        super.onDestroy()
    }

    override fun onDetach() {
        super.onDetach()
        requireContext().unregisterComponentCallbacks(memoryTrimCallback)
    }

    override fun onPageStarted(url: String, favicon: Bitmap?) {
        onPageStarted()
    }

    override fun onPageFinished(url: String) {
        onPageFinished()
    }

    override fun onPageError(errorCode: Int, description: String, failingUrl: String) {
        onPageError(errorCode)
    }

    override fun onPageAdded() {
        // сразу выставляем статус не дожидаясь одноименного события по причине долгой доставки WebView.WebViewTransport
        onPageStarted()
    }

    override fun onPageRemoved() {
        if (mPageState == PageState.ERROR) {
            mPageState = PageState.FINISHED
        }
        onPageFinished()
    }

    /**
     * @return true если загрузка страницы завершена
     */
    protected val isPageFinished: Boolean
        get() = mPageState == PageState.FINISHED

    private fun onPageStarted() {
        mPageState = PageState.STARTED
        hideWebView()
        hideEmptyView()
        showLoadingProgress()
    }

    private fun onPageFinished() {
        if (mPageState == PageState.ERROR) {
            return
        }
        mPageState = PageState.FINISHED
        mOnPageFinishedCallback.run()
    }

    private fun onPageError(errorCode: Int) {
        mPageState = PageState.ERROR
        showError()
        if (hasEmptyView()) {
            hideWebView()
            hideLoadingProgress()
            showEmptyView()
        } else {
            hideLoadingProgress()
            hideWebView()
            initStubView(errorCode)
        }
    }

    override fun onDownloadRequested(
        url: String,
        suggestedFilename: String,
        mimeType: String,
        contentLength: Long,
        contentDisposition: String,
        userAgent: String
    ) {
        Timber.d("Download requested for file '%s' from url '%s'", suggestedFilename, url)
    }

    override fun onExternalPageRequest(url: String) {
        Timber.d("External page requested %s", url)
    }

    override fun onFileAlreadyExists(fileName: String, folderPath: String, url: String) {
        if (isAdded) {
            mActionPerforming = true
            mFileLoadingDialog = buildOpenOrLoadFileDialog(
                getString(R.string.webviewer_load_file_again, fileName),
                FileUtil.getFilePath(folderPath, fileName),
                {
                    closeFileLoadingDialog()
                    onFileLoading(fileName, folderPath, url)
                },
                mDialogCancelingAction
            )
            mFileLoadingDialog!!.show()
        }
    }

    protected fun showLoadingProgress() {
        if (mPageLoadingProgress == null) return
        mPageLoadingProgress!!.visibility = View.VISIBLE
        loadingProgressVisible(true)
    }

    protected fun hideLoadingProgress() {
        if (mPageLoadingProgress == null) return
        mPageLoadingProgress!!.visibility = View.GONE
        loadingProgressVisible(false)
    }

    protected fun hideWebView() {
        if (mWebView == null) return
        mWebView!!.visibility = View.INVISIBLE
    }

    protected fun showWebView() {
        if (mWebView == null) return
        mWebView!!.visibility = View.VISIBLE
    }

    protected fun showStubView() {
        if (stubView == null) return
        stubView!!.visibility = View.VISIBLE
    }

    protected fun hideStubView() {
        if (stubView == null) return
        stubView!!.visibility = View.GONE
    }

    private fun loadDocumentUrl() {
        if (mDocumentUrl == null || mWebView == null || mWebView!!.documentUrl != null) {
            return
        }
        val detector = GuestSidDetector()
        detector.clearCookiesIfGuestSidsFound(mDocumentUrl)
        mWebView!!.setShouldAllowReplaceHttpToHttps(allowReplaceHttpToHttps)
        mWebView!!.setDocumentUrl(mDocumentUrl!!)
        mWebView!!.setDocumentId(mDocumentId)
    }

    private fun updateViewTitle() {
        if (mWebView == null) {
            return
        }
        val holder = listener
        holder?.onPageTitleLoaded(mWebView!!.title)
    }

    private val listener: DocumentViewerHolder?
        get() = if (context is DocumentViewerHolder) {
            context as DocumentViewerHolder?
        } else if (parentFragment is DocumentViewerHolder) {
            parentFragment as DocumentViewerHolder?
        } else {
            null
        }

    protected open fun hasEmptyView(): Boolean {
        return false
    }

    protected open fun hideEmptyView() {}
    protected open fun showEmptyView() {}
    protected open fun showError() {}

    /** Уведомить об изменении видимости прогресса загрузки */
    protected open fun loadingProgressVisible(visible: Boolean) = Unit

    /**
     * SelfDocumented
     */
    protected open val isBindFileLoadingServiceNeeded = true

    /**
     * Возвращает новый WebView, которым в иерархии был заменён исходный
     */
    protected open fun recreateWebView(oldWebView: W): W {
        val parent = requireView() as ViewGroup
        parent.removeView(oldWebView)
        val webView = DocumentWebView(requireContext())
        parent.addView(webView, 0, oldWebView!!.layoutParams)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            webView.importantForAutofill = View.IMPORTANT_FOR_AUTOFILL_NO_EXCLUDE_DESCENDANTS
        }
        return webView as W
    }

    private fun closeFileLoadingDialog() {
        if (mFileLoadingDialog != null) {
            mFileLoadingDialog!!.dismiss()
            mFileLoadingDialog = null
        }
    }

    override fun onFileLoading(fileName: String, folderPath: String, url: String) {
        showToast(R.string.webviewer_loading_started)
        if (!isFileLoading(url)) {
            FileLoadingService.loadFile(
                requireContext(),
                url,
                FileUtil.getFilePath(folderPath, fileName),
                null,
                requireActivity().intent,
                FileLoadedEvent.FileAction.OPEN
            )
            mLoadingUrls.add(url)
        }
        mActionPerforming = false
    }

    override fun onUnableToLoadFile() {
        showToast(R.string.webviewer_unable_to_load_file_error)
    }

    override fun hasActiveAction(): Boolean {
        return mActionPerforming
    }

    override fun onWebViewRendererKilled() {
        if (mWebView != null) {
            initWebView(recreateWebView(mWebView!!))
        }
    }

    /**
     * Отображение диалогового окна для выбора дальнейших действий с файлом
     *
     * @param filePath полный путь до файла
     */
    private fun showFileLoadedDialog(filePath: String) {
        if (isAdded) {
            mActionPerforming = true
            mFileLoadingDialog = buildOpenFileDialog(
                filePath,
                mDialogCancelingAction
            )
            mFileLoadingDialog!!.show()
        }
    }

    private fun isFileLoading(url: String): Boolean {
        return mLoadingUrls.contains(url)
    }

    fun onEventMainThread(event: FileLoadedEvent?) {
        if (event != null) {
            processLoadingEvent(event.isSuccess, event.url, event.filePath)
        }
    }

    private fun processLoadingEvent(result: Boolean, url: String, filePath: String) {
        if (TextUtils.isEmpty(url) || !mLoadingUrls.contains(url)) {
            return
        }
        mLoadingUrls.remove(url)
        if (!result) {
            showToast(ru.tensor.sbis.common.R.string.common_file_loading_error)
        } else if (!hasActiveAction()) {
            showFileLoadedDialog(filePath)
        }
    }

    private fun initWebView(webView: W) {
        mWebView = webView
        webView!!.setListener(activity, this)
        webView.setRendererDeathListener(this)
        // Принудительно проставляем размер шрифта, так он не будет зависеть от системных настроек,
        // тогда контролы на веб-страницах будут отображаться верно.
        webView.settings.textZoom = WEB_VIEW_TEXT_ZOOM
        hideWebView()
        val token = webViewerComponent
            .getDependency()
            .loginInterface
            .token
        if (token != null) {
            mWebView!!.setUiCookies(webView.context)
            mWebView!!.setToken(token)
        }
        loadDocumentUrl()
    }

    /**
     * Построение диалогового окна для выбора дальнейших действий с файлом
     * Возможности - отображение или повторная загрузки
     *
     * @param message               текст информационного сообщения, отображаемого в диалоговом окне
     * @param filePath              полный путь до файла
     * @param loadFileAction        действие, выполняемое на вызывающей стороне после принятия решения о скачивании файла
     * @param actionAfterCompletion действие, выполняемое на вызывающей стороне после завершения работы с файлом
     * @return диалоговое окно для отображения
     */
    private fun buildOpenOrLoadFileDialog(
        message: String,
        filePath: String,
        loadFileAction: Runnable,
        actionAfterCompletion: Runnable
    ): AlertDialog {
        return AlertDialog.Builder(requireContext())
            .setMessage(message)
            .setNegativeButton(R.string.webviewer_open_file) { _, _ ->
                actionAfterCompletion.run()
                openFromInternalStorage(filePath, requireContext())
            }
            .setPositiveButton(ru.tensor.sbis.common.R.string.common_load) { _, _ -> loadFileAction.run() }
            .setOnCancelListener { actionAfterCompletion.run() }
            .setCancelable(true)
            .create()
    }

    /**
     * Построение диалогового окна для выбора дальнейших действий с файлом
     * Возможности - открыть файл, отменить
     *
     * @param filePath              полный путь до файла
     * @param actionAfterCompletion действие, выполняемое на вызывающей стороне после завершения работы с файлом
     * @return диалоговое окно для отображения
     */
    private fun buildOpenFileDialog(
        filePath: String,
        actionAfterCompletion: Runnable
    ): AlertDialog {
        return AlertDialog.Builder(requireContext())
            .setMessage(getString(R.string.webviewer_loading_finished, File(filePath).name))
            .setPositiveButton(R.string.webviewer_open_file) { _, _ ->
                actionAfterCompletion.run()
                openFromInternalStorage(filePath, requireContext())
            }
            .setNegativeButton(ru.tensor.sbis.design.R.string.design_cancel_item_label) { _, _ -> actionAfterCompletion.run() }
            .setOnCancelListener { actionAfterCompletion.run() }
            .setCancelable(true)
            .create()
    }

    private inner class WebViewMemoryTrimCallback : ComponentCallbacks2 {
        @SuppressLint("SwitchIntDef")
        override fun onTrimMemory(level: Int) {
            when (level) {
                ComponentCallbacks2.TRIM_MEMORY_RUNNING_CRITICAL, ComponentCallbacks2.TRIM_MEMORY_COMPLETE -> {
                    // Память может закончиться и приложение завершится аварийно - запомним, при каких условиях
                    Timber.e(
                        "WebView.MemWarn: level - %d; state=%s; documentUrl='%s'; documentId=%s, loadingUrls=%s",
                        level,
                        mPageState,
                        mDocumentUrl,
                        mDocumentId,
                        mLoadingUrls
                    )
                    if (mWebView != null) {
                        mWebView!!.clearCache(false)
                    }
                    if (mPageState != PageState.FINISHED && activity is DocumentViewerActivity) {
                        activity!!.recreate()
                    }
                }
                else -> {}
            }
        }

        override fun onConfigurationChanged(newConfig: Configuration) {
            // ignored
        }

        override fun onLowMemory() {
            // ignored
        }
    }

    companion object {
        /**
         * Зум шрифта для WebView. "Из коробки" для системного стандартного размера шрифта равен 100.
         */
        private const val WEB_VIEW_TEXT_ZOOM = 100
    }
}