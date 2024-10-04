package ru.tensor.sbis.appdesign.hallscheme

import android.app.Activity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.facebook.drawee.backends.pipeline.Fresco
import ru.tensor.sbis.appdesign.R
import ru.tensor.sbis.hallscheme.v2.HallSchemeV2
import ru.tensor.sbis.hallscheme.v2.business.model.Background
import ru.tensor.sbis.hallscheme.v2.business.model.HallSchemeModel
import ru.tensor.sbis.hallscheme.v2.business.model.hallschemeitem.HallSchemeItem
import ru.tensor.sbis.hallscheme.v2.data.HallSchemeItemDto
import ru.tensor.sbis.hallscheme.v2.data.HallSchemeModelDto
import ru.tensor.sbis.hallscheme.v2.mocks.Mocks
import kotlin.random.Random

/**
 * Демо экран схемы зала.
 *
 * @author aa.gulevskiy
 */
class HallSchemeActivity : Activity() {

    private val itemsCreators = listOf(Mocks.getTablesSameX())

    private var currentSchemeIndex = 0
    private val random = Random(currentSchemeIndex)

    private lateinit var hallScheme: HallSchemeV2

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_hall_scheme)

        hallScheme = findViewById(R.id.hall_scheme)
        refreshSchemeData()

        findViewById<View>(R.id.btn_refresh).setOnClickListener { refreshSchemeData() }
    }

    private fun refreshSchemeData() {
        hallScheme.show(generateHallSchemeModel(), object : HallSchemeV2.OnHallSchemeItemClickListener {
            override fun onItemClick(hallSchemeItem: HallSchemeItem) {
                Toast.makeText(this@HallSchemeActivity.applicationContext,
                        hallSchemeItem.name + " clicked", Toast.LENGTH_SHORT)
                        .show()
            }
        })
    }

    private fun generateHallSchemeModel(): HallSchemeModel {
        val background = Background(null, "", "",
                "http://goldofpackard.ru/wp-content/uploads/2015/03/map.png")
        val items = getItems()

        var left = 0
        var top = 0
        var right = 0
        var bottom = 0

        items.forEach {
            if (it.coordinateX < left) left = it.coordinateX
            if (it.coordinateY < top) top = it.coordinateY
            if (it.coordinateX > right) right = it.coordinateX
            if (it.coordinateY > bottom) bottom = it.coordinateY
        }

        // Добавляем расстояние чтоб элементы поместились на экране (в реальности правая нижняя координата приходит с сервера)
        val addedSpace = 200

        return hallScheme.getHallSchemeModel(
                HallSchemeModelDto(
                        "picture",
                        Background(
                                background.position,
                                background.repeat,
                                background.size,
                                background.url),
                        null,
                        top,
                        left,
                        bottom + addedSpace,
                        right + addedSpace,
                        false,
                        items))
    }

    private fun getItems(): List<HallSchemeItemDto> {
        return itemsCreators[getNextSchemeIndex()]
    }

    private fun getNextSchemeIndex(): Int {
        val nextSchemeIndex = random.nextInt(itemsCreators.lastIndex + 1)
        if (nextSchemeIndex == currentSchemeIndex) {
            return getNextSchemeIndex()
        } else {
            currentSchemeIndex = nextSchemeIndex
        }

        return currentSchemeIndex
    }
}