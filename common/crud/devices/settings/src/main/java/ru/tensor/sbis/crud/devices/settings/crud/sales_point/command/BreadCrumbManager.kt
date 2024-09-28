package ru.tensor.sbis.crud.devices.settings.crud.sales_point.command

/**
 * Менеджер хлебных крошек.
 * Решает проблему невозможности определения иерархии из полученной порции данных непосредственно от источника данных.
 * Пользователю необходимо - при определенном пользовательском действии (переходе на другой уровень) позвать метод
 * [changeMovingState] с соответствующим переходу [MovingState], а при обработке результата запроса вычислить текущее состояние
 * хлебных крошек в методе [proceed].
 */
interface BreadCrumbManager {

    /**
     * Хлебные крошки
     */
    val breadCrumbs: MutableList<BreadCrumb>

    /**
     * Изменить состояние движения по хлебным крошкам. Следующая операция движения будет выполнена с учетом этого состояния.
     */
    fun changeMovingState(movingState: MovingState)

    /**
     * Продвинуться по хлебным крошкам, с учетом направления, выбранного вызовом [changeMovingState].
     * В результате работы метода цепочка хлебных крошек будет продолжена, или укорочена.
     * Вызов [changeMovingState] перед [proceed] обязателен, иначе ничего не произойдет.
     */
    fun proceed(id: String?, title: String?)

    /**
     * Перечисление состояний движения по хлебным крошкам.
     * @see proceed
     */
    enum class MovingState {
        /**
         * Бездействие
         */
        IDLE,

        /**
         * Ожидается движение вперед
         */
        MOVE_FORWARD,

        /**
         * Ожидается движение назад
         */
        MOVE_BACKWARD,

        /**
         * Ожидается движение к корню иерархии
         */
        MOVE_TO_START
    }
}

/**
 * Реализация менеджера хлебных крошек.
 * Реализует логику хранения данных о хлебных крошках и возвращает их иерархию в зависимости от состояния.
 */
class BreadCrumbManagerImpl: BreadCrumbManager {

    override val breadCrumbs: MutableList<BreadCrumb> = mutableListOf()

    private var movingState: BreadCrumbManager.MovingState = BreadCrumbManager.MovingState.IDLE

    override fun changeMovingState(movingState: BreadCrumbManager.MovingState) {
        this.movingState = movingState
    }

    override fun proceed(id: String?, title: String?) {
        if (id == null && title == null && movingState != BreadCrumbManager.MovingState.MOVE_TO_START) {
            tryStepBack()
            return
        }
        when (movingState) {
            BreadCrumbManager.MovingState.IDLE         -> { /*just idle*/ }
            BreadCrumbManager.MovingState.MOVE_FORWARD  -> tryStepForward(BreadCrumb(id, title))
            BreadCrumbManager.MovingState.MOVE_BACKWARD -> tryStepBack()
            BreadCrumbManager.MovingState.MOVE_TO_START -> clearBreadcrumbs()
        }
        resetMovingState()
    }

    private fun tryStepForward(crumb: BreadCrumb) {
        if (!breadCrumbs.contains(crumb)) {
            breadCrumbs.add(0, crumb)
        }
    }

    private fun tryStepBack() {
        if (breadCrumbs.isNotEmpty()) {
            breadCrumbs.removeFirst()
        }
    }

    private fun clearBreadcrumbs() {
        breadCrumbs.clear()
    }

    private fun resetMovingState() {
        movingState = BreadCrumbManager.MovingState.IDLE
    }
}

/**
 * Класс для трансфера хлебных крошек
 */
data class BreadCrumb(
    val id: String?,
    val title: String?)