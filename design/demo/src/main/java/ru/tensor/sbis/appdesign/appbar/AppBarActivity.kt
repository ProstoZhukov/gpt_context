package ru.tensor.sbis.appdesign.appbar

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.facebook.drawee.view.SimpleDraweeView
import ru.tensor.sbis.appdesign.R
import ru.tensor.sbis.appdesign.databinding.ActivityAppBarBinding
import ru.tensor.sbis.design.toolbar.Toolbar
import ru.tensor.sbis.design.toolbar.appbar.SbisAppBarLayout
import ru.tensor.sbis.design.toolbar.appbar.offset.AlphaOffsetObserver
import ru.tensor.sbis.design.toolbar.appbar.setupWithSbisToolbar
import ru.tensor.sbis.design.toolbar.appbar.transition.restoreState
import ru.tensor.sbis.design.toolbar.appbar.transition.saveTransitionState

/**
 * Демо экран для демонстрации возможностей SbisAppBarLayout
 *
 * @author ma.kolpakov
 */
class AppBarActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val viewBinding = ActivityAppBarBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)

        val mainImage: SimpleDraweeView = findViewById(R.id.toolbar_mainImage)
        mainImage.hierarchy.setPlaceholderImage(null)

        findViewById<Toolbar>(R.id.toolbar_sbisToolbar).let {
            viewBinding.appBar.addOffsetObserver(
                AlphaOffsetObserver(
                    Pair(mainImage, true),
                    Pair(it.centerText, false)
                )
            )
        }

        viewBinding.appBar.restoreState(intent)
        viewBinding.appBar.setupWithSbisToolbar {
            setTitle("Суслова Мария Андреевна")
        }
    }

    fun onHeaderClicked(view: View) {
        view as SbisAppBarLayout
        Intent(this, AppBarActivity::class.java)
            .apply { view.model.saveTransitionState(this) }
            .run(::startActivity)
    }
}
