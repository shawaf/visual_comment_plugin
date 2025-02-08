package me.shawaf.visualcomment

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.command.WriteCommandAction
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.editor.Document
import com.intellij.openapi.project.Project
import me.shawaf.visualcomment.utils.extractImagePath

class VisualCommentAction : AnAction() {
    override fun actionPerformed(e: AnActionEvent) {
        val project = e.project ?: return
        val editor = e.getData(com.intellij.openapi.actionSystem.CommonDataKeys.EDITOR) ?: return

        val dialog = VisualCommentDialog(project, editor)

        if (dialog.showAndGet()) {
            val descriptionText = dialog.getDescriptionText()
            val savedImagePath = dialog.getImagePath()

            //Full Comment to Insert Including Text/Description and ImagePath
            val comment =
                "// VISUAL_COMMENT: {\"description\": \"$descriptionText\", \"image\": \"$savedImagePath\"}\n"

            insertVisualComment(project, editor, comment)

            // Show preview dialog after insertion
            VisualCommentPreview.showPreview(descriptionText, extractImagePath(project, comment))
        }
    }

    private fun insertVisualComment(project: Project, editor: Editor, comment: String) {
        val document: Document = editor.document

        WriteCommandAction.runWriteCommandAction(project) {
            document.insertString(editor.caretModel.offset, comment)
        }
    }
}
