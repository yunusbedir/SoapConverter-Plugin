package ui.component

import com.intellij.ui.components.panels.HorizontalLayout
import com.intellij.ui.components.panels.VerticalLayout
import java.awt.Dimension
import javax.swing.ButtonGroup
import javax.swing.JPanel
import javax.swing.JRadioButton


fun getVerticalLayoutPanel(dimension: Dimension? = null) = JPanel().apply {
    if (dimension != null)
        preferredSize = dimension
    layout = VerticalLayout(3, 2)
}

fun getHorizontalLayoutPanel(dimension: Dimension? = null) = JPanel().apply {
    if (dimension != null)
        preferredSize = dimension
    layout = HorizontalLayout(3, 1)
}

fun ButtonGroup.getRadioButton(text: String) = JRadioButton().apply {
    this.text = text
    this@getRadioButton.add(this)
}
