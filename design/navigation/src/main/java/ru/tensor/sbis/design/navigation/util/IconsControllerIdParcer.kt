/**
 * Файл с инструментом парсинга данных с контроллера.
 *
 * @author ma.kolpakov
 */
package ru.tensor.sbis.design.navigation.util

import ru.tensor.sbis.design.R as RDesign

/**
 * Функция маппит идентификаторы иконок навигации с контроллера в xml иконки.
 */
@Suppress("unused")
fun mapTextToIconRes(name: String?) = when (name) {
    "default-icon" -> ControllerNavIcon(
        RDesign.string.design_nav_icon_default,
        RDesign.string.design_nav_icon_sbis_bird_fill
    )

    "Aexpand" -> ControllerNavIcon(RDesign.string.design_nav_icon_aexpand, RDesign.string.design_nav_icon_aexpand)
    "Acollapse" -> ControllerNavIcon(
        RDesign.string.design_nav_icon_acoolapse,
        RDesign.string.design_nav_icon_acoolapse
    )

    "Menu" -> ControllerNavIcon(RDesign.string.design_nav_icon_menu, RDesign.string.design_nav_icon_menu)
    "Plus" -> ControllerNavIcon(RDesign.string.design_nav_icon_plus, RDesign.string.design_nav_icon_plus)
    "plus" -> ControllerNavIcon(RDesign.string.design_nav_icon_plus, RDesign.string.design_nav_icon_plus)
    "MarkExpand" -> ControllerNavIcon(
        RDesign.string.design_nav_icon_mark_expand,
        RDesign.string.design_nav_icon_mark_expand
    )

    "MarkRight" -> ControllerNavIcon(
        RDesign.string.design_nav_icon_mark_right,
        RDesign.string.design_nav_icon_mark_right
    )

    "View" -> ControllerNavIcon(RDesign.string.design_nav_icon_view, RDesign.string.design_nav_icon_view)
    "Auction" -> ControllerNavIcon(RDesign.string.design_nav_icon_auction, RDesign.string.design_nav_icon_auction)
    "auction" -> ControllerNavIcon(RDesign.string.design_nav_icon_auction, RDesign.string.design_nav_icon_auction)
    "Egais" -> ControllerNavIcon(RDesign.string.design_nav_icon_egais, RDesign.string.design_nav_icon_egais)
    "egais" -> ControllerNavIcon(RDesign.string.design_nav_icon_egais, RDesign.string.design_nav_icon_egais)
    "Cashbox" -> ControllerNavIcon(
        RDesign.string.design_nav_icon_cashbox,
        RDesign.string.design_nav_icon_cashbox_fill
    )

    "cashbox" -> ControllerNavIcon(
        RDesign.string.design_nav_icon_cashbox,
        RDesign.string.design_nav_icon_cashbox_fill
    )

    "Bookkeeping" -> ControllerNavIcon(
        RDesign.string.design_nav_icon_bookkeeping,
        RDesign.string.design_nav_icon_bookkeeping_fill
    )

    "Contacts" -> ControllerNavIcon(
        RDesign.string.design_nav_icon_contacts,
        RDesign.string.design_nav_icon_contacts_fill
    )

    "contacts" -> ControllerNavIcon(
        RDesign.string.design_nav_icon_contacts,
        RDesign.string.design_nav_icon_contacts_fill
    )

    "Ashop" -> ControllerNavIcon(RDesign.string.design_nav_icon_retail, RDesign.string.design_nav_icon_ashop_fill)
    "retail" -> ControllerNavIcon(RDesign.string.design_nav_icon_retail, RDesign.string.design_nav_icon_ashop_fill)
    "Ereport" -> ControllerNavIcon(
        RDesign.string.design_nav_icon_ereport,
        RDesign.string.design_nav_icon_ereport_fill
    )

    "ereport" -> ControllerNavIcon(
        RDesign.string.design_nav_icon_ereport,
        RDesign.string.design_nav_icon_ereport_fill
    )

    "Canavication" -> ControllerNavIcon(
        RDesign.string.design_nav_icon_navication,
        RDesign.string.design_nav_icon_canavication_fill
    )

    "ca_navication" -> ControllerNavIcon(
        RDesign.string.design_nav_icon_navication,
        RDesign.string.design_nav_icon_canavication_fill
    )

    "Mercury" -> ControllerNavIcon(RDesign.string.design_nav_icon_mercury, RDesign.string.design_nav_icon_mercury)
    "Setting" -> ControllerNavIcon(
        RDesign.string.design_nav_icon_setting,
        RDesign.string.design_nav_icon_setting_fill
    )

    "Purchase" -> ControllerNavIcon(
        RDesign.string.design_nav_icon_purchase,
        RDesign.string.design_nav_icon_purchases_fill
    )

    "purchases" -> ControllerNavIcon(
        RDesign.string.design_nav_icon_purchase,
        RDesign.string.design_nav_icon_purchases_fill
    )

    "Stock" -> ControllerNavIcon(RDesign.string.design_nav_icon_stock, RDesign.string.design_nav_icon_stock)
    "Nomenclature" -> ControllerNavIcon(
        RDesign.string.design_nav_icon_nomenclature,
        RDesign.string.design_nav_icon_nomenclature_fill
    )

    "nomenclature" -> ControllerNavIcon(
        RDesign.string.design_nav_icon_nomenclature,
        RDesign.string.design_nav_icon_nomenclature_fill
    )

    "Market" -> ControllerNavIcon(RDesign.string.design_nav_icon_market, RDesign.string.design_nav_icon_market_fill)
    "market" -> ControllerNavIcon(RDesign.string.design_nav_icon_market, RDesign.string.design_nav_icon_market)
    "Production" -> ControllerNavIcon(
        RDesign.string.design_nav_icon_production,
        RDesign.string.design_nav_icon_production
    )

    "SbisBird" -> ControllerNavIcon(
        RDesign.string.design_nav_icon_sbis_bird,
        RDesign.string.design_nav_icon_sbis_bird_fill
    )

    "Vacancy" -> ControllerNavIcon(RDesign.string.design_nav_icon_vacansy, RDesign.string.design_nav_icon_vacansy)
    "hr" -> ControllerNavIcon(RDesign.string.design_nav_icon_vacansy, RDesign.string.design_nav_icon_vacansy)
    "Presto" -> ControllerNavIcon(
        RDesign.string.design_nav_icon_presto,
        RDesign.string.design_nav_icon_presto_fill
    )

    "presto" -> ControllerNavIcon(RDesign.string.design_nav_icon_presto, RDesign.string.design_nav_icon_presto_fill)
    "Money" -> ControllerNavIcon(RDesign.string.design_nav_icon_money, RDesign.string.design_nav_icon_money_fill)
    "Answers" -> ControllerNavIcon(RDesign.string.design_nav_icon_answers, RDesign.string.design_nav_icon_answers)
    "discussions" -> ControllerNavIcon(RDesign.string.design_nav_icon_answers, RDesign.string.design_nav_icon_answers)
    "answers" -> ControllerNavIcon(RDesign.string.design_nav_icon_answers, RDesign.string.design_nav_icon_answers)
    "Staff" -> ControllerNavIcon(RDesign.string.design_nav_icon_staff, RDesign.string.design_nav_icon_staff_fill)
    "staff" -> ControllerNavIcon(RDesign.string.design_nav_icon_staff, RDesign.string.design_nav_icon_staff_fill)
    "groups" -> ControllerNavIcon(RDesign.string.design_nav_icon_staff, RDesign.string.design_nav_icon_staff_fill)
    "Selling" -> ControllerNavIcon(
        RDesign.string.design_nav_icon_selling,
        RDesign.string.design_nav_icon_selling_fill
    )

    "sales" -> ControllerNavIcon(RDesign.string.design_nav_icon_selling, RDesign.string.design_nav_icon_selling_fill)
    "Salon" -> ControllerNavIcon(RDesign.string.design_nav_icon_salon, RDesign.string.design_nav_icon_salon_fill)
    "booking" -> ControllerNavIcon(RDesign.string.design_nav_icon_salon, RDesign.string.design_nav_icon_salon_fill)
    "Calendar" -> ControllerNavIcon(
        RDesign.string.design_nav_icon_calendar,
        RDesign.string.design_nav_icon_calendar_fill
    )

    "calendar" -> ControllerNavIcon(
        RDesign.string.design_nav_icon_calendar,
        RDesign.string.design_nav_icon_calendar_fill
    )

    "Deeds" -> ControllerNavIcon(RDesign.string.design_nav_icon_work, RDesign.string.design_nav_icon_deeds_fill)
    "work" -> ControllerNavIcon(RDesign.string.design_nav_icon_work, RDesign.string.design_nav_icon_deeds_fill)
    "Accounting" -> ControllerNavIcon(
        RDesign.string.design_nav_icon_accounting,
        RDesign.string.design_nav_icon_accounting_fill
    )

    "accounting" -> ControllerNavIcon(
        RDesign.string.design_nav_icon_accounting,
        RDesign.string.design_nav_icon_accounting_fill
    )

    "Documents" -> ControllerNavIcon(
        RDesign.string.design_nav_icon_documents,
        RDesign.string.design_nav_icon_documents_fill
    )

    "documents" -> ControllerNavIcon(
        RDesign.string.design_nav_icon_documents,
        RDesign.string.design_nav_icon_documents_fill
    )

    "Company" -> ControllerNavIcon(
        RDesign.string.design_nav_icon_company,
        RDesign.string.design_nav_icon_company_fill
    )

    "contragents" -> ControllerNavIcon(
        RDesign.string.design_nav_icon_company,
        RDesign.string.design_nav_icon_company_fill
    )

    "Business" -> ControllerNavIcon(
        RDesign.string.design_nav_icon_business,
        RDesign.string.design_nav_icon_business
    )

    "business" -> ControllerNavIcon(RDesign.string.design_nav_icon_business, RDesign.string.design_nav_icon_business)
    "Channels" -> ControllerNavIcon(
        RDesign.string.design_nav_icon_channels,
        RDesign.string.design_nav_icon_channels_fill
    )

    "EditForm" -> ControllerNavIcon(
        RDesign.string.design_nav_icon_edit_form,
        RDesign.string.design_nav_icon_edit_form_fill
    )

    "Formats" -> ControllerNavIcon(RDesign.string.design_nav_icon_formats, RDesign.string.design_nav_icon_formats)
    "Help" -> ControllerNavIcon(RDesign.string.design_nav_icon_help, RDesign.string.design_nav_icon_help)
    "Inspections" -> ControllerNavIcon(
        RDesign.string.design_nav_icon_inspection,
        RDesign.string.design_nav_icon_inspection
    )

    "Models" -> ControllerNavIcon(RDesign.string.design_nav_icon_models, RDesign.string.design_nav_icon_models)
    "OfficeCheck" -> ControllerNavIcon(
        RDesign.string.design_nav_icon_office_check,
        RDesign.string.design_nav_icon_office_check
    )

    "Operations" -> ControllerNavIcon(
        RDesign.string.design_nav_icon_operations,
        RDesign.string.design_nav_icon_operations
    )

    "Patterns" -> ControllerNavIcon(
        RDesign.string.design_nav_icon_patterns,
        RDesign.string.design_nav_icon_patterns
    )

    "Quarantine" -> ControllerNavIcon(
        RDesign.string.design_nav_icon_quarantine,
        RDesign.string.design_nav_icon_quarantine
    )

    "Statistics" -> ControllerNavIcon(
        RDesign.string.design_nav_icon_statistics,
        RDesign.string.design_nav_icon_statistics_fill
    )

    "Subscribers" -> ControllerNavIcon(
        RDesign.string.design_nav_icon_subscribers,
        RDesign.string.design_nav_icon_subscribers
    )

    "Substitutions" -> ControllerNavIcon(
        RDesign.string.design_nav_icon_substitutions,
        RDesign.string.design_nav_icon_substitutions
    )

    "Businesses" -> ControllerNavIcon(
        RDesign.string.design_nav_icon_businesses,
        RDesign.string.design_nav_icon_businesses
    )

    "Heart" -> ControllerNavIcon(RDesign.string.design_nav_icon_heart, RDesign.string.design_nav_icon_heart_fill)
    "Map" -> ControllerNavIcon(RDesign.string.design_nav_icon_map, RDesign.string.design_nav_icon_map_fill)
    "News" -> ControllerNavIcon(RDesign.string.design_nav_icon_news, RDesign.string.design_nav_icon_news_fill)
    "Profile" -> ControllerNavIcon(
        RDesign.string.design_nav_icon_profile,
        RDesign.string.design_nav_icon_profile_fill
    )

    "Purchases" -> ControllerNavIcon(
        RDesign.string.design_nav_icon_purchases,
        RDesign.string.design_nav_icon_purchases_fill
    )

    "Marking" -> ControllerNavIcon(
        RDesign.string.design_nav_icon_marking,
        RDesign.string.design_nav_icon_marking_fill
    )

    "Bell" -> ControllerNavIcon(RDesign.string.design_nav_icon_bell, RDesign.string.design_nav_icon_bell_fill)
    "CbPlus" -> ControllerNavIcon(RDesign.string.design_nav_icon_cb_plus, RDesign.string.design_nav_icon_cb_plus)
    "Phone" -> ControllerNavIcon(RDesign.string.design_nav_icon_phone, RDesign.string.design_nav_icon_phone)
    "SabyDisk" -> ControllerNavIcon(
        RDesign.string.design_nav_icon_saby_disk,
        RDesign.string.design_nav_icon_saby_disk_fill
    )

    "Surveyors" -> ControllerNavIcon(
        RDesign.string.design_nav_icon_surveyors,
        RDesign.string.design_nav_icon_surveyors
    )

    "Queue" -> ControllerNavIcon(RDesign.string.design_nav_icon_queue, RDesign.string.design_nav_icon_queue)
    "Clients" -> ControllerNavIcon(
        RDesign.string.design_nav_icon_clients,
        RDesign.string.design_nav_icon_clients_fill
    )

    "Service" -> ControllerNavIcon(RDesign.string.design_nav_icon_service, RDesign.string.design_nav_icon_service)
    "Transport" -> ControllerNavIcon(
        RDesign.string.design_nav_icon_transport,
        RDesign.string.design_nav_icon_transport
    )

    "DiscountCard" -> ControllerNavIcon(
        RDesign.string.design_nav_icon_discount_card,
        RDesign.string.design_nav_icon_discount_card_fill
    )

    "ProfileSaby" -> ControllerNavIcon(
        RDesign.string.design_nav_icon_profile_saby,
        RDesign.string.design_nav_icon_profile_saby_fill
    )

    "Letter" -> ControllerNavIcon(RDesign.string.design_nav_icon_letter, RDesign.string.design_nav_icon_letter)
    "FieldWork" -> ControllerNavIcon(
        RDesign.string.design_nav_icon_field_work,
        RDesign.string.design_nav_icon_field_work_fill
    )

    "Sales" -> ControllerNavIcon(RDesign.string.design_nav_icon_sales, RDesign.string.design_nav_icon_bonus_fill)
    "Bonus" -> ControllerNavIcon(RDesign.string.design_nav_icon_sales, RDesign.string.design_nav_icon_bonus_fill)
    "Application" -> ControllerNavIcon(
        RDesign.string.design_nav_icon_application,
        RDesign.string.design_nav_icon_application
    )

    "Outbox" -> ControllerNavIcon(
        RDesign.string.design_nav_icon_outbox,
        RDesign.string.design_nav_icon_outbox_fill
    )

    "Inbox" -> ControllerNavIcon(RDesign.string.design_nav_icon_inbox, RDesign.string.design_nav_icon_inbox_fill)
    "Accepted" -> ControllerNavIcon(
        RDesign.string.design_nav_icon_accepted,
        RDesign.string.design_nav_icon_accepted_fill
    )

    "Analytics" -> ControllerNavIcon(
        RDesign.string.design_nav_icon_analytics,
        RDesign.string.design_nav_icon_analytics_fill
    )

    "CBRF" -> ControllerNavIcon(RDesign.string.design_nav_icon_cbrf, RDesign.string.design_nav_icon_cbrf_fill)
    "Collation" -> ControllerNavIcon(
        RDesign.string.design_nav_icon_collation,
        RDesign.string.design_nav_icon_collation_fill
    )

    "FNS" -> ControllerNavIcon(RDesign.string.design_nav_icon_fns, RDesign.string.design_nav_icon_fns_fill)
    "FSS" -> ControllerNavIcon(RDesign.string.design_nav_icon_fss, RDesign.string.design_nav_icon_fss)
    "MVD" -> ControllerNavIcon(RDesign.string.design_nav_icon_mvd, RDesign.string.design_nav_icon_mvd_fill)
    "PFR" -> ControllerNavIcon(RDesign.string.design_nav_icon_pfr, RDesign.string.design_nav_icon_pfr)
    "RPN" -> ControllerNavIcon(RDesign.string.design_nav_icon_rpn, RDesign.string.design_nav_icon_rpn_fill)
    "STAT" -> ControllerNavIcon(RDesign.string.design_nav_icon_stat, RDesign.string.design_nav_icon_stat_fill)
    "Support" -> ControllerNavIcon(
        RDesign.string.design_nav_icon_support,
        RDesign.string.design_nav_icon_support_fill
    )

    "FSRAR" -> ControllerNavIcon(RDesign.string.design_nav_icon_fsrar, RDesign.string.design_nav_icon_fsrar_fill)
    "OurCompany" -> ControllerNavIcon(
        RDesign.string.design_nav_icon_our_company,
        RDesign.string.design_nav_icon_our_company_fill
    )

    "Marketing" -> ControllerNavIcon(
        RDesign.string.design_nav_icon_marketing,
        RDesign.string.design_nav_icon_marketing
    )

    "Knowledge" -> ControllerNavIcon(
        RDesign.string.design_nav_icon_knowledge,
        RDesign.string.design_nav_icon_knowledge_fill
    )

    "Extra" -> ControllerNavIcon(RDesign.string.design_nav_icon_extra, RDesign.string.design_nav_icon_extra)
    "Call" -> ControllerNavIcon(RDesign.string.design_nav_icon_call, RDesign.string.design_nav_icon_call_fill)
    "Property" -> ControllerNavIcon(
        RDesign.string.design_nav_icon_property,
        RDesign.string.design_nav_icon_property
    )

    "Import" -> ControllerNavIcon(RDesign.string.design_nav_icon_import, RDesign.string.design_nav_icon_import)
    "Pack" -> ControllerNavIcon(RDesign.string.design_nav_icon_pack, RDesign.string.design_nav_icon_pack)
    "Relabelling" -> ControllerNavIcon(
        RDesign.string.design_nav_icon_relabelling,
        RDesign.string.design_nav_icon_relabelling
    )

    "WriteOff" -> ControllerNavIcon(
        RDesign.string.design_nav_icon_write_off,
        RDesign.string.design_nav_icon_write_off
    )

    "Showcase" -> ControllerNavIcon(
        RDesign.string.design_nav_icon_showcase,
        RDesign.string.design_nav_icon_showcase
    )

    "Reviews" -> ControllerNavIcon(
        RDesign.string.design_nav_icon_reviews,
        RDesign.string.design_nav_icon_reviews_fill
    )

    "Discount" -> ControllerNavIcon(
        RDesign.string.design_nav_icon_discount,
        RDesign.string.design_nav_icon_discount
    )

    "Events" -> ControllerNavIcon(RDesign.string.design_nav_icon_events, RDesign.string.design_nav_icon_events)
    "Payments" -> ControllerNavIcon(
        RDesign.string.design_nav_icon_payments,
        RDesign.string.design_nav_icon_payments
    )

    "TaxAccounting" -> ControllerNavIcon(
        RDesign.string.design_nav_icon_tax_accounting,
        RDesign.string.design_nav_icon_tax_accounting
    )

    "Timesheet" -> ControllerNavIcon(
        RDesign.string.design_nav_icon_timesheet,
        RDesign.string.design_nav_icon_timesheet_fill
    )

    "Vacation" -> ControllerNavIcon(
        RDesign.string.design_nav_icon_vacation,
        RDesign.string.design_nav_icon_vacation
    )

    "Wages" -> ControllerNavIcon(RDesign.string.design_nav_icon_wages, RDesign.string.design_nav_icon_wages_fill)
    "ClientChat" -> ControllerNavIcon(
        RDesign.string.design_nav_icon_client_chat,
        RDesign.string.design_nav_icon_client_chat_fill
    )

    "Minzdrav" -> ControllerNavIcon(
        RDesign.string.design_nav_icon_minzdrav,
        RDesign.string.design_nav_icon_minzdrav_fill
    )

    "Promokod" -> ControllerNavIcon(
        RDesign.string.design_nav_icon_promokod,
        RDesign.string.design_nav_icon_promokod_fill
    )

    "Screwdriver" -> ControllerNavIcon(
        RDesign.string.design_nav_icon_screwdriver,
        RDesign.string.design_nav_icon_screwdriver
    )

    "Courses" -> ControllerNavIcon(RDesign.string.design_nav_icon_courses, RDesign.string.design_nav_icon_courses)
    "Groups" -> ControllerNavIcon(RDesign.string.design_nav_icon_groups, RDesign.string.design_nav_icon_groups)
    "Meetings" -> ControllerNavIcon(
        RDesign.string.design_nav_icon_meetings,
        RDesign.string.design_nav_icon_meetings_fill
    )

    "Lock" -> ControllerNavIcon(RDesign.string.design_nav_icon_lock, RDesign.string.design_nav_icon_lock)
    "Unlock" -> ControllerNavIcon(RDesign.string.design_nav_icon_unlock, RDesign.string.design_nav_icon_unlock)
    "ChatBot" -> ControllerNavIcon(RDesign.string.design_nav_icon_chatbot, RDesign.string.design_nav_icon_chatbot)
    "Lunch" -> ControllerNavIcon(RDesign.string.design_nav_icon_lunch, RDesign.string.design_nav_icon_lunch)
    "Offer" -> ControllerNavIcon(RDesign.string.design_nav_icon_offer, RDesign.string.design_nav_icon_offer_fill)
    "SFR" -> ControllerNavIcon(RDesign.string.design_nav_icon_sfr, RDesign.string.design_nav_icon_sfr)
    "Corn" -> ControllerNavIcon(RDesign.string.design_nav_icon_corn, RDesign.string.design_nav_icon_corn)
    "Lenta" -> ControllerNavIcon(RDesign.string.design_nav_icon_lenta, RDesign.string.design_nav_icon_lenta)
    "Order" -> ControllerNavIcon(RDesign.string.design_nav_icon_order, RDesign.string.design_nav_icon_order_fill)
    "Stoplist" -> ControllerNavIcon(
        RDesign.string.design_nav_icon_stop_list,
        RDesign.string.design_nav_icon_stoplist_fill
    )

    "Cameras" -> ControllerNavIcon(
        RDesign.string.design_nav_icon_cameras,
        RDesign.string.design_nav_icon_cameras_fill
    )

    else -> null
}