package ru.tensor.sbis.appdesign.stubview

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.Px
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.doOnLayout
import ru.tensor.sbis.appdesign.R
import ru.tensor.sbis.appdesign.databinding.ActivityStubViewBinding
import ru.tensor.sbis.design.SbisMobileIcon
import ru.tensor.sbis.design.stubview.*

/**
 * Активити для демонстрации заглушки
 *
 * @author ma.kolpakov
 */
class StubViewActivity : AppCompatActivity() {

    private lateinit var binding: ActivityStubViewBinding
    private lateinit var stubViews: List<StubView>

    @Px
    private var initialContainerHeight = 0

    @Px
    private var minContainerHeight = 0

    @Px
    private val containerResizeStep = 72

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStubViewBinding.inflate(layoutInflater)
        setContentView(binding.root)

        stubViews = listOf(
            binding.stubViewDefault,
            binding.stubViewBlock,
            binding.stubViewSmall,
            binding.stubSbisIcon,
            binding.stubViewTexts,
            binding.stubViewCustom,
            binding.stubViewLongTexts,
            binding.stubViewLongTexts,
            binding.stubViewCustomColors,
        )

        setupHeights()
        setupChangeButtons()
        setupCaseButton()
        setupScaleButtons()
        setupStubsContent()
    }

    private fun setupStubsContent() {
        binding.stubViewDefault.setCase(StubViewCase.values()[0])
        binding.stubViewBlock.setCase(StubViewCase.values()[0])
        binding.stubViewCustomColors.setCase(StubViewCase.values()[0])

        val smallContent = ResourceImageStubContent(
            icon = R.drawable.ic_scale,
            messageRes = StubViewCase.NO_MESSAGES.messageRes,
            details = ""
        )
        binding.stubViewSmall.setContent(smallContent)

        val textsContent = ResourceImageStubContent(
            messageRes = StubViewCase.NO_MESSAGES.messageRes,
            detailsRes = StubViewCase.NO_MESSAGES.detailsRes,
        )
        binding.stubViewTexts.setContent(textsContent)

        val sbisIconContent = IconStubContent(
            icon = SbisMobileIcon.Icon.smi_AlertNull,
            iconColor = android.R.color.holo_red_dark,
            iconSize = android.R.dimen.app_icon_size,
            messageRes = StubViewCase.NO_MESSAGES.messageRes,
            detailsRes = StubViewCase.NO_MESSAGES.detailsRes,
        )
        binding.stubSbisIcon.setContent(sbisIconContent)

        val textView = TextView(baseContext).apply { text = "Custom Text" }
        val button = Button(baseContext).apply { text = "Custom Button" }
        val layoutLinearLayout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            addView(textView)
            addView(button)
        }
        val customContent = ViewStubContent(
            icon = layoutLinearLayout,
            messageRes = StubViewCase.NO_MESSAGES.messageRes,
            detailsRes = StubViewCase.NO_MESSAGES.detailsRes,
        )
        binding.stubViewCustom.setContent(customContent)

        val longTextsContent = ResourceImageStubContent(
            icon = StubViewCase.NO_MESSAGES.iconRes,
            messageRes = R.string.stub_view_long_title,
            detailsRes = R.string.stub_view_long_description
        )
        binding.stubViewLongTexts.setContent(longTextsContent)
    }

    private fun setupChangeButtons() {
        val showStub: (StubView) -> Unit = { stub ->
            stubViews.forEach { it.visibility = View.GONE }
            stub.visibility = View.VISIBLE
        }

        binding.btnDefault.setOnClickListener { showStub(binding.stubViewDefault) }
        binding.btnBlock.setOnClickListener { showStub(binding.stubViewBlock) }
        binding.btnSmall.setOnClickListener { showStub(binding.stubViewSmall) }
        binding.btnSbisIcon.setOnClickListener { showStub(binding.stubSbisIcon) }
        binding.btnTexts.setOnClickListener { showStub(binding.stubViewTexts) }
        binding.btnCustom.setOnClickListener { showStub(binding.stubViewCustom) }
        binding.btnLongTexts.setOnClickListener { showStub(binding.stubViewLongTexts) }
        binding.btnCustomColors.setOnClickListener { showStub(binding.stubViewCustomColors) }
    }

    private fun setupCaseButton() {
        var count = 0
        binding.btnCase.setOnClickListener {
            val values = StubViewCase.values()
            val index = ++count % values.size

            binding.stubViewDefault.setContent(values[index].getStubViewContent())
            binding.stubViewBlock.setContent(values[index].getStubViewContent())
            binding.stubViewCustomColors.setContent(values[index].getStubViewContent())
        }
    }

    private fun StubViewCase.getStubViewContent(): StubViewContent =
        when (this) {
            StubViewCase.NO_EVENTS -> {
                val meetingRes = R.string.design_stub_view_no_events_details_clickable_1
                val confRes = R.string.design_stub_view_no_events_details_clickable_2
                val meeting = getString(meetingRes)
                val conf = getString(confRes)

                getContent(mapOf(meetingRes to { toast(meeting) }, confRes to { toast(conf) }))
            }
            StubViewCase.NO_CONNECTION -> {
                val refreshRes = R.string.design_stub_view_no_connection_details_clickable
                val refresh = getString(refreshRes)

                getContent(mapOf(refreshRes to { toast(refresh) }))
            }
            else                                -> getContent()
        }

    private fun setupScaleButtons() {
        val stubViewContainer = binding.stubsContainer
        val changeHeight: (Int) -> Unit = {
            val params = stubViewContainer.layoutParams
            params.height = it
            stubViewContainer.layoutParams = params
        }

        binding.btnMinus.setOnClickListener {
            changeHeight((stubViewContainer.measuredHeight - containerResizeStep).coerceAtLeast(minContainerHeight))
        }

        binding.btnPlus.setOnClickListener {
            changeHeight((stubViewContainer.measuredHeight + containerResizeStep).coerceAtMost(initialContainerHeight))
        }

        binding.btnReset.setOnClickListener {
            changeHeight(ConstraintLayout.LayoutParams.MATCH_CONSTRAINT)
        }

        binding.btnHideButtons.setOnClickListener {
            if (binding.topButtonsVertical?.visibility == View.VISIBLE) {
                binding.topButtonsVertical?.visibility = View.GONE
            } else {
                binding.topButtonsVertical?.visibility = View.VISIBLE
            }
        }
    }

    private fun setupHeights() {
        minContainerHeight = resources.getDimensionPixelSize(R.dimen.stub_view_activity_min_container_height)

        binding.stubsContainer.doOnLayout {
            initialContainerHeight = it.measuredHeight
        }
    }

    private fun toast(text: String) =
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show()
}
