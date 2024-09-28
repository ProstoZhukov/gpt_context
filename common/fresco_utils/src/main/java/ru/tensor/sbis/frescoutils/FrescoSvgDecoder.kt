package ru.tensor.sbis.frescoutils

import android.content.res.Resources
import android.graphics.Picture
import android.graphics.drawable.PictureDrawable
import com.caverock.androidsvg.SVG
import com.facebook.imageformat.ImageFormat
import com.facebook.imageformat.ImageFormatCheckerUtils
import com.facebook.imagepipeline.common.ImageDecodeOptions
import com.facebook.imagepipeline.decoder.ImageDecoder
import com.facebook.imagepipeline.drawable.DrawableFactory
import com.facebook.imagepipeline.image.CloseableImage
import com.facebook.imagepipeline.image.EncodedImage
import com.facebook.imagepipeline.image.QualityInfo
import timber.log.Timber

/**
 * Сущности fresco для декодирования SVG.
 */
@Suppress("unused")
object FrescoSvgDecoder {

    val SVG_FORMAT = ImageFormat("SVG_FORMAT", "svg")

    private val HEADER = ImageFormatCheckerUtils.asciiBytes("<svg")

    private val POSSIBLE_HEADER_TAGS = arrayOf(ImageFormatCheckerUtils.asciiBytes("<?xml"))

    class SvgFormatChecker : ImageFormat.FormatChecker {

        override fun getHeaderSize() = HEADER.size

        override fun determineFormat(headerBytes: ByteArray, headerSize: Int): ImageFormat? {
            if (headerSize < this.headerSize) {
                return null
            }
            if (ImageFormatCheckerUtils.startsWithPattern(headerBytes, HEADER)) {
                return SVG_FORMAT
            }
            for (possibleHeaderTag in POSSIBLE_HEADER_TAGS) {
                if (ImageFormatCheckerUtils.startsWithPattern(headerBytes, possibleHeaderTag)) {
                    return SVG_FORMAT
                }
            }
            return null
        }
    }

    class SvgDecoder(private val resources: Resources) : ImageDecoder {

        override fun decode(
            encodedImage: EncodedImage,
            length: Int,
            qualityInfo: QualityInfo,
            options: ImageDecodeOptions
        ): CloseableImage? = try {
            val inputStream = encodedImage.inputStream
            val svg = SVG.getFromInputStream(inputStream)
            svg.renderDPI = resources.displayMetrics.densityDpi.toFloat()
            CloseableSvgImage(
                picture = svg.renderToPicture(svg.documentWidth.toInt(), svg.documentHeight.toInt()),
                sizeInBytes = encodedImage.size
            )
        } catch (e: Exception) {
            Timber.e(e)
            null
        }
    }

    class SvgDrawableFactory : DrawableFactory {

        override fun supportsImageType(image: CloseableImage) =
            image is CloseableSvgImage

        override fun createDrawable(image: CloseableImage) =
            PictureDrawable((image as CloseableSvgImage).picture)
    }

    private class CloseableSvgImage(val picture: Picture, private val sizeInBytes: Int) : CloseableImage() {

        private var mClosed = false

        override fun close() {
            mClosed = true
        }

        override fun isClosed() = mClosed

        override fun getSizeInBytes() = sizeInBytes

        override fun getWidth() = picture.width

        override fun getHeight() = picture.height
    }
}