package ui.dialog

import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.DialogWrapper
import common.Converter.convertToOutDataClass
import common.Converter.convertToRootDC
import common.GlobalConfig
import ui.component.getHorizontalLayoutPanel
import ui.component.getRadioButton
import ui.component.getVerticalLayoutPanel
import java.awt.Dimension
import java.io.File
import java.io.FileOutputStream
import javax.swing.*
import javax.swing.JScrollPane

class MainDialog(project: Project, selectedFolderPath: String) : DialogWrapper(true) {

    private val panel = getVerticalLayoutPanel(Dimension(400, 400))

    private val txtXml2 = JTextArea()
    private val marginTextArea = 5

    //For PATH
    private val btnSelectPath = JButton().apply {
        text = "Path"
        addActionListener {
            FileChooserDialog.Builder
                .addToSelectPath(selectedFolderPath)
                .addSelectPathListener {
                    txtPath.text = it.path
                }
                .build().openFileChooser(project)
        }
    }

    private val txtPath = JTextField().apply {
        text = selectedFolderPath
        preferredSize = Dimension(500, getFontMetrics(font).height + (marginTextArea * 3))
    }

    private val txtClassName = JTextField().apply {
        preferredSize = Dimension(150, getFontMetrics(font).height + (marginTextArea * 3))
    }

    //for properties
    private val radioButtonGroup = ButtonGroup()
    private val rdbResponse = radioButtonGroup.getRadioButton(text = "Request Data Class")
    private val rdbRequest = radioButtonGroup.getRadioButton(text = "Response Data Class")

    init {
        init()
        title = GlobalConfig.PANEL_TITLE
    }

    override fun createCenterPanel(): JComponent? {
        panel.add(
            getHorizontalLayoutPanel().apply {
                add(
                    JScrollPane(
                        txtXml2,
                        JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS
                    ).apply {
                        preferredSize = Dimension(500, 700)
                    })
                add(getVerticalLayoutPanel().apply {
                    add(rdbRequest)
                    add(rdbResponse)
                })
            }
        )
        panel.add(txtClassName)
        panel.add(getHorizontalLayoutPanel().apply {
            add(txtPath)
            add(btnSelectPath)
        })
        return panel
    }

    override fun doOKAction() {
        super.doOKAction()

        txtXml2.text.convertToRootDC(txtClassName.text).convertToOutDataClass().forEach {
            try {
                val outputFile = File(txtPath.text, "${it.className}.kt")
                val fos = FileOutputStream(outputFile,true)
                fos.close()
                outputFile.writeText(it.outClass)
            }catch (e:Exception){
                e.printStackTrace()
            }
        }
    }

}