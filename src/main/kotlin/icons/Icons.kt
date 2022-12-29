package icons

import com.intellij.ui.IconManager
import com.intellij.ui.JBColor
import javax.swing.Icon

object Icons {

    @JvmField
    val JsonFormatIcon: Icon = load("/icons/json_format_icon.svg")

    @JvmStatic
    fun load(path: String): Icon {
        return IconManager.getInstance().getIcon(path, Icons::class.java)
    }
}