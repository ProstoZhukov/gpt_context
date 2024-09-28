package ru.tensor.sbis.review

import ru.tensor.sbis.review.action.DialogReviewAction
import ru.tensor.sbis.review.action.GooglePlayReviewAction
import ru.tensor.sbis.review.action.NoneReviewAction
import ru.tensor.sbis.review.action.ReviewAction
import ru.tensor.sbis.review.action.ToastReviewAction

/**
 * @author ma.kolpakov
 */
enum class ReviewType(internal val action: ReviewAction) {

    /**
     * Сервис оценок из Гугл плей
     */
    GOOGLE(GooglePlayReviewAction()),

    /**
     * Вместо сервиса оценок будет показан диалог предлагающий оценить приложение
     */
    DEMO(DialogReviewAction()),

    /**
     * Вместо сервиса оценок будет показан "toast" предлагающий оценить приложение
     */
    DEMO_TOAST(ToastReviewAction()),

    /**
     * При срабатывании положительного сценария ни чего не произойдет
     */
    NONE(NoneReviewAction()),
    /**
     * В будущем появится реализация для устройств без гугл-плей сервисов
     */
    // TODO: 16.08.2021 Реализовать запрос оценки приложения на устройствах HUAWAI https://online.sbis.ru/opendoc.html?guid=52c715a0-2355-42cc-83b2-16639acc4998
    // HUAWAI

}
