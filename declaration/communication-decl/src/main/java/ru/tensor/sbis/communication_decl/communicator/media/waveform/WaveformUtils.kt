package ru.tensor.sbis.communication_decl.communicator.media.waveform

import android.util.Base64
import org.json.JSONObject
import java.nio.ByteBuffer

/**
 * Утилита для парсинга осциллограммы аудио сообщений.
 *
 * @author vv.chekurda
 */
object WaveformUtils {

    /**
     * При упаковке берем по четыре байта и пакуем в три.
     */
    fun encodeWaveform(data: ByteArray): ByteArray {
        if (data.size.rem(4) != 0) {
            throw IllegalArgumentException("Размер входного массива должен быть кратен 4")
        }
        val input = ByteBuffer.wrap(data)
        val output = ByteBuffer.allocate(data.size * 3 / 4)
        val bytesInput = ByteArray(4)
        val bytesOutput = ByteArray(3)
        while (input.hasRemaining()) {
            input.get(bytesInput)
            bytesOutput[0] = ((mask(bytesInput[0], 0b00111111) shl 2) or (mask(bytesInput[1], 0b00110000) shr 4)).toByte()
            bytesOutput[1] = ((mask(bytesInput[1], 0b00001111) shl 4) or (mask(bytesInput[2], 0b00111100) shr 2)).toByte()
            bytesOutput[2] = ((mask(bytesInput[2], 0b00000011) shl 6) or (mask(bytesInput[3], 0b00111111))).toByte()
            output.put(bytesOutput)
        }
        return output.array()
    }

    /**
     * При распаковке берем по три байта и распаковываем в четыре.
     */
    fun decodeWaveform(data: ByteArray): ByteArray {
        if (data.size.rem(3) != 0) {
            throw IllegalArgumentException("Размер входного массива должен быть кратен 3")
        }
        val input = ByteBuffer.wrap(data)
        val output = ByteBuffer.allocate(data.size * 4 / 3)
        val bytesInput = ByteArray(3)
        val bytesOutput = ByteArray(4)
        while (input.hasRemaining()) {
            input.get(bytesInput)
            bytesOutput[0] = (mask(bytesInput[0], 0b11111100) shr 2).toByte()
            bytesOutput[1] = ((mask(bytesInput[0], 0b00000011) shl 4) or (mask(bytesInput[1], 0b11110000) shr 4)).toByte()
            bytesOutput[2] = ((mask(bytesInput[1], 0b00001111) shl 2) or (mask(bytesInput[2], 0b11000000) shr 6)).toByte()
            bytesOutput[3] = mask(bytesInput[2], 0b00111111).toByte()
            output.put(bytesOutput)
        }
        return output.array()
    }

    /**
     * Получить осциилограмму из json объекта.
     */
    fun getWaveform(jsonObject: JSONObject): ByteArray {
        val waveform: ByteArray
        val waveformString = jsonObject.optString(OSCILLOGRAM_KEY)
        waveform = if (waveformString.isNotEmpty()) {
            val waveform6Bit = Base64.decode(waveformString, Base64.DEFAULT)
            // Согласно ТЗ данные осциллограммы будут представлены 6 битными значениями в количестве 340
            // (340 * 6 / 8 = 255 байт)
            val expectedBytes = 255
            if (waveform6Bit.size == expectedBytes) {
                decodeWaveform(waveform6Bit)
            } else {
                ByteArray(WAVEFORM_SIZE)
            }
        } else {
            ByteArray(WAVEFORM_SIZE)
        }
        return waveform
    }

    private fun mask(byte: Byte, mask: Int): Int =
        mask and byte.toUByte().toInt()
}

private const val OSCILLOGRAM_KEY = "oscillogram"

/**
 * Константа количества точек осциллограммы аудиосообщений.
 */
const val WAVEFORM_SIZE = 340