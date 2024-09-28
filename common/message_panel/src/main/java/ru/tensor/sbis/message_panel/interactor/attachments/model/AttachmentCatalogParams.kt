package ru.tensor.sbis.message_panel.interactor.attachments.model

/**
 * Параметры, описывающие объект бизнес-логики
 *
 * @param blObjectName название объекта бизнес-логики
 * @param cloudObjectId id объекта на облаке
 *
 * @author vv.chekurda
 */
data class AttachmentCatalogParams(val blObjectName: String, val cloudObjectId: String? = null)