import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.project.guessProjectDir
import com.intellij.psi.PsiDirectory
import com.intellij.psi.PsiManager

// TODO: to think about null project path processing
fun Project.findProjectBaseDirectory() : PsiDirectory {
    return this.guessProjectDir()?.let { projectBaseDir ->
        ApplicationManager.getApplication().runReadAction<PsiDirectory> {
            PsiManager.getInstance(this).findDirectory(projectBaseDir)
        }
    } ?: error("No project file path")
}