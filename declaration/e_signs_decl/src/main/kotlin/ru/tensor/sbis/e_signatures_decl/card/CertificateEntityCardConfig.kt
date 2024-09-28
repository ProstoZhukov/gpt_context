package ru.tensor.sbis.e_signatures_decl.card

import android.os.Parcelable
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize

/**
 * Конфигурации запуска карточек сертификатов и заявок по сценарию использования.
 *
 * @author vv.malyhin
 */
sealed class CertificateEntityCardConfig : Parcelable{

    /** Идентификатор объекта [CertificateOrPetition]. */
    abstract val id: Long?

    /** Отпечаток объекта [CertificateOrPetition]. */
    abstract val thumbprint: String?

    /** True, если конфигурация карточки подразумевает использование вебвью. */
    abstract val hasWebImplementation: Boolean

    /** Ссылка на веб-страницу с карточкой. */
    abstract val url: String

    /** True, если экран должен загрузить и отобразить список доверий. */
    abstract val hasAuthoritiesList: Boolean

    /**
     * Конфигурации карточек сертификатов.
     * @property isQualified True, если сертификат является квалифицированным.
     * @property contractorId Идентификатор контрагента.
     * @property operationData Данные операции, которая доступна к выполнению на экране.
     *
     * @author vv.malyhin
     */
    sealed class Certificate : CertificateEntityCardConfig() {

        abstract val isQualified: Boolean?
        abstract val contractorId: Long?
        abstract val operationData: OperationData?

        /** Карточка сертификата со всеми возможными блоками информации. */
        @Parcelize
        class Detailed(
            override val id: Long?,
            override val thumbprint: String,
            override val contractorId: Long? = null,
            override val operationData: OperationData? = null,
            override val isQualified: Boolean? = null,
            override val hasAuthoritiesList: Boolean = false,
        ) : Certificate() {
            @IgnoredOnParcel
            override val hasWebImplementation = false
            @IgnoredOnParcel
            override val url = ""
        }

        /** Карточка сертификата с основной информацией. */
        @Parcelize
        class Preview(
            override val id: Long?,
            override val thumbprint: String,
            override val contractorId: Long? = null,
            override val isQualified: Boolean? = null,
        ) : Certificate() {
            @IgnoredOnParcel
            override val operationData = null
            @IgnoredOnParcel
            override val hasAuthoritiesList = false
            @IgnoredOnParcel
            override val hasWebImplementation = false
            @IgnoredOnParcel
            override val url = ""
        }

        /** Карточка сертификата для запуска операции. */
        @Parcelize
        class Operation(
            override val id: Long?,
            override val thumbprint: String,
            override val operationData: OperationData,
            override val isQualified: Boolean? = null,
            override val hasAuthoritiesList: Boolean = false,
        ) : Certificate() {
            @IgnoredOnParcel
            override val contractorId = null
            @IgnoredOnParcel
            override val hasWebImplementation = false
            @IgnoredOnParcel
            override val url = ""
        }
    }

    /**
     * Конфигурации карточек заявок.
     *
     * @author vv.malyhin
     */
    sealed class Petition : CertificateEntityCardConfig() {

        /** Карточка заявка на КЭП. */
        @Parcelize
        class Qualified(override val id: Long, override val url: String) : Petition() {

            @IgnoredOnParcel
            override val thumbprint: String? = null

            @IgnoredOnParcel
            override val hasWebImplementation = false

            @IgnoredOnParcel
            override val hasAuthoritiesList = false
        }

        /** Карточка заявка на НЭП. */
        @Parcelize
        class Unqualified(override val url: String) : Petition() {

            @IgnoredOnParcel
            override val id = null

            @IgnoredOnParcel
            override val thumbprint: String? = null

            @IgnoredOnParcel
            override val hasWebImplementation = true

            @IgnoredOnParcel
            override val hasAuthoritiesList = false
        }
    }
}

