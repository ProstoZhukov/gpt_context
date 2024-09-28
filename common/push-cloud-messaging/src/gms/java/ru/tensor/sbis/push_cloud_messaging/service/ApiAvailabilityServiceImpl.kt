package ru.tensor.sbis.push_cloud_messaging.service

import ru.tensor.sbis.mobile_services_decl.ApiAvailabilityService
import ru.tensor.sbis.mobile_services_google.GoogleApiAvailabilityService

/**
 * Реализация интерфейса проверки доступности сервисов Google Play.
 *
 * @author am.boldinov
 */
internal class ApiAvailabilityServiceImpl : ApiAvailabilityService by GoogleApiAvailabilityService