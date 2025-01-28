package me.shawaf.visualflow

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.components.Service
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.editor.EditorFactory
import com.intellij.openapi.editor.event.EditorMouseEvent
import com.intellij.openapi.editor.event.EditorMouseListener
import com.intellij.openapi.editor.markup.GutterIconRenderer
import com.intellij.openapi.editor.markup.MarkupModel
import com.intellij.openapi.editor.markup.RangeHighlighter
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.popup.JBPopupFactory
import com.intellij.openapi.util.IconLoader
import com.intellij.ui.awt.RelativePoint
import java.awt.Color
import java.awt.Font
import java.awt.Image
import java.io.File
import javax.imageio.ImageIO
import javax.swing.ImageIcon
import javax.swing.JLabel
import javax.swing.JPanel
import javax.swing.JScrollPane
import javax.swing.JTextArea
import javax.swing.SwingConstants

@Service
class DiagramPreviewService(private val project: Project) {

    private val descriptions = mutableMapOf<Int, String>()
    private val diagrams = mutableMapOf<Int, String>() // Use a file path or base64 image for diagrams
    private val highlighters = mutableListOf<RangeHighlighter>()

    init {
        // Attach mouse listeners to editors
        EditorFactory.getInstance().eventMulticaster.addEditorMouseListener(object : EditorMouseListener {
            override fun mouseClicked(event: EditorMouseEvent) {
                val editor = event.editor
                val caretLine = editor.caretModel.logicalPosition.line
                if (descriptions.containsKey(caretLine)) {
                    showPopup(event, caretLine)
                }
            }
        }, project)
    }

    fun addDiagram(lineNumber: Int, description: String, diagramPath: String, editor: Editor) {
        descriptions[lineNumber] = description
        diagrams[lineNumber] = diagramPath

        println("Adding diagram: $description at line $lineNumber")
        println("Diagram path: $diagramPath")

        val markupModel: MarkupModel = editor.markupModel
        val highlighter = markupModel.addLineHighlighter(lineNumber, 0, null)

        val icon = IconLoader.getIcon("/icons/diagram_icon.svg", javaClass)
        highlighter.gutterIconRenderer = object : GutterIconRenderer() {
            override fun getIcon() = icon
            override fun getTooltipText() = "Click to view diagram"
            override fun equals(other: Any?) = false
            override fun hashCode() = lineNumber.hashCode()
            override fun getClickAction(): AnAction? {
                return object : AnAction() {
                    override fun actionPerformed(e: AnActionEvent) {
                        showPopupAtLine(editor, lineNumber)
                    }
                }
            }
        }

        highlighters.add(highlighter)
    }

    private fun showPopup(event: EditorMouseEvent, lineNumber: Int) {
        val description = descriptions[lineNumber] ?: return
        val diagramPath = diagrams[lineNumber] ?: return

        val contentPanel = JPanel()
        contentPanel.layout = null

        // Create the description label with word wrapping
        val descriptionLabel = JLabel("<html><b>Description:</b><br>$description</html>", SwingConstants.LEFT)
        descriptionLabel.setBounds(10, 10, 300, 100) // Increase the height for wrapping text
        descriptionLabel.isOpaque = false
        contentPanel.add(descriptionLabel)

        // Set the description label to allow word wrapping
        descriptionLabel.apply {
            font = Font("Arial", Font.PLAIN, 12)
            foreground = Color.BLACK
            val textArea = JTextArea(description)
            textArea.isEditable = false
            textArea.lineWrap = true
            textArea.wrapStyleWord = true
            textArea.font = Font("Arial", Font.PLAIN, 12)
            textArea.foreground = Color.BLACK
            textArea.background = contentPanel.background

            // Adjust size of JTextArea dynamically based on content
            val textAreaScrollPane = JScrollPane(textArea)
            textAreaScrollPane.setBounds(10, 10, 300, 100)
            contentPanel.add(textAreaScrollPane)
        }

        // Load the image from the file
        val imageFile = File("/Users/mohamedelshawaf/Downloads/diagram-export-1-22-2025-6_21_00-PM.png")
        val image: Image? = if (imageFile.exists()) ImageIO.read(imageFile) else null

        if (image != null) {
            val diagramLabel = JLabel(ImageIcon(image.getScaledInstance(400, 300, Image.SCALE_SMOOTH))) // Adjust size
            diagramLabel.setBounds(10, 120, 400, 300) // Position below the description label
            contentPanel.add(diagramLabel)
        } else {
            val errorLabel = JLabel("<html><b>Error:</b> Unable to load diagram image!</html>", SwingConstants.LEFT)
            errorLabel.setBounds(10, 120, 300, 50)
            contentPanel.add(errorLabel)
        }

        // Show the popup
        JBPopupFactory.getInstance()
            .createComponentPopupBuilder(contentPanel, null)
            .setTitle("Diagram Preview")
            .createPopup()
            .show(RelativePoint(event.mouseEvent.component, event.mouseEvent.point))
        println("Showing popup for line: $lineNumber")
    }

    private fun showPopupAtLine(editor: com.intellij.openapi.editor.Editor, lineNumber: Int) {
        val description = descriptions[lineNumber] ?: return
        val diagramPath = diagrams[lineNumber] ?: return

        val contentPanel = JPanel()
        contentPanel.layout = null

        val descriptionLabel = JLabel("<html><b>Description:</b><br>$description</html>", SwingConstants.LEFT)
        descriptionLabel.setBounds(10, 10, 300, 50)
        contentPanel.add(descriptionLabel)

        // Load the image from the file
        val imageFile = File(diagramPath)
        val image: Image? = if (imageFile.exists()) ImageIO.read(imageFile) else null

        if (image != null) {
            val diagramLabel = JLabel(ImageIcon(image.getScaledInstance(400, 300, Image.SCALE_SMOOTH))) // Adjust size
            diagramLabel.setBounds(10, 70, 400, 300)
            contentPanel.add(diagramLabel)
        } else {
            val errorLabel = JLabel("<html><b>Error:</b> Unable to load diagram image!</html>", SwingConstants.LEFT)
            errorLabel.setBounds(10, 70, 300, 50)
            contentPanel.add(errorLabel)
        }

        val caretPosition = editor.visualPositionToXY(editor.caretModel.visualPosition)
        JBPopupFactory.getInstance()
            .createComponentPopupBuilder(contentPanel, null)
            .setTitle("Diagram Preview")
            .createPopup()
            .show(RelativePoint(editor.contentComponent, caretPosition))
        println("Showing popup for line: $lineNumber")

    }

    fun clearDiagrams() {
        descriptions.clear()
        diagrams.clear()
        highlighters.forEach { it.dispose() }
        highlighters.clear()
    }
}