package ru.tensor.sbis.mobile_services_google

import android.app.Activity
import android.content.Context
import androidx.annotation.UiThread
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import ru.tensor.sbis.mobile_services_decl.ApiAvailabilityService
import ru.tensor.sbis.mobile_services_decl.ServiceConnectionResult
import timber.log.Timber

/**
 * Реализация сервиса проверки доступности сервисов Google Play.
 *
 * @author am.boldinov
 */
object GoogleApiAvailabilityService : ApiAvailabilityService {

    private const val ERROR_DIALOG_CODE = 541230

    private val notAvailableConnectionResults: Set<ServiceConnectionResult>
        get() = setOf(
            ServiceConnectionResult.SERVICE_DISABLED,
            ServiceConnectionResult.SERVICE_MISSING,
            ServiceConnectionResult.INTERNAL_ERROR
        )

    override fun isServicesAvailable(context: Context): Boolean {
        return notAvailableConnectionResults.contains(checkServicesAvailability(context)).not()
    }

    override fun checkServicesAvailability(context: Context): ServiceConnectionResult {
        val result = try {
            GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(context)
        } catch (e: Exception) {
            Timber.e(e)
            ConnectionResult.INTERNAL_ERROR
        }
        return result.toServiceConnectionResult()
    }

    @UiThread
    override fun showServicesUnavailableDialog(activity: Activity) {
        try {
            GoogleApiAvailability.getInstance().let {
                it.showErrorDialogFragment(activity, it.isGooglePlayServicesAvailable(activity), ERROR_DIALOG_CODE)
            }
        } catch (e: Exception) {
            Timber.e(e)
        }
    }

    private fun Int.toServiceConnectionResult(): ServiceConnectionResult {
        return when (this) {
            ConnectionResult.SUCCESS -> ServiceConnectionResult.SUCCESS
            ConnectionResult.NETWORK_ERROR -> ServiceConnectionResult.NETWORK_ERROR
            ConnectionResult.INTERNAL_ERROR -> ServiceConnectionResult.INTERNAL_ERROR
            ConnectionResult.SERVICE_VERSION_UPDATE_REQUIRED -> ServiceConnectionResult.SERVICE_VERSION_UPDATE_REQUIRED
            ConnectionResult.SERVICE_DISABLED -> ServiceConnectionResult.SERVICE_DISABLED
            ConnectionResult.SERVICE_MISSING -> ServiceConnectionResult.SERVICE_MISSING
            else -> ServiceConnectionResult.INTERNAL_ERROR
        }
    }
}

