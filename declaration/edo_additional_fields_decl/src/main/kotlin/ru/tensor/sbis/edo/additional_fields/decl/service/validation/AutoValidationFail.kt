package ru.tensor.sbis.edo.additional_fields.decl.service.validation

import ru.tensor.sbis.list.view.item.AnyItem

/**
 * Ошибка автоматической валидации
 * Возникает, например, при попытке выполнить переход через ДЗЗ с незаполненными доп полями
 */
class AutoValidationFail(val itemToScroll: AnyItem)