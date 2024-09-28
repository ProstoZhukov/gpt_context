package ru.tensor.sbis.cashboxes_lite_decl.feature

import android.content.Context
import android.os.Bundle
import android.os.Parcelable
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import io.reactivex.Maybe
import io.reactivex.Single
import kotlinx.parcelize.Parcelize
import ru.tensor.sbis.cashboxes_lite_decl.dialog.ModalDialogWindow
import ru.tensor.sbis.cashboxes_lite_decl.government_prices.DialogDataMaximalProductCodePriceLessThatMinimalGovernmentPrice
import ru.tensor.sbis.cashboxes_lite_decl.government_prices.DialogDataMerchPriceLessThatMinimalGovernmentPrice
import ru.tensor.sbis.cashboxes_lite_decl.government_prices.DialogDataMerchPriceNotEqualsMaximumPriceFromProductCode
import ru.tensor.sbis.cashboxes_lite_decl.government_prices.GovernmentPriceDialogKeys.GOVERNMENT_DIALOG_ACCEPT_EMP_MORE_MRC
import ru.tensor.sbis.cashboxes_lite_decl.government_prices.GovernmentPriceDialogKeys.GOVERNMENT_DIALOG_ACCEPT_PRICE_LESS_MINIMAL_GOVERNMENT_PRICE
import ru.tensor.sbis.cashboxes_lite_decl.government_prices.GovernmentPriceDialogKeys.GOVERNMENT_DIALOG_ACCEPT_PRICE_NOT_EQUALS_MAXIMUM_PRICE_FROM_PRODUCT_CODE
import ru.tensor.sbis.cashboxes_lite_decl.government_prices.GovernmentPriceDialogKeys.GOVERNMENT_DIALOG_CONTINUE_WITH_VIOLATION_WHEN_PRICE_LESS_MINIMAL_GOVERNMENT_PRICE
import ru.tensor.sbis.cashboxes_lite_decl.government_prices.GovernmentPriceDialogKeys.GOVERNMENT_DIALOG_CONTINUE_WITH_VIOLATION_WHEN_PRICE_NOT_EQUAL_MAXIMUM_PRICE_FROM_PROD_CODE
import ru.tensor.sbis.cashboxes_lite_decl.model.CheckTobaccoGovernmentRulesResult
import ru.tensor.sbis.cashboxes_lite_decl.model.CheckTobaccoPriceResult
import ru.tensor.sbis.cashboxes_lite_decl.model.IndicatedBatch
import ru.tensor.sbis.cashboxes_lite_decl.model.MarkedGoodsParams
import ru.tensor.sbis.cashboxes_lite_decl.model.MarkingCodeValidationInfo
import ru.tensor.sbis.cashboxes_lite_decl.model.PacksWithPrice
import ru.tensor.sbis.cashboxes_lite_decl.model.PerfumeInfo
import ru.tensor.sbis.cashboxes_lite_decl.model.ValidateProductCodeResult
import ru.tensor.sbis.catalog_decl.catalog.DraftAlcoData
import ru.tensor.sbis.catalog_decl.catalog.MarkedProductionGroup
import ru.tensor.sbis.catalog_decl.catalog.MeasuredValueInside
import ru.tensor.sbis.catalog_decl.catalog.NomenclatureAlcoInfo
import ru.tensor.sbis.catalog_decl.catalog.OperationType
import ru.tensor.sbis.catalog_decl.catalog.SubAccounting
import ru.tensor.sbis.loyalty_decl.model.LoyaltyProgram
import ru.tensor.sbis.loyalty_decl.model.LoyaltyProgramBaseInfo
import ru.tensor.sbis.plugin_struct.feature.Feature
import java.math.BigDecimal
import java.util.UUID

const val MAX_PRICE = 99999999.99
const val FORCE_ADD_MERCH = "FORCE_ADD_MERCH"
const val FORCE_SET_MARKED_CODE_ACTION = "FORCE_SET_MARKED_CODE_ACTION"
const val SCAN_MARKED_CODE_WITH_PHONE_CAMERA = "SCAN_MARKED_CODE_WITH_PHONE_CAMERA"
const val REMOVE_MARKING_CODE_WITHOUT_SCAN = "REMOVE_MARKING_CODE_WITHOUT_SCAN"
const val CONTINUE_WITHOUT_CODE = "CONTINUE_WITHOUT_CODE"
const val CANCEL_MARKING_CODE_SCAN = "CANCEL_MARKING_CODE_SCAN"
const val BATCH_SELECTED = "BATCH_SELECTED"
const val BATCH_KEY = "BATCH_KEY"

/** @SelfDocumented */
interface SaleFeature : Feature {

    /** Получить ограничения по номенклатурам - количество номенклатур в чеке, максимальная цена номенклатуры, и.т.д */
    fun getNomenclatureRestrictions(): Single<SaleNomenclatureRestrictions>

    /** Получить информацию о номенклатуре по её идентификатору */
    fun getNomenclatureData(operationUUID: UUID, positionUuid: UUID): SaleNomenclatureData

    /** Отредактировать информацию о номенклатуре */
    fun setNomenclatureData(data: SaleNomenclatureData)

    /**
     * Показывает диалог с предупреждением/ошибкой в случае если серийный номер не прошел валидацию.
     * @param result результат валидации
     * @param fragment фрагмент в котором будет показан диалог с предупреждением/ошибкой.
     * Чтобы получить событие игнорирования валидации от пользователя, фрагмент должен реализовывать интерфейс ContentActionHandler.
     * @param forceSkipValidationActionName действие которое будет передаваться в ContentActionHandler
     * при выборе пользователем опции продолжения с игнорированием валидации
     * @return true если серийный номер можно добавлять, false если добавлять нельзя
     */
    fun handleSerialNumberValidationResult(
        result: CommonValidateSerialResult,
        fragment: Fragment,
        forceSkipValidationActionName: String,
        defaultActionName: String
    ): Boolean

    /**
     * Валидирует серийный номер
     */
    suspend fun validateSerialNumber(
        operationUUID: UUID,
        catalogUUID: UUID?,
        serialNumber: String,
        subAccounting: SubAccounting?
    ): CommonValidateSerialResult

    /**
     * Проверяет сходство по шаблону серийного номера подвида учета
     */
    fun checkSerialNumberPatternMatching(serialNumber: String, subAccounting: SubAccounting): Boolean

    /**
     * Показывает диалог предупреждения о лишних СН
     */
    fun showAlertDialogExtraSerialNumbers(fragment: Fragment, continueWithExtraSnActionKey: String)

    /**
     * Показывает диалог предупреждения о недостаточных СН
     */
    fun showAlertDialogNotEnoughSerialNumbers(fragment: Fragment, continueWithExtraSnActionKey: String)

    /**
     * Показывает диалог предупреждения для случаев когда кол-во товара дробное и у товара есть учет по серийникам.
     */
    fun showDecimalQuantityWithSerialNumberAccountingWarningDialog(
        fragment: Fragment,
        merchName: String,
        fullSnControl: Boolean,
        snControl: Boolean,
        subAccounting: SubAccounting?
    )

    /**
     * Отобразить всплывающее меню со списком доступных упаковок
     * @param fragment фрагмент, в котором нужно отобразить меню
     * @param anchorView view-якорь (рядом с которым нужно показать меню)
     * @param selectedPack выбранная упаковка товара
     * @param packSelectedAction действие, которое необходимо выполнить при выборе упаковки
     * @param positionUuid UUID номенклатуры в продаже
     */
    fun showPackSelectionMenu(
        fragment: Fragment,
        anchorView: View,
        selectedPack: PacksWithPrice?,
        packSelectedAction: (PacksWithPrice) -> Unit,
        positionUuid: UUID,
        operationUUID: UUID,
        isDraftPerfume: Boolean
    )

    /**
     * Показывает контекстное меню с подробной информацией о скидках
     * @param fragment DialogFragment на котором показывается подсказка
     * @param anchorView view, относительно которого показывается подсказка
     * @param isChangeCatalogPriceAllowed доступно ли изменение каталожной цены
     * @param isPriceEditable можно ли менять цену
     * @param mrcValue МРЦ, если null, то опция установки МРЦ недоступна
     * @param systemDiscount информация о скидке, если null, то блок системной скидки не показывается
     * @param catalogPrice цена товара в каталоге
     * @param currentPrice текущая цена товара
     * @param isSystemDiscountApplied применена ли к этой позиции системная скидка. systemDiscount может быть заполнен,
     * но скидка не считаться примененной, например, если пользователь поменял параметры позиции.
     * @param isConditionalNomenclature является ли позиция условным товаром
     * @param listener слушатель действий пользователя на подсказке
     */
    fun showDiscountInfoContextMenu(
        fragment: Fragment,
        anchorView: View,
        isChangeCatalogPriceAllowed: Boolean,
        isPriceEditable: Boolean,
        mrcValue: BigDecimal?,
        systemDiscount: LoyaltyProgram?,
        catalogPrice: BigDecimal,
        currentPrice: BigDecimal,
        isSystemDiscountApplied: Boolean,
        isConditionalNomenclature: Boolean,
        listener: DiscountTooltipActionsListener
    )

    /**
     * Отобразить всплывающее меню со списком доступных на выбор скидок
     * @param fragment на котором показывает меню
     * @param anchorView view от которого отображаем меню
     * @param onDiscountSelected действие при выборе скидки в меню
     */
    fun showSelectDiscountTooltipMenu(fragment: Fragment, anchorView: View, onDiscountSelected: (BigDecimal) -> Unit)

    /**
     * Отображает окно с подробной информацией о скидке
     * @param fragmentManager см. [FragmentManager]
     * @param discountInfo информация о скидке, см. [LoyaltyProgramBaseInfo]
     */
    fun showDiscountInfo(fragmentManager: FragmentManager, discountInfo: LoyaltyProgramBaseInfo)

    /**
     * Проверка МРЦ табака
     * TODO: Должно быть удалено по:
     * https://online.sbis.ru/opendoc.html?guid=02fa63ee-2945-44bb-9017-ce26fbbb85da&client=3
     * @param markedGoodsParams информация о маркировке
     * @param price проверяемая цена
     * @return [CheckTobaccoPriceResult]
     */
    suspend fun checkTobaccoPrice(
        markedGoodsParams: MarkedGoodsParams,
        price: BigDecimal
    ): Result<CheckTobaccoPriceResult>

    /**
     * Проверка табака на предмет МРЦ/ЕМЦ/гос.ограничений.
     *
     * @param markedGoodsParams информация о маркировке
     * @param price проверяемая цена
     * @return [CheckTobaccoPriceResult]
     */
    suspend fun checkTobaccoGovernmentRules(
        markedGoodsParams: MarkedGoodsParams,
        price: BigDecimal
    ): Result<CheckTobaccoGovernmentRulesResult>

    /**
     * Создание диалога для подтверждения цены табака
     * @param name название товара
     * @param price цена табака
     * @param maxRetailPrice максимальная розничная цена табака
     * @return [Fragment]
     */
    fun getTobaccoMaxRetailPriceDialog(
        name: String,
        price: BigDecimal,
        maxRetailPrice: BigDecimal
    ): ModalDialogWindow

    /**
     * Создание диалога-предупреждения о минимальной цене на табак
     * @param name название товара
     * @param maxRetailPrice максимальная розничная цена табака
     * @param unifiedMinimumTobaccoPrice минимально допустимая цена на табак (или блок табака)
     * @return [Fragment]
     */
    fun getUnifiedMinimumTobaccoPriceDialog(
        name: String,
        maxRetailPrice: BigDecimal,
        unifiedMinimumTobaccoPrice: BigDecimal
    ): ModalDialogWindow

    /**
     *  Диалог запроса кода маркировки
     *
     * @param requestForRemove Сканирования для подтверждения удаления КМ
     * @param isOptionalCode Возможность проболжить без сканирования КМ
     * @param isForMarkedAlco Для случая, когда работа ведётся с маркированным алкоголем
     */
    fun getMarkingCodeRequestDialog(
        requestForRemove: Boolean,
        isOptionalCode: Boolean = false,
        isForMarkedAlco: Boolean = false,
        tag: String? = null
    ): ModalDialogWindow

    /**
     * Получение данных для последующей валидации кода маркировки
     * @param markingCode отсканированный код маркировки
     * @return данные для последующей валидации кода маркировки [MarkingCodeValidationInfo]
     */
    suspend fun getMarkingCodeValidationInfo(
        operationUUID: UUID,
        nomenclatureUuid: UUID?,
        markingCode: String,
        nomUuid: UUID
    ): MarkingCodeValidationInfo

    /**
     * Валидация кода маркировки
     * @param uuid UUID номенклатуры
     * @param markingCode отсканированный код маркировки
     * @param markingCodeValidationInfo данные для валидации кода маркировки
     * @param fragment хост-фрагмент для отображения ошибок и предупреждений
     * @return true, если валидация прошла успешно
     */
    suspend fun validateMarkingCode(
        uuid: UUID,
        markingCode: String,
        markingCodeValidationInfo: MarkingCodeValidationInfo,
        fragment: Fragment,
        subAccounting: SubAccounting?,
        markedProductionGroup: MarkedProductionGroup?
    ): Boolean

    /**
     * Обработчик ошибок при получении данных для валидации кода маркировки
     * @param uuid UUID номенклатуры
     * @param fragment хост-фрагмент для отображения ошибок
     * @param throwable исключение
     */
    fun handleGetMarkingCodeValidationInfoErrors(uuid: UUID, fragment: Fragment, throwable: Throwable)

    /**
     * Возвращает идентификатор вида скидки в котором необходимо её отобразить
     * @param isConditionalNomenclature скидка применена на условный товар?
     * @param catalogPrice цена товара в каталоге
     * @param actualPrice текущая цена товара которая указана пользователем
     * @param initialPrice цена товара которая была у него изначально когда открыли карточку товара
     * @param systemDiscountInfo информация о системной скидке, если есть
     * @param markedGoodsParams информация о маркировке, если есть.
     */
    fun getDiscountVisibilityState(
        isConditionalNomenclature: Boolean,
        catalogPrice: BigDecimal,
        actualPrice: BigDecimal,
        initialPrice: BigDecimal,
        systemDiscountInfo: LoyaltyProgram?,
        markedGoodsParams: MarkedGoodsParams?
    ): DiscountVisibilityState

    /**
     * Отображение ошибки при попытке сохранить товар без кода маркировки там, где это запрещено
     * @param fragment фрагмент
     * @param anchorViewId идентификатор View, над которой появится всплывающая ошибка
     */
    fun showMarkingCodeNotScannedError(fragment: Fragment, anchorViewId: Int)

    /**
     * Создание фрагмента окна выбора партии
     * @param nomenclatureUuid UUID номенклатуры
     * @return [Fragment]
     */
    fun createBatchListFragment(nomenclatureUuid: UUID): Fragment

    /**
     * Скрытие ошибки при попытке сохранить товар без кода маркировки там, где это запрещено
     */
    fun hideMarkingCodeNotScannedError()

    /**
     * Асинхронное получение значения на весах
     */
    fun getMeasuredValue(): Maybe<MeasuredValueInside>

    /** Показать диалог, предлагающий поднять цену алкоголя до МРЦ. */
    fun showAlcoMinimalRetailPriceDialog(
        fragment: Fragment,
        positionTitle: String,
        positionPrice: BigDecimal,
        minimalAlcoPrice: BigDecimal,
        positionUUID: UUID?,
        onAcceptMrcActionId: String
    )

    /**
     * Создать диалог "Максимальная розничная цена ниже минимальной по закону".
     * Используется, если МРЦ из КМ меньше, чем ЕМЦ товара.
     * Это является противоречивой ситуацией. Например, государство говорит, что товар нельзя продавать дешевле чем,
     * к примеру, 100р(ЕМЦ), а МРЦ из КМ говорит, максимальная цена к продаже, к примеру, 80р(МРЦ)
     *
     * Рисует кнопки:
     * - "Продолжить с нарушением", если это заложено в конфигурации.
     * - "Отмена".
     *
     * При нажатии на "Продолжить с нарушением" отсылает:
     * - Ключ [GOVERNMENT_DIALOG_ACCEPT_EMP_MORE_MRC]
     * - [additionalParams]
     *
     * При нажатии на "Отмена" просто закрывает диалог.
     *
     * @param data набор данных, необходимых для отображения диалога.
     * @param additionalParams Bundle, который будет пронесён через диалог и переотправлен при необходимости.
     * */
    fun createMaximumProductCodePriceLessThatMinimalGovernmentPriceDialog(
        data: DialogDataMaximalProductCodePriceLessThatMinimalGovernmentPrice,
        additionalParams: Bundle? = null
    ): ModalDialogWindow

    /**
     * Создать диалог "Цена отличается от максимальной розничной цены".
     *
     * Рисует кнопки:
     * - "Установить %s", s = МРЦ. Если МРЦ = 100, нарисуется кнопка "Установить 100".
     * - "Продолжить с нарушением", если это заложено в конфигурации.
     * - "Отмена".
     *
     * При нажатии на "Установить %s" отсылает:
     * - Ключ [GOVERNMENT_DIALOG_ACCEPT_PRICE_NOT_EQUALS_MAXIMUM_PRICE_FROM_PRODUCT_CODE]
     * - [additionalParams]
     *
     * При нажатии на "Продолжить с нарушением" отсылает:
     * - Ключ [GOVERNMENT_DIALOG_CONTINUE_WITH_VIOLATION_WHEN_PRICE_NOT_EQUAL_MAXIMUM_PRICE_FROM_PROD_CODE]
     * - [additionalParams]
     *
     * При нажатии на "Отмена" просто закрывает диалог.
     *
     * @param context контекст, необходимый для сбора строки "Установить %s".
     * @param data набор данных, необходимых для отображения диалога.
     * @param additionalParams Bundle, который будет пронесён через диалог и переотправлен при необходимости.
     * */
    fun createMerchPriceNotEqualsMaximumPriceFromProductCodeDialog(
        context: Context,
        data: DialogDataMerchPriceNotEqualsMaximumPriceFromProductCode,
        additionalParams: Bundle? = null
    ): ModalDialogWindow

    /**
     * Создать диалог "Цена ниже минимальной по закону".
     *
     * Рисует кнопки:
     * - "Установить %s", s = МРЦ. Если МРЦ = 100, нарисуется кнопка "Установить 100".
     * - "Продолжить с нарушением", если это заложено в конфигурации.
     * - "Отмена".
     *
     * При нажатии на "Установить %s" отсылает:
     * - Ключ [GOVERNMENT_DIALOG_ACCEPT_PRICE_LESS_MINIMAL_GOVERNMENT_PRICE]
     * - [additionalParams]
     *
     * При нажатии на "Продолжить с нарушением" отсылает:
     * - Ключ [GOVERNMENT_DIALOG_CONTINUE_WITH_VIOLATION_WHEN_PRICE_LESS_MINIMAL_GOVERNMENT_PRICE]
     * - [additionalParams]
     *
     * При нажатии на "Отмена" просто закрывает диалог.
     *
     * @param context контекст, необходимый для сбора строки "Установить %s".
     * @param data набор данных, необходимых для отображения диалога.
     * @param additionalParams Bundle, который будет пронесён через диалог и переотправлен при необходимости.
     * */
    fun createMerchPriceLessThatMinimalGovernmentPriceDialog(
        context: Context,
        data: DialogDataMerchPriceLessThatMinimalGovernmentPrice,
        additionalParams: Bundle? = null
    ): ModalDialogWindow

    /**
     * Получить структуру [PerfumeInfo]
     * @param nomUUID Каталожный UUID номенклатуры.
     * @param markedGoodsParams Данные о маркировке.
     * @param operationType Тип операции
     * */
    suspend fun getPerfumeInfo(
        nomUUID: UUID,
        subAccounting: SubAccounting?,
        markedProductionGroup: MarkedProductionGroup?,
        markedGoodsParams: MarkedGoodsParams?,
        operationType: OperationType?
    ): PerfumeInfo?

    /**
     * Прочитать настройку "Продажа алкоголя без ЕГАИС".
     * Может трактоваться как режим работы с алкоголем: true = Общепит, false = Магазин.
     * */
    suspend fun getAlcoholWithoutEGAISSetting(): Boolean

    /** Типы учётов серийных номеров */
    enum class SerialNumbersAccounting {
        /** Справочный. Серийный номер не обязателен. */
        REFERENTIAL,

        /** Полный. Серийный номер обязателен. */
        FULL
    }

    /** @SelfDocumented */
    data class SaleNomenclatureData(
        val saleUuid: UUID,
        val uuid: UUID,
        val price: BigDecimal,
        val sum: BigDecimal?,
        val catalogPrice: BigDecimal,
        val manualPrice: BigDecimal?,
        val quantity: BigDecimal,
        val serialNumbers: ArrayList<String>,
        val selectedPack: PacksWithPrice? = null,
        val isMarkedBySubAccountingType: Boolean = false,
        val markedGoodsParamList: List<MarkedGoodsParams>,
        val discountInfo: LoyaltyProgram?,
        val canBeSoldWithoutMarkingCode: Boolean,
        val indicatedBatch: IndicatedBatch?,
        val balance: BigDecimal?,
        val alcoInfo: NomenclatureAlcoInfo?,
        val minimalRetailPrice: BigDecimal?,
        val draftAlcoData: DraftAlcoData?,
        val perfumeInfo: PerfumeInfo?,
        val prodCodeScanningForbidden: Boolean
    )

    /** @SelfDocumented */
    @Parcelize
    data class SaleNomenclatureRestrictions(
        val maxPrice: Double,
        val maxQuantity: Int,
        val maxSumm: Int,
        val minTobaccoPrice: BigDecimal
    ) : Parcelable

    /** @SelfDocumented */
    data class CommonValidateSerialResult(
        val serialNumber: String,
        val validatedSerialNumberResult: ValidateProductCodeResult,
        var isSerialNumberPatternMatching: Boolean = true
    )

    /**
     * Слушатель действия на подсказке скидки
     */
    interface DiscountTooltipActionsListener {

        /**
         * В тултипе нажата кнопка "Установить цену", цена становится равной МРЦ.
         * */
        fun onSetPriceToMrcValueClick()

        /** Нажатие на скидку. Предполагается что откроется окно с информацией о скидке.*/
        fun onSystemDiscountClick()

        /**
         * Нажатие на кнопку возвращения цены у маркированного товара с МРЦ.
         * Предполагается что мы покажем диалог МРЦ, кнопка в подсказке отобразится
         * если в настройках приложения включена проверка МРЦ
         */
        fun onRevertWithConfirmationClick()

        /**
         * Нажатие на кнопку возвращения цены не у маркированного товара, либо у товара без МРЦ.
         * Возвращаем товару каталожную цену напрямую, без диалога МРЦ.
         */
        fun onPlainRevertClick()

        /**
         * Нажатие на кнопку сохранения цены. В этом случае текущая цена становится каталожной.
         */
        fun onRememberDiscountClick()
    }

    /**
     * Состояние отображения скидки.
     * SYSTEM_DISCOUNT - применена системная скидка (как правило показывается зеленым цветом)
     * MANUAL_DISCOUNT - применена ручная скидка (как правило показывается красным цветом)
     * NO_DISCOUNT - не показывать скидку
     */
    enum class DiscountVisibilityState {
        SYSTEM_DISCOUNT,
        MANUAL_DISCOUNT,
        NO_DISCOUNT
    }
}