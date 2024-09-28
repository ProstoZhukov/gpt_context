package ru.tensor.sbis.person_decl.employee.card_configuration_common

import android.os.Parcelable

/**
 * Конфигурация раздела о месте работы.
 *
 * @author ra.temnikov
 */
interface WorkInfoConfiguration : Parcelable {
    /**
     * Возможность включить/выключить показ статуса отпуска/больничного/увольнения сотрудника.
     */
    val isUnavailabilityStatusVisible: Boolean

    /**
     * Возможность включить/выключить показ должности сотрудника.
     */
    val isWorkPositionVisible: Boolean

    /**
     * Возможность включить/выключить переход в трудовую книжку сотрудника.
     */
    val isRecordBookScreenAvailable: Boolean

    /**
     * Возможность включить/выключить показ подразделения.
     */
    val isDepartmentVisible: Boolean

    /**
     * Возможность включить/выключить показ продолжительности работы.
     */
    val isExperienceVisible: Boolean


    /**
     * Флаг для получения информации о недоступности всего контента блока.
     * true, если все конфигурации влияющие на отображение контента выключены.
     */
    val everythingIsUnavailable get() = isUnavailabilityStatusVisible.not() &&
        isWorkPositionVisible.not() && isDepartmentVisible.not() && isExperienceVisible.not()
}