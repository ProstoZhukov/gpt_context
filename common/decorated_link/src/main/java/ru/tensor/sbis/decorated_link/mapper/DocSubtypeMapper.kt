package ru.tensor.sbis.decorated_link.mapper

import ru.tensor.sbis.toolbox_decl.linkopener.data.LinkDocSubtype
import timber.log.Timber
import ru.tensor.sbis.linkdecorator.generated.LinkPreview as ControllerLinkPreview

/**
 * Маппер для преобразования подтипов ссылок контроллера [ControllerDocSubtype] к
 * подтипам ссылок [LinkDocSubtype] используемому на UI
 */
internal fun ControllerLinkPreview.mapDocSubtype(): LinkDocSubtype =
    when (docSubtype) {
        "FILE"                        -> LinkDocSubtype.DISK_FILE
        "XML"                         -> LinkDocSubtype.DISK_XML
        "IMAGE"                       -> LinkDocSubtype.DISK_IMAGE
        "XLS"                         -> LinkDocSubtype.DISK_XLS
        "DOC"                         -> LinkDocSubtype.DISK_DOC
        "PPT"                         -> LinkDocSubtype.DISK_PPT
        "PDF"                         -> LinkDocSubtype.DISK_PDF
        "TXT"                         -> LinkDocSubtype.DISK_TXT
        "ARCHIVE"                     -> LinkDocSubtype.DISK_ARCHIVE
        "AUDIO"                       -> LinkDocSubtype.DISK_AUDIO
        "VIDEO"                       -> LinkDocSubtype.DISK_VIDEO
        "FOLDER"                      -> LinkDocSubtype.DISK_FOLDER
        "URL"                         -> LinkDocSubtype.DISK_URL
        "LINK"                        -> LinkDocSubtype.DISK_LINK
        "SABYDOC"                     -> LinkDocSubtype.DISK_SABYDOC
        "СлужЗап"                     -> LinkDocSubtype.TASK_NOTE
        "ПланРабот"                   -> LinkDocSubtype.TASK_WORK_PLAN
        "ПунктПлана"                  -> LinkDocSubtype.TASK_WORK_PLAN_ITEM
        "CapitalDoc"                  -> LinkDocSubtype.TASK_CAPITAL_DOC
        "ЗаявкаСертификата"           -> LinkDocSubtype.TASK_SERTIFICATE
        "ДвижениеСредств"             -> LinkDocSubtype.TASK_FUNDS_MOVE
        "АвансОтчет"                  -> LinkDocSubtype.TASK_PREPAYMENT_REPORT
        "СписокЛидов"                 -> LinkDocSubtype.TASK_LEAD_LIST
        "ЗаявкаНаАккредитацию"        -> LinkDocSubtype.TASK_ACCREDITATION
        "ВыдачаПрочее"                -> LinkDocSubtype.TASK_OUTCOME
        "Infraction"                  -> LinkDocSubtype.TASK_INFRACTION
        "РекламацияВнутр"             -> LinkDocSubtype.TASK_RECLAMATION
        "Отпуск"                      -> LinkDocSubtype.TASK_VACATION
        "Наряд"                       -> LinkDocSubtype.TASK_ORDER
        "СчетИсх"                     -> LinkDocSubtype.TASK_INVOICE
        "Поощрение"                   -> LinkDocSubtype.TASK_INCENTIVE
        "Взыскание"                   -> LinkDocSubtype.TASK_PENALTY
        "ПредставлениеФНС"            -> LinkDocSubtype.TASK_REQUIREMENTS
        "ИстребованиеФНС"             -> LinkDocSubtype.TASK_REQUIREMENTS
        "Проект"                      -> LinkDocSubtype.TASK_PROJECT
        "ПунктПроверки"               -> LinkDocSubtype.REVIEW_ITEM
        "РекламацияВх"                -> LinkDocSubtype.RECLAMATION
        "Командировка"                -> LinkDocSubtype.BUSINESS_TRIP
        "Отгул"                       -> LinkDocSubtype.TIME_OFF
        "Переработка"                 -> LinkDocSubtype.OVERTIME_HOURS
        "Прогул"                      -> LinkDocSubtype.TRUANCY
        "ПриходныйОрдер"              -> LinkDocSubtype.ORDER_INCOME
        "РасходныйОрдер"              -> LinkDocSubtype.ORDER_EXPEND
        "ИсходящийПлатеж"             -> LinkDocSubtype.OUTGOING_PAYMENT
        "ВходящийПлатеж"              -> LinkDocSubtype.INCOMING_PAYMENT
        "ЗаявкаНаОплату"              -> LinkDocSubtype.PAYMENT_REQUEST
        "Простой"                     -> LinkDocSubtype.DOWNTIME
        "ПунктЧеклиста"               -> LinkDocSubtype.CHECKLIST_ITEM
        "ТабельНаДень"                -> LinkDocSubtype.DAY_TIMESHEET
        "DeliveryTask"                -> LinkDocSubtype.TASK_DELIVERY_TASK
        "ConsignmentNote"             -> LinkDocSubtype.TASK_DELIVERY_TASK
        "Waybill"                     -> LinkDocSubtype.TASK_WAYBILL
        "ПриемНаРаботу"               -> LinkDocSubtype.HIRE
        "ИзмененияОклада"             -> LinkDocSubtype.CHANGE_RATE
        "ПриказНаУвольнение"          -> LinkDocSubtype.DISMISSAL
        "ИзмененияДолжности"          -> LinkDocSubtype.TRANSFER
        "Refueling"                   -> LinkDocSubtype.REFUELING
        "ОтчетФНС"                    -> LinkDocSubtype.TASK_FNS_REPORT
        "ДокОтгрВх"                   -> LinkDocSubtype.ADMISSION
        "ДокОтгрИсх"                  -> LinkDocSubtype.SELLING
        "ДоговорДок"                  -> LinkDocSubtype.AGREEMENT
        "PriceMatchingIn"             -> LinkDocSubtype.PRICE_MATCHING_IN
        "СовещаниеСервис"             -> LinkDocSubtype.MEETING
        "Видеосовещание"              -> LinkDocSubtype.MEETING_VIDEOCALL
        "Вебинар"                     -> LinkDocSubtype.WEBINAR
        "Мероприятие"                 -> LinkDocSubtype.EVENT
        "InstructionDoc"              -> LinkDocSubtype.INSTRUCTION
        "ClientsList"                 -> LinkDocSubtype.CLIENTS_LIST
        "ShiftSchedule"               -> LinkDocSubtype.SHIFT_SCHEDULE
        ""                            -> LinkDocSubtype.UNKNOWN
        else -> {
            // нормальная ситуация - можем долго жить без поддержки типа, и мешать тестированию это не должно
            Timber.d("Unknown document subtype: $docSubtype (href $href)")
            LinkDocSubtype.UNKNOWN
        }
    }
