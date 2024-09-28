package ru.tensor.sbis.design.gallery.impl.ui.adapter

import androidx.camera.core.CameraSelector
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.google.common.util.concurrent.ListenableFuture
import kotlinx.coroutines.launch
import ru.tensor.sbis.base_components.adapter.universal.UniversalBindingItem
import ru.tensor.sbis.base_components.adapter.universal.UniversalViewHolder
import ru.tensor.sbis.common.util.safeThrow
import ru.tensor.sbis.design.gallery.databinding.DesignGalleryCameraPreviewItemBinding
import ru.tensor.sbis.design.gallery.impl.ui.GalleryClickHandler
import ru.tensor.sbis.mvi_extension.subscribe
import timber.log.Timber

internal class GalleryCameraPreviewVH(
    private val binding: DesignGalleryCameraPreviewItemBinding,
    clickHandlerVariable: Int,
    clickHandler: GalleryClickHandler,
    private val cameraProviderFuture: ListenableFuture<ProcessCameraProvider>,
    private val lifecycleOwner: LifecycleOwner
) : UniversalViewHolder<UniversalBindingItem>(binding, clickHandlerVariable, clickHandler) {

    private var cameraProvider: ProcessCameraProvider? = null
    private val cameraSelector: CameraSelector by lazy {
        CameraSelector.Builder()
            .apply {
                if (cameraProvider?.hasCamera(CameraSelector.DEFAULT_BACK_CAMERA) == true) {
                    requireLensFacing(CameraSelector.LENS_FACING_BACK)
                } else if (cameraProvider?.hasCamera(CameraSelector.DEFAULT_FRONT_CAMERA) == true) {
                    requireLensFacing(CameraSelector.LENS_FACING_FRONT)
                } else {
                    safeThrow(IllegalArgumentException("No available cameras on the device"))
                }
            }
            .build()
    }
    private val preview: Preview by lazy {
        Preview.Builder().build().also {
            it.setSurfaceProvider(binding.galleryPreviewView.surfaceProvider)
        }
    }

    init {
        log("init")
        lifecycleOwner.lifecycle.subscribe(
            onPause = {
                log("unbind all, onPause")
                try {
                    cameraProvider?.unbindAll()
                } catch (e: Exception) {
                    safeThrow("catch exception in unbind", e)
                }
            }
        )
        cameraProviderFuture.addListener(
            {
                try {
                    cameraProvider = cameraProviderFuture.get()
                    val lifecycleState = lifecycleOwner.lifecycle.currentState
                    lifecycleOwner.lifecycleScope.launch {
                        lifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.RESUMED) {
                            log("bind to lifecycle, state - $lifecycleState")
                            try {
                                cameraProvider?.bindToLifecycle(lifecycleOwner, cameraSelector, preview)
                            } catch (e: Exception) {
                                safeThrow("catch exception in bindToLifecycle", e)
                            }
                        }
                    }
                } catch (e: Exception) {
                    safeThrow("catch exception", e)
                }
            },
            ContextCompat.getMainExecutor(binding.root.context)
        )
    }

    private fun log(message: String) = Timber.d("Log gallery camera viewHolder - $message")
}