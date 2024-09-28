package ru.tensor.sbis.pdf_viewer.decl

/**
 * Интерфейс для реализации логики обновления аргументов дочернего фрагмента.
 * см. PdfViewerHostFragment
 * Обновление пути к файлу pdf, если дочерний фрагмент существует
 */
interface PdfViewerArgsUpdatable {

    fun updateArgs(args: PdfViewerArgs)
}