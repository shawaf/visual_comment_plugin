package me.shawaf.visualcomment

import com.intellij.openapi.ui.DialogWrapper
import java.awt.*
import javax.swing.*

class VisualCommentPreview(private val commentText: String, private val imagePath: String) :
    DialogWrapper(true) {

    init {
        title = "Visual Comment Preview"
        init()
    }

    override fun createCenterPanel(): JComponent {
        val panel = JPanel(BorderLayout())
        panel.add(JLabel("<html><b>$commentText</b></html>"), BorderLayout.NORTH)

        val imageIcon = ImageIcon(imagePath)
        val imageLabel = JLabel(imageIcon)

        panel.add(imageLabel, BorderLayout.CENTER)
        return panel
    }

    companion object {
        fun showPreview(commentText: String, imagePath: String) {
            VisualCommentPreview(commentText, imagePath).show()
        }
    }
}
