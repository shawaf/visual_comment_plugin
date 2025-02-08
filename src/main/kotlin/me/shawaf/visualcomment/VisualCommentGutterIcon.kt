package me.shawaf.visualcomment

import com.intellij.codeInsight.daemon.LineMarkerInfo
import com.intellij.codeInsight.daemon.LineMarkerProvider
import com.intellij.openapi.editor.markup.GutterIconRenderer
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiComment
import com.intellij.icons.AllIcons
import me.shawaf.visualcomment.utils.extractImagePath
import javax.swing.Icon

/**
 * Adds a gutter icon beside visualized comments in the editor.
 */
class VisualCommentGutterIcon : LineMarkerProvider {

    override fun getLineMarkerInfo(element: PsiElement): LineMarkerInfo<*>? {
        if (element is PsiComment && isVisualComment(element.text)) {
            return createLineMarker(element)
        }
        return null
    }

    private fun isVisualComment(commentText: String): Boolean {
        return commentText.startsWith("// VISUAL_COMMENT")
    }

    private fun createLineMarker(element: PsiComment): LineMarkerInfo<PsiElement> {
        return LineMarkerInfo(
            element,
            element.textRange,
            getIcon(),
            { "View Visual Comment" },
            { _, _ -> openCommentPreview(element) },
            GutterIconRenderer.Alignment.RIGHT,
            { "Visual Comment" }
        )
    }

    private fun openCommentPreview(element: PsiComment) {
        val project = element.project
        val commentText = extractDescription(element.text)
        val imagePath = extractImagePath(project, element.text)  // Use project-based image path
        VisualCommentPreview.showPreview(commentText, imagePath)
    }

    private fun extractDescription(commentText: String): String {
        val match = """"description":\s*"([^"]+)"""".toRegex().find(commentText)
        return match?.groups?.get(1)?.value ?: "No description available."
    }

    private fun getIcon(): Icon = AllIcons.General.Inline_edit  // Use built-in IntelliJ icon
}