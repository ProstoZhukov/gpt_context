package ru.tensor.sbis.design_dialogs.dialogs.container.util.immersive_mode

/**
 * Вью режима погружения
 *
 * @author sa.nikitin
 */
interface ImmersiveModeView {

    /**
     * Сменить видимость управляющих элементов
     *
     * @param changeWithAnim Следует ли сменить с анимацией
     */
    fun changeControlsVisibility(isVisible: Boolean, changeWithAnim: Boolean)
}