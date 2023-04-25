package com.lixiang.car.happytools.tools.view

import com.intellij.ui.components.JBLabel
import com.lixiang.car.happytools.tools.data.DownloadManager
import com.lixiang.car.happytools.tools.entity.SequenceDiagramElement
import java.awt.Dimension
import java.awt.Graphics
import java.awt.datatransfer.DataFlavor
import java.awt.datatransfer.UnsupportedFlavorException
import java.io.File
import java.io.IOException
import javax.swing.JButton
import javax.swing.JPanel
import javax.swing.TransferHandler
import kotlin.math.max

class FileDropView(val getKeyWords: () -> List<String>, onSuccess: (listData: ArrayList<SequenceDiagramElement>) -> Unit, onProgress: ((Int) -> Unit)?) : JPanel() {
    private val defaultText = "如果过滤本地*.log文件，请将文件拖拽到此处"
    private val textArea: JBLabel = JBLabel(defaultText)
    private val minWidth = 200
    private val dropFileList = mutableListOf<String>()

    init {
        // 创建一个文本区域
        add(textArea)
        add(JButton("过滤本地文件，运行点这里").apply {
            addActionListener {
                DownloadManager.analysisLog(dropFileList, keyWords = getKeyWords(), onSuccess = onSuccess, onProgress = onProgress)
            }
        })
        add(JButton("清空").apply {
            addActionListener {
                textArea.text = defaultText
                dropFileList.clear()
            }
        })
        // 定义 TransferHandler
        val th: TransferHandler = object : TransferHandler() {
            override fun canImport(support: TransferSupport): Boolean {
                return support.isDataFlavorSupported(DataFlavor.javaFileListFlavor)
            }

            override fun importData(support: TransferSupport): Boolean {
                if (!canImport(support)) {
                    return false
                }
                val transferable = support.transferable
                try {
                    val fileList = transferable.getTransferData(DataFlavor.javaFileListFlavor) as List<File>
                    for (file in fileList) {
                        val absolutePath = file.absolutePath
                        dropFileList.add(absolutePath)
                    }
                    textArea.text = "已选中${dropFileList.size}个文件"
                } catch (e: UnsupportedFlavorException) {
                    return false
                } catch (e: IOException) {
                    return false
                }
                return true
            }
        }

        // 设置 TransferHandler
        transferHandler = th
        isVisible = true
    }

    override fun paintComponent(g: Graphics?) {
        super.paintComponent(g)
        g?.drawRoundRect(0, 0, width, height, 10, 10)
    }

    override fun getPreferredSize(): Dimension {
        return Dimension(max(super.getPreferredSize().width, textArea.preferredSize.width).coerceAtLeast(minWidth), super.getPreferredSize().height)
    }
}