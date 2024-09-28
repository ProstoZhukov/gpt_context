package ru.tensor.sbis.frescoutils

import android.net.Uri
import com.facebook.drawee.backends.pipeline.Fresco
import com.facebook.imagepipeline.request.ImageRequest
import ru.tensor.sbis.common.util.UrlUtils
import ru.tensor.sbis.verification_decl.account.UserAccount

/**
 * Утилиты для взаимодействия с кэшем [Fresco]
 *
 * @author sa.nikitin
 */
object FrescoCache {

    /**
     * Закэшировано ли изображение по [uri] в кэше памяти или диска
     */
    fun isCached(uri: Uri?): Boolean =
        uri
            ?.let {
                val canonicalUri: Uri = FrescoHostIndependentKeyFactory.canonicalUri(it)
                isCachedInMemory(canonicalUri) || isCachedInStorage(canonicalUri)
            }
            ?: false

    /**
     * Выгрузить из кэша памяти и диска изображение по [uri]
     */
    fun evict(uri: Uri) {
        Fresco.getImagePipeline().evictFromCache(uri)
    }

    /**
     * Выгрузить из кэша памяти и диска фото текущего аккаунта
     */
    fun evictCurrentAccountPhoto(currentAccount: UserAccount) {
        evict(Uri.parse(UrlUtils.getImageUrl(currentAccount)))
        evictMyProfilePhoto()
    }

    /**
     * Выгрузить из кэша памяти и диска фото "моего" профиля
     */
    fun evictMyProfilePhoto() {
        //Ссылка на фотографию профиля из любого аккаунта - одинаковая,
        //поэтому удаляем из кеша изображение по этой ссылке
        evict(Uri.parse(UrlUtils.getMyProfilePhotoUrl()))
    }

    /** @SelfDocumented */
    private fun isCachedInMemory(uri: Uri): Boolean = Fresco.getImagePipeline().isInBitmapMemoryCache(uri)

    /** @SelfDocumented */
    private fun isCachedInStorage(
        uri: Uri,
        cacheChoice: ImageRequest.CacheChoice = ImageRequest.CacheChoice.DEFAULT
    ): Boolean =
        Fresco.getImagePipeline().isInDiskCacheSync(uri, cacheChoice)
}