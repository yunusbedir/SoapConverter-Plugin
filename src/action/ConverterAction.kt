package action

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.PlatformDataKeys
import ui.dialog.MainDialog

class ConverterAction : AnAction() {

    override fun actionPerformed(e: AnActionEvent) {
        val selectedFolderPath = e.getData(PlatformDataKeys.VIRTUAL_FILE)
        MainDialog(e.project!!,selectedFolderPath!!.path).showAndGet()

    }
}