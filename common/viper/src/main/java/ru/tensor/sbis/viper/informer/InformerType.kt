package ru.tensor.sbis.viper.informer

/**
 * Перечисление типов информеров
 */
enum class InformerType {

    /**
     * Метод для отображения ошибки
     */
    FAILURE,

    /**
     * Информер об успехе
     */
    SUCCESS,

    /**
     * Акцентный-информер, цвета темы приложения
     */
    ACCENT
}