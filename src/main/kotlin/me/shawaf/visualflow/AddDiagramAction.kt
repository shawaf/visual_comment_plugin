package me.shawaf.visualflow
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.editor.EditorFactory
import com.intellij.openapi.ui.Messages
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.popup.JBPopupFactory
import com.intellij.openapi.util.TextRange
import java.awt.BorderLayout
import javax.swing.ImageIcon
import javax.swing.JComponent
import javax.swing.JLabel
import javax.swing.JPanel
import javax.swing.JTextArea

class AddDiagramAction : AnAction() {
    override fun actionPerformed(e: AnActionEvent) {
        val editor = e.getData(CommonDataKeys.EDITOR) ?: return
        val caretModel = editor.caretModel
        val offset = caretModel.offset
        val document = editor.document
        val lineNumber = document.getLineNumber(offset)
        val lineStart = document.getLineStartOffset(lineNumber)
        val lineEnd = document.getLineEndOffset(lineNumber)
        val commentLine = document.getText(TextRange(lineStart, lineEnd))

        // Parse the comment
        val (description, imagePath) = parseCustomComment(commentLine)

        // Show popup
        showPopup(description, imagePath, e)
    }

    private fun parseCustomComment(comment: String): Pair<String, String> {
        val regex = Regex("//@CustomComment:description=(.*?);image=(.*)")
        val matchResult = regex.find(comment)
        return if (matchResult != null) {
            val (description, imagePath) = matchResult.destructured
            description to imagePath
        } else {
            "No description" to ""
        }
    }

    private fun showPopup(description: String, imagePath: String, e: AnActionEvent) {
        val project = e.project ?: return
        val popup = JBPopupFactory.getInstance().createComponentPopupBuilder(
            createPopupContent(description, imagePath), null
        ).createPopup()
        popup.showInBestPositionFor(e.dataContext)
    }

    private fun createPopupContent(description: String, imagePath: String): JComponent {
        val panel = JPanel(BorderLayout())
        val textArea = JTextArea(description)
        textArea.isEditable = false
        panel.add(textArea, BorderLayout.CENTER)

        if (imagePath.isNotEmpty()) {
            val imageIcon = ImageIcon(imagePath)
            val imageLabel = JLabel(imageIcon)
            panel.add(imageLabel, BorderLayout.SOUTH)
        }

        return panel
    }
}