package ru.tensor.sbis.edo.additional_fields.decl.model

import ru.tensor.sbis.docface.generated.DocFace
import ru.tensor.sbis.edo.additional_fields.decl.model.raw_data.FieldRawData
import ru.tensor.sbis.edo.additional_fields.decl.model.raw_data.GroupRawData
import ru.tensor.sbis.edo.additional_fields.decl.model.value.AppliedListItem
import ru.tensor.sbis.edo.additional_fields.decl.model.value.BooleanValue
import ru.tensor.sbis.edo.additional_fields.decl.model.value.DoubleValue
import ru.tensor.sbis.edo.additional_fields.decl.model.value.FaceListType
import ru.tensor.sbis.edo.additional_fields.decl.model.value.FlagValue
import ru.tensor.sbis.edo.additional_fields.decl.model.value.FullNameValue
import ru.tensor.sbis.edo.additional_fields.decl.model.value.ListItem
import ru.tensor.sbis.edo.additional_fields.decl.model.value.TimeValue
import java.util.UUID
import ru.tensor.sbis.common.util.dateperiod.DatePeriod as UIDatePeriod
import java.util.Date as KDate
import kotlin.Boolean as KBoolean

/**
 * Модель дополнительного элемента - поля или группы полей
 */
sealed class AdditionalItemModel {

    abstract val id: UUID
    abstract val title: String
    var parent: Group? = null

    /**
     * Элемент типа "Группа" элементов
     */
    data class Group(val rawData: GroupRawData) : AdditionalItemModel() {
        override val id: UUID get() = rawData.id
        override val title: String get() = rawData.title

        private val _nestedItems = mutableListOf<AdditionalItemModel>()
        val nestedItems: List<AdditionalItemModel> get() = _nestedItems

        fun addNestedItem(item: AdditionalItemModel) {
            _nestedItems.add(item)
        }

        fun addNestedItems(items: List<AdditionalItemModel>) {
            _nestedItems.addAll(items)
        }
    }

    /**
     * Элемент типа "Поле"
     */
    sealed class Field<VALUE> : AdditionalItemModel() {
        abstract val rawData: FieldRawData

        override val id: UUID get() = rawData.id
        override val title: String get() = rawData.title
        val isRequired: KBoolean get() = rawData.isRequired
        val placeholder: String get() = rawData.placeholder

        abstract val defaultValue: VALUE
        abstract var value: VALUE

        open val isEmptyValue: KBoolean get() = value == null

        data class Text(
            override val rawData: FieldRawData,
            override val defaultValue: CharSequence?,
            override var value: CharSequence?,
            val isSuggest: KBoolean
        ) : Field<CharSequence?>() {

            override val isEmptyValue: KBoolean get() = value.isNullOrEmpty()

            fun updateValue(newValue: CharSequence?): Text =
                Text(rawData, defaultValue, newValue, isSuggest)
        }

        data class MultilineText(
            override val rawData: FieldRawData,
            override val defaultValue: CharSequence?,
            override var value: CharSequence?
        ) : Field<CharSequence?>() {

            override val isEmptyValue: KBoolean get() = value.isNullOrEmpty()

            fun updateValue(newValue: CharSequence?): MultilineText =
                MultilineText(rawData, defaultValue, newValue)
        }

        /**
         * Поле "Богатый текст"
         * В режиме просмотра используется поле [richTextValue], в режиме редактирования - [value] и view для
         * многострочного текста
         *
         * @property value Текстовое значение без стилей, изменяется при редактировании
         *
         * @property defaultValue Значение богатого текста в формате json по умолчанию
         *
         * @property defaultStringValue Значение текста без стилей по умолчанию.
         * Требуется для передачи правильного значения на контроллер для проверки на равенство доп. полей
         *
         * @property richTextValue Значение богатого текста в формате json, используется при просмотре
         *
         * @property immutableRichTextStringValue Неизменяемое текстовое значение без стилей.
         * Используется для сравнения этого значения и текстового значения после редактирования.
         * Если значения равны, то при сохранении нужно использовать [richTextValue], иначе [value]
         */
        data class RichText(
            override val rawData: FieldRawData,
            override val defaultValue: CharSequence?,
            val defaultStringValue: String?,
            override var value: CharSequence?,
            val richTextValue: String?,
            val immutableRichTextStringValue: CharSequence?
        ) : Field<CharSequence?>() {

            override val isEmptyValue: KBoolean get() = value.isNullOrEmpty()

            fun updateValue(newValue: CharSequence?): RichText =
                RichText(
                    rawData,
                    defaultValue,
                    defaultStringValue,
                    newValue,
                    richTextValue,
                    immutableRichTextStringValue
                )
        }

        /**
         * Числовое значение
         */
        data class Number(
            override val rawData: FieldRawData,
            override val defaultValue: DoubleValue?,
            override var value: DoubleValue?,
        ) : Field<DoubleValue?>() {

            fun updateValue(newValue: DoubleValue?): Number =
                Number(rawData, defaultValue, newValue)
        }

        /**
         * Логическое значение
         */
        data class Boolean(
            override val rawData: FieldRawData,
            override val defaultValue: BooleanValue,
            override var value: BooleanValue,
        ) : Field<BooleanValue>() {

            override val isEmptyValue: KBoolean get() = value == BooleanValue.UNDEFINED

            fun updateValue(newValue: BooleanValue): Boolean =
                Boolean(rawData, defaultValue, newValue)
        }

        /**
         * Значение из списка выбираемых элементов
         */
        data class SelectableList(
            override val rawData: FieldRawData,
            override val defaultValue: List<ListItem>,
            override var value: List<ListItem>,
            val isMultiSelectable: KBoolean,
            val isSearchable: KBoolean,
        ) : Field<List<ListItem>>() {

            override val isEmptyValue: KBoolean get() = value.none { it.isSelected }

            fun updateValue(newValue: List<ListItem>): SelectableList =
                SelectableList(rawData, defaultValue, newValue, isMultiSelectable, isSearchable)
        }

        /**
         * Значение из прикладного списка выбираемых элементов
         */
        data class AppliedList(
            override val rawData: FieldRawData,
            override val defaultValue: List<AppliedListItem>,
            override var value: List<AppliedListItem>,
            val isMultiSelectable: kotlin.Boolean
        ) : Field<List<AppliedListItem>>() {

            override val isEmptyValue: KBoolean get() = value.isEmpty()

            fun updateValue(newValue: List<AppliedListItem>): AppliedList =
                AppliedList(rawData, defaultValue, newValue, isMultiSelectable)
        }

        /**
         * Значение из списка персон
         */
        data class FaceList(
            override val rawData: FieldRawData,
            override val defaultValue: List<DocFace>,
            override var value: List<DocFace>,
            val listType: FaceListType,
            val isMultiSelectable: KBoolean,
        ) : Field<List<DocFace>>() {

            override val isEmptyValue: KBoolean get() = value.isEmpty()

            fun updateValue(newValue: List<DocFace>): FaceList =
                FaceList(rawData, defaultValue, newValue, listType, isMultiSelectable)
        }

        data class Flag(
            override val rawData: FieldRawData,
            override val defaultValue: KBoolean,
            override var value: KBoolean,
        ) : Field<KBoolean>() {

            fun updateValue(newValue: KBoolean): Flag =
                Flag(rawData, defaultValue, newValue)
        }

        data class FlagList(
            override val rawData: FieldRawData,
            override val defaultValue: List<FlagValue>,
            override var value: List<FlagValue>
        ) : Field<List<FlagValue>>() {

            override val isEmptyValue: KBoolean get() = value.none { it.isSelected }

            fun updateValue(newValue: List<FlagValue>): FlagList =
                FlagList(rawData, defaultValue, newValue)
        }

        sealed class Masked<VALUE> : Field<VALUE>() {

            abstract val mask: String?

            /**
             * Значение даты
             */
            data class Date(
                override val rawData: FieldRawData,
                override val mask: String?,
                override val defaultValue: KDate?,
                override var value: KDate?
            ) : Masked<KDate?>() {

                fun updateValue(newValue: KDate?): Date =
                    Date(rawData, mask, defaultValue, newValue)
            }

            data class DatePeriod(
                override val rawData: FieldRawData,
                override val mask: String?,
                override val defaultValue: UIDatePeriod?,
                override var value: UIDatePeriod?
            ) : Masked<UIDatePeriod?>() {

                fun updateValue(newValue: UIDatePeriod?): DatePeriod =
                    DatePeriod(rawData, mask, defaultValue, newValue)
            }

            data class FullName(
                override val rawData: FieldRawData,
                override val mask: String?,
                override val defaultValue: FullNameValue,
                override var value: FullNameValue
            ) : Masked<FullNameValue>() {

                override val isEmptyValue: KBoolean get() = value.isEmpty

                fun updateValue(newValue: FullNameValue): FullName =
                    FullName(rawData, mask, defaultValue, newValue)
            }

            data class Phone(
                override val rawData: FieldRawData,
                override val mask: String?,
                override val defaultValue: CharSequence?,
                override var value: CharSequence?,
            ) : Masked<CharSequence?>() {

                override val isEmptyValue: KBoolean get() = value.isNullOrEmpty()

                fun updateValue(newValue: CharSequence?): Phone =
                    Phone(rawData, mask, defaultValue, newValue)
            }

            data class CreditCard(
                override val rawData: FieldRawData,
                override val mask: String?,
                override val defaultValue: CharSequence?,
                override var value: CharSequence?,
            ) : Masked<CharSequence?>() {

                override val isEmptyValue: KBoolean get() = value.isNullOrEmpty()

                fun updateValue(newValue: CharSequence?): CreditCard =
                    CreditCard(rawData, mask, defaultValue, newValue)
            }

            data class SNILS(
                override val rawData: FieldRawData,
                override val mask: String?,
                override val defaultValue: CharSequence?,
                override var value: CharSequence?,
            ) : Masked<CharSequence?>() {

                override val isEmptyValue: KBoolean get() = value.isNullOrEmpty()

                fun updateValue(newValue: CharSequence?): SNILS =
                    SNILS(rawData, mask, defaultValue, newValue)
            }

            data class Ip(
                override val rawData: FieldRawData,
                override val mask: String?,
                override val defaultValue: CharSequence?,
                override var value: CharSequence?
            ) : Masked<CharSequence?>() {

                override val isEmptyValue: KBoolean get() = value.isNullOrEmpty()

                fun updateValue(newValue: CharSequence?): Ip =
                    Ip(rawData, mask, defaultValue, newValue)
            }

            data class Time(
                override val rawData: FieldRawData,
                override val mask: String?,
                override val defaultValue: TimeValue?,
                override var value: TimeValue?
            ) : Masked<TimeValue?>() {

                fun updateValue(newValue: TimeValue?): Time =
                    Time(rawData, mask, defaultValue, newValue)
            }
        }
    }
}
