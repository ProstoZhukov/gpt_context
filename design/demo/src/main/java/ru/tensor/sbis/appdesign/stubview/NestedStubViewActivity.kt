package ru.tensor.sbis.appdesign.stubview

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import ru.tensor.sbis.appdesign.databinding.ActivityStubViewNestedBinding
import ru.tensor.sbis.design.stubview.StubViewMode

/**
 * Демо экран вложенного компонента заглишки.
 *
 * @author ma.kolpakov
 */
class NestedStubViewActivity : AppCompatActivity() {

    private lateinit var binding: ActivityStubViewNestedBinding

    private var minHeight = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStubViewNestedBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnToggle.setOnClickListener {
            minHeight = !minHeight
            binding.stubView.setMode(StubViewMode.BLOCK, minHeight)
        }
    }
}
