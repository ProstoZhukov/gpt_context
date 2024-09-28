package ru.tensor.sbis.communication_decl.communicator.media.data

/**
 * Скорость воспроизведения мультимедии.
 *
 * @author da.zhukov
 */
enum class PlaybackSpeed(val value: Float, val text: String) {
    X1(1f, "1x"),
    X1_2(1.2f, "1.2x"),
    X1_5(1.5f, "1.5x"),
    X2(2f, "2x");

    /**
     * Следующая скорость воспроизведения мультимедии.
     */
    fun nextGradation(): PlaybackSpeed {
        val nextIndex = (values().indexOf(this) + 1) % values().size
        return values()[nextIndex]
    }
}