package ru.tensor.sbis.application_tools.logcrashesinfo.deviceinfo

/**
 * @author du.bykov
 *
 * Модель представления данных об устройстве.
 */
class DeviceInfo private constructor(
    val brand: String,
    val name: String,
    val sdk: String
) {

    class Builder {

        private var name: String? = null
        private var brand: String? = null
        private var sdk: String? = null
        private var manufacturer: String? = null

        fun withBrand(brand: String): Builder {
            this.brand = brand
            return this
        }

        fun withSDK(sdk: String): Builder {
            this.sdk = sdk
            return this
        }

        fun withModel(name: String): Builder {
            this.name = name
            return this
        }

        fun withManufacturer(manufacturer: String): Builder {
            this.manufacturer = manufacturer
            return this
        }

        fun build(): DeviceInfo {
            return DeviceInfo(
                brand!!,
                name!!,
                sdk!!
            )
        }
    }
}