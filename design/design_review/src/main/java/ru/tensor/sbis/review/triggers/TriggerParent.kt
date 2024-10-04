package ru.tensor.sbis.review.triggers

/**
 * Родительский триггер, может содержать другие триггеры
 *
 * @author ma.kolpakov
 */
interface TriggerParent {

    /**@SelfDocumented**/
    val children: Array<out Trigger>

}
