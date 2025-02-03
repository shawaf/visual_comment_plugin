package me.shawaf.visualcomment.utils

import com.intellij.openapi.project.Project
import java.nio.file.Paths


fun extractImagePath(project: Project, commentText: String): String {
    val match = """"image":\s*"([^"]+)"""".toRegex().find(commentText)
    val relativePath = match?.groups?.get(1)?.value ?: return ""

    // Convert relative path to absolute path using the project root
    val projectBaseDir = project.basePath ?: return relativePath
    return Paths.get(projectBaseDir, relativePath).toString()
}