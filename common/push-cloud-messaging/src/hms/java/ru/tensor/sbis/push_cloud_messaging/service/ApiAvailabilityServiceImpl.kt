package ru.tensor.sbis.push_cloud_messaging.service

import ru.tensor.sbis.mobile_services_decl.ApiAvailabilityService
import ru.tensor.sbis.mobile_services_huawei.HuaweiApiAvailabilityService

/**
 * Реализация интерфейса проверки доступности сервисов Huawei AppGallery Connect.
 *
 * @author am.boldinov
 */
internal class ApiAvailabilityServiceImpl : ApiAvailabilityService by HuaweiApiAvailabilityService