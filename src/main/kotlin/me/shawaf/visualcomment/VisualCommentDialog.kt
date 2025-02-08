package me.shawaf.visualcomment

import com.intellij.openapi.fileChooser.FileChooser
import com.intellij.openapi.fileChooser.FileChooserDescriptor
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.DialogWrapper
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.util.ui.JBUI
import me.shawaf.visualcomment.utils.Save_Folder_Name
import java.awt.*
import java.nio.file.Files
import java.nio.file.Paths
import javax.swing.*

class VisualCommentDialog(val project: Project, private val editor: com.intellij.openapi.editor.Editor) :
    DialogWrapper(project) {

    private val descriptionField = JTextField(30)
    private val imageLabel = JLabel("No Image Selected")
    private var selectedImagePath: String? = null

    init {
        init()
        title = "Add Visual Comment"
    }

    override fun createCenterPanel(): JComponent {
        val panel = JPanel(GridBagLayout())
        val gbc = GridBagConstraints()
        gbc.insets = JBUI.insets(5)

        gbc.gridx = 0
        gbc.gridy = 0
        panel.add(JLabel("Description:"), gbc)

        gbc.gridx = 1
        panel.add(descriptionField, gbc)

        val browseButton = JButton("Select Image").apply {

            addActionListener {
                val selectedFile = openFileChooser()
                selectedFile?.let {
                    selectedImagePath = saveImageToProjectFolder(project = project, selectedFile.path)
                    imageLabel.text = "Image Selected Path: $selectedImagePath"
                    imageLabel.invalidate()
                }
            }
        }

        gbc.gridx = 0
        gbc.gridy = 1
        panel.add(browseButton, gbc)

        gbc.gridx = 1
        panel.add(imageLabel, gbc)

        return panel
    }

    private fun openFileChooser(): VirtualFile? {
        // Open file chooser to select an image
        val fileChooserDescriptor = FileChooserDescriptor(true, false, false, false, false, false)
        val selectedFile = FileChooser.chooseFile(fileChooserDescriptor, project, null)
        return selectedFile
    }

    private fun saveImageToProjectFolder(project: Project, imagePath: String): String {
        val projectBaseDir = project.basePath ?: return imagePath  // Get the project root directory
        val folderPath = Paths.get(projectBaseDir, Save_Folder_Name)

        // Ensure the folder exists
        if (!Files.exists(folderPath)) {
            Files.createDirectory(folderPath)
        }

        val fileName = Paths.get(imagePath).fileName.toString() // Extract only the filename
        val newImagePath = folderPath.resolve(fileName) // Save inside project folder

        // Copy the image from the selected path to the project folder
        Files.copy(Paths.get(imagePath), newImagePath)

        // Return a **relative path** instead of an absolute one
        return "$Save_Folder_Name/$fileName"
    }

    fun getDescriptionText(): String = descriptionField.text
    fun getImagePath(): String = selectedImagePath ?: ""
}
