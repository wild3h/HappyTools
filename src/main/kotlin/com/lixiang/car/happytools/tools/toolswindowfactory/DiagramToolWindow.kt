package com.lixiang.car.happytools.tools.toolswindowfactory

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.JBPopupMenu
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowFactory
import com.intellij.ui.EditorComboBox
import com.intellij.ui.components.JBLabel
import com.intellij.ui.components.JBPanel
import com.intellij.ui.components.JBTextField
import com.intellij.ui.content.ContentFactory
import com.lixiang.car.happytools.tools.data.*
import com.lixiang.car.happytools.tools.util.*
import com.lixiang.car.happytools.tools.view.DateSelectorView
import com.lixiang.car.happytools.tools.view.MultiComboBox
import com.lixiang.car.happytools.tools.view.SequenceDiagramPanel
import kotlinx.coroutines.*
import org.jdesktop.swingx.JXComboBox
import org.jdesktop.swingx.JXDatePicker
import wu.seal.jsontokotlin.ui.jHorizontalLinearLayout
import java.awt.Dimension
import java.awt.event.ComponentEvent
import java.awt.event.ComponentListener
import javax.swing.*
import javax.swing.event.DocumentEvent
import javax.swing.event.DocumentListener


class DiagramToolWindow : ToolWindowFactory {

    private val rootView by lazy {
        JPanel()
    }
    private val sequenceDiagramPanel by lazy {
        SequenceDiagramPanel()
    }
    private val lifecycleSelector by lazy {
        MultiComboBox() {
            sequenceDiagramPanel.diagramDelegate.setDrawLifecycles(it.toList())
            sequenceDiagramPanel.repaint()
        }.apply {
            preferredSize = Dimension(200, 30)
        }
    }
    private val vinConfigPanel by lazy {
        val jbTextField = JBTextField()
        jbTextField.preferredSize = Dimension(150, 30)
        jbTextField.document.addDocumentListener(object : DocumentListener {
            override fun insertUpdate(e: DocumentEvent?) {
                updateVinConfig(jbTextField.text)
            }

            override fun removeUpdate(e: DocumentEvent?) {
                updateVinConfig(jbTextField.text)
            }

            override fun changedUpdate(e: DocumentEvent?) {
            }
        })
        jbTextField
    }

    private val vinConfigMap = HashMap<String, String>()
    var oldJob: Job? = null


    val format = "yyyy-MM-dd+HH:mm:ss.SSS"
    private val startJXDatePicker by lazy {
        DateSelectorView().apply {
            setFormats(format)
            preferredSize = Dimension(250, 30)
        }
    }

    private val endJXDatePicker by lazy {
        DateSelectorView().apply {
            setFormats(format)
            preferredSize = Dimension(250, 30)
        }
    }

    private val logTypeComboBox by lazy {
        JXComboBox(arrayOf("log_HUF_8155_android"))
    }

    private val wordsTextArea = JBTextField().apply {
        preferredSize = Dimension(300, 30)
    }

    private val wordsLine by lazy {
        jHorizontalLinearLayout {
            add(JBLabel("过滤词：(支持多个过滤词，需英文逗号隔开)").apply {
                minimumSize = Dimension(10, 30)
            })
            add(wordsTextArea)
        }
    }

    private val progressBar by lazy {
        JProgressBar().apply {
            preferredSize = Dimension(800, 50)
        }
    }

    private val carConfigsMap = HashMap<String, Array<String>>()

    init {
        carConfigsMap["M"] = arrayOf(
            "PROCESSER_820_AND",
            "PROCESSER_820_KERNEL",
            "PROCESSER_820_MCU",
            "PROCESSER_J6",
            "PROCESSER_ANR",
            "PROCESSER_TOMBSTONE",
            "PROCESSER_SYS",
            "DROPBOX",
            "BLUETOOTH",
            "MODEM_HEART_BEAT",
            "EGWPCAP",
            "TOUCH",
            "NPU",
            "noa",
            "VHAL",
            "cpu_monitor"
        )
        carConfigsMap["X"] = arrayOf(
            "log_HUR_8155_android",
            "log_HUF_8155_android",
            "scs_j5_log",
            "scs_g3_log",
            "log_HUR_oom",
            "log_HUR_rawdump",
            "log_HUR_adsp_subsys",
            "log_HUR_adsp",
            "log_5G_kernel",
            "log_5G_APNRT",
            "log_HUF_adsp_subsys",
            "log_HUF_adsp",
            "log_xcu_bms",
            "log_xcu_fbcm",
            "log_5G_APRT",
            "log_xcu_rbcm",
            "log_HUF_rawdump",
            "log_HUF_oom",
            "nvh_eol",
            "nvh_server",
            "res_mileage",
            "lisysm_uploader",
            "log_HUF_pcap",
            "log_HUR_5G_kernel",
            "log_HUR_pcap",
            "log_HUR_Klog",
            "log_HUF_Klog",
            "log_xcu_pstore",
            "log_xcu_pcap",
            "log_xcu_bluetooth",
            "log_xcu_kernel",
            "log_xcu_application",
            "log_xcu_service",
            "log_HUF_crash_event",
            "log_HUR_5G_APRT",
            "log_HUF_bluetooth",
            "log_HUF_crash_panic",
            "log_HUF_mcu",
            "log_HUF_kernel",
            "log_HUR_crash_event",
            "log_HUR_kernel",
            "log_HUF_touchbar",
            "log_HUF_crash_anr",
            "log_xcu_debug",
            "log_xcu_coredump",
            "log_xcu_mcu",
            "log_HUR_crash_panic",
            "log_HUR_crash_anr",
            "log_HUR_dropbox",
            "log_HUF_roofbar",
            "log_HUF_amp",
            "log_HUR_bluetooth",
            "log_gbt32960",
            "log_HUF_dropbox",
            "log_HUR_crash_tmston",
            "log_HUR_crash_sys",
            "log_HUR_5G_APNRT",
            "log_fsdB",
            "log_fsdA",
            "log_HUF_hud",
            "log_HUF_crash_tmston",
            "log_xcu",
            "log_HUF_crash_sys"
        )
        carConfigsMap["NONE"] = arrayOf()
    }

    override fun createToolWindowContent(project: Project, toolWindow: ToolWindow) {
        val contentFactory = ContentFactory.SERVICE.getInstance()
        initRootView(project)
        val content = contentFactory.createContent(rootView, "", false)
        toolWindow.contentManager.addContent(content)
        rootView.addComponentListener(object : ComponentListener {
            override fun componentResized(p0: ComponentEvent?) {
                val newWidth = (p0?.component?.width ?: 0) - 50
                sequenceDiagramPanel.preferredSize = Dimension(newWidth, (p0?.component?.height ?: 0) - 150)
                SequenceDiagramPanel.MAX_WIDTH = newWidth
                SequenceDiagramPanel.MAX_HEIGHT = (p0?.component?.height ?: 0) - 150
                sequenceDiagramPanel.revalidate()
            }

            override fun componentMoved(p0: ComponentEvent?) {
            }

            override fun componentShown(p0: ComponentEvent?) {
            }

            override fun componentHidden(p0: ComponentEvent?) {
            }


        })
    }

    private fun initRootView(project: Project) {
        val run = JButton("run").apply {
            this.addActionListener {
                val config = LogConfigBeans(
                    wordsTextArea.text.split(','),
                    logTypeComboBox.selectedItem?.toString() ?: "",
                    endJXDatePicker.getFormatDate(format),
                    startJXDatePicker.getFormatDate(format),
                    vinConfigPanel.text
                )
                DownloadManager.download(project, config, 1, sequenceDiagramPanel, onSuccess = {
                    lifecycleSelector.setValues(arrayListOf<String>().apply {
                        add("全选")
                        addAll(sequenceDiagramPanel.diagramDelegate.getDrawLifecycles().map { it.element.className })
                    }.toTypedArray())
                }, onProgress = {
                    progressBar.isVisible = it in 0 until 100
                    progressBar.value = it
                })

            }
        }
        val springLayout = SpringLayout()
        rootView.layout = springLayout
        val vinTitle = JBLabel("VIN").apply {
            preferredSize = Dimension(40, 30)
        }
        val startTitle = JBLabel("开始时间：").apply {
            minimumSize = Dimension(60, 30)
        }
        val endTitle = JBLabel("  结束时间：").apply {
            minimumSize = Dimension(60, 30)
        }
        val timeLine = jHorizontalLinearLayout {
            add(startTitle)
            add(startJXDatePicker)
            add(endTitle)
            add(endJXDatePicker)
        }
        val logTypeTitle = JBLabel("业务类型：").apply {
            preferredSize = Dimension(80, 30)
        }
        rootView.add(
            sequenceDiagramPanel,
            run,
            lifecycleSelector,
            vinTitle,
            vinConfigPanel,
            timeLine,
            logTypeTitle,
            logTypeComboBox,
            wordsLine,
            progressBar
        )

        val vinCons = springLayout.getConstraints(vinTitle)
        vinCons.x = Spring.constant(20)
        vinCons.y = Spring.constant(20)

        vinConfigPanel.leftToRight(vinTitle)
        vinConfigPanel.topToTop(vinTitle)
        logTypeTitle.leftToRight(vinConfigPanel)
        logTypeTitle.topToTop(vinConfigPanel)
        logTypeComboBox.leftToRight(logTypeTitle)
        logTypeComboBox.topToTop(logTypeTitle)

        timeLine.topToBottom(vinTitle)
        timeLine.leftToLeft(vinTitle)

        wordsLine.topToBottom(timeLine)
        wordsLine.leftToLeft(timeLine)

        run.topToBottom(wordsLine)
        run.leftToLeft(vinTitle)
        lifecycleSelector.leftToRight(run, 5)
        lifecycleSelector.topToTop(run)

        sequenceDiagramPanel.topToBottom(run, 20)
        sequenceDiagramPanel.leftToLeft(vinTitle)
        progressBar.topToTop(sequenceDiagramPanel)
        progressBar.leftToLeft(sequenceDiagramPanel)
    }
    private fun updateVinConfig(vin: String) {

        if (vinConfigMap.contains(vin)) {
            val vinType = vinConfigMap[vin]
            val array = carConfigsMap[vinType?.get(0).toString()]
            logTypeComboBox.model = DefaultComboBoxModel(array)
            return
        }
        if (oldJob?.isActive == true) {
            oldJob?.cancel()
        }
        oldJob = GlobalScope.launch {
            withContext(Dispatchers.IO){
                DownloadManager.requestUrl<VinConfig>(
                    "https://dip-data-msg-parsing-service.prod.k8s.chehejia.com/v1-0/msg-parsing/common/vehicles/pagination?pageNum=1&pageSize=100&vinContains=$vin",
                    object : TypeToken<BaseResp<VinConfig>>() {}.type
                ) {
                    vinConfigMap[vin] = it?.data?.list?.firstOrNull()?.vehSeriesNo ?: "NONE"
                    val vinType = vinConfigMap[vin]
                    val array = carConfigsMap[vinType?.get(0).toString()]
                    logTypeComboBox.model = DefaultComboBoxModel(array)
                }
            }
        }
    }
}