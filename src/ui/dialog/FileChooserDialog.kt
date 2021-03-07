package ui.dialog

import com.intellij.openapi.fileChooser.FileChooser
import com.intellij.openapi.fileChooser.FileChooserDescriptor
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.LocalFileSystem
import com.intellij.openapi.vfs.VirtualFile

class FileChooserDialog private constructor(
    private val path: String?,
    private val selectedPathListener: ((virtualFile: VirtualFile) -> Unit)?
) {

    object Builder {
        private var toSelectPath: String? = null
        private var selectedPathListener: ((virtualFile: VirtualFile) -> Unit)? = null

        fun addToSelectPath(path: String): Builder {
            this.toSelectPath = path
            return this
        }

        fun addSelectPathListener(selectedPathListener: (virtualFile: VirtualFile) -> Unit): Builder {
            this.selectedPathListener = selectedPathListener
            return this
        }

        fun build(): FileChooserDialog {
            return FileChooserDialog(toSelectPath, selectedPathListener)
        }
    }

    fun openFileChooser(project: Project) {

        val toSelect: VirtualFile? = if (path == null) {
            null
        } else {
            LocalFileSystem.getInstance().refreshAndFindFileByPath(path)
        }

        val fileChooser = FileChooserDescriptor(
            false,
            true,
            false,
            false,
            false,
            false
        )
        fileChooser.title = "MyIdeaDemo Pick Directory"
        fileChooser.description = "My File chooser demo"
        FileChooser.chooseFile(fileChooser, project, toSelect) {
            selectedPathListener?.let { it1 -> it1(it) }
        }
    }
}