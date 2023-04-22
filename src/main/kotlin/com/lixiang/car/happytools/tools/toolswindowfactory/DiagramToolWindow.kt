package com.lixiang.car.happytools.tools.toolswindowfactory

import com.google.gson.reflect.TypeToken
import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowFactory
import com.intellij.ui.components.JBLabel
import com.intellij.ui.components.JBScrollBar
import com.intellij.ui.components.JBScrollPane
import com.intellij.ui.components.JBTextField
import com.intellij.ui.content.ContentFactory
import com.intellij.util.containers.toArray
import com.lixiang.car.happytools.tools.data.*
import com.lixiang.car.happytools.tools.util.*
import com.lixiang.car.happytools.tools.view.DateSelectorView
import com.lixiang.car.happytools.tools.view.MultiComboBox
import com.lixiang.car.happytools.tools.view.SequenceDiagramPanel
import com.lixiang.car.happytools.tools.view.ToolTextField
import kotlinx.coroutines.*
import org.jdesktop.swingx.JXComboBox
import wu.seal.jsontokotlin.ui.jHorizontalLinearLayout
import java.awt.Desktop
import java.awt.Dimension
import java.awt.event.ComponentEvent
import java.awt.event.ComponentListener
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import javax.swing.*
import javax.swing.event.DocumentEvent
import javax.swing.event.DocumentListener
import kotlin.collections.HashMap
import kotlin.math.abs


class DiagramToolWindow : ToolWindowFactory {

    private val rootView by lazy {
        JPanel()
    }
    private val sequenceDiagramPanel by lazy {
        SequenceDiagramPanel()
    }
    private val lifecycleSelector by lazy {
        MultiComboBox(200) {
            sequenceDiagramPanel.diagramDelegate.setDrawLifecycles(it.toList())
            sequenceDiagramPanel.repaint()
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

    val dateViewFormat = "yyyy-MM-dd HH:mm:ss"
    val format = "yyyy-MM-dd+HH:mm:ss.SSS"
    private val startJXDatePicker by lazy {
        DateSelectorView(250).apply {
            setFormats(dateViewFormat)
            editor.isEnabled = false
        }
    }

    private val endJXDatePicker by lazy {
        DateSelectorView(250).apply {
            setFormats(dateViewFormat)
            editor.isEnabled = false
        }
    }

    private val occurrenceTimePicker by lazy {
        DateSelectorView(250).apply {
            setFormats(dateViewFormat)
            editor.isEnabled = false
        }
    }

    private val logTypeComboBox by lazy {
        JXComboBox(arrayOf("log_HUF_8155_android"))
    }

    private val wordsTextArea = ToolTextField(300)

    private val wordsLine by lazy {
        jHorizontalLinearLayout {
            add(JBLabel("过滤词：(可选项，多个过滤词需英文逗号隔开)").apply {
                minimumSize = Dimension(10, 30)
            })
            add(wordsTextArea)
            add(saveButton)
        }
    }

    private val progressBar by lazy {
        JProgressBar().apply {
            preferredSize = Dimension(400, 10)
            isVisible = false
        }
    }

    private val carConfigsMap = HashMap<String, Array<String>>()

    private val openFolder by lazy {
        JButton("打开下载文件夹").apply {
            addActionListener {
                try {
                    val defaultFileFolder = FileUtils.defaultFileFolder()
                    val file = File(defaultFileFolder)
                    if (!file.exists()) {
                        file.mkdir()
                    }
                    Desktop.getDesktop().open(file)
                } catch (ex: Exception) {
                    ex.printStackTrace()
                }
            }
        }
    }

    private val occurrenceTimeComboBox by lazy {
        val toArray = timeHashMap.keys.toArray(arrayOf())
        toArray.sortBy { timeHashMap[it] }
        JXComboBox().apply {
            model = DefaultComboBoxModel(toArray)
        }
    }

    //创建一个懒加载的按钮，用于点击后，执行保存文件操作
    private val saveButton by lazy {
        JButton("保存过滤结果").apply {
            addActionListener {
                val selectedElement = sequenceDiagramPanel.diagramDelegate.getSelectedElement()
                if (selectedElement.isEmpty()) {
                    notifyText("请先点下载分析数据~")
                    return@addActionListener
                }
                //先获取当前时间戳，然后将时间戳格式化成字符串，最后将字符串拼接到文件名中
                val currentTimeMillis = System.currentTimeMillis()
                val format = SimpleDateFormat("yyyy-MM-dd-HH-mm-ss")
                val dateStr = format.format(currentTimeMillis)
                val fileName = FileUtils.getFilterFileFolder() + dateStr + ".log"
                val file = File(fileName)
                if (!file.exists()) {
                    file.createNewFile()
                }
                selectedElement.forEach { element ->
                    file.appendText(element.toString())
                    file.appendText("\n")
                }
                Desktop.getDesktop().open(File(FileUtils.getFilterFileFolder()))
            }
        }
    }

    private val scrollBar by lazy {
        JBScrollBar(JBScrollBar.VERTICAL).apply {
            preferredSize = Dimension(50, SequenceDiagramPanel.MAX_HEIGHT)
        }
    }

    override fun createToolWindowContent(project: Project, toolWindow: ToolWindow) {
        val contentFactory = ContentFactory.SERVICE.getInstance()
        initRootView(project)
        val content = contentFactory.createContent(rootView, "", false)
        toolWindow.contentManager.addContent(content)
        rootView.addComponentListener(object : ComponentListener {
            override fun componentResized(p0: ComponentEvent?) {
                val newWidth = (p0?.component?.width ?: 0) - 100
                val newHeight = (p0?.component?.height ?: 0) - sequenceDiagramPanel.y - 50
                sequenceDiagramPanel.preferredSize = Dimension(newWidth, newHeight)
                SequenceDiagramPanel.MAX_WIDTH = newWidth
                SequenceDiagramPanel.MAX_HEIGHT = newHeight
                sequenceDiagramPanel.revalidate()
                scrollBar.preferredSize = Dimension(50, newHeight)
                scrollBar.revalidate()
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
        val run = JButton("Download").apply {
            this.addActionListener {
                vinConfigPanel.text.let {
                    if (it.isEmpty() or it.isBlank()) {
                        notifyText("VIN不能为空")
                    }
                }
                if (startJXDatePicker.date == null) {
                    notifyText("开始时间不能为空")
                    return@addActionListener
                }
                if (endJXDatePicker.date == null) {
                    notifyText("结束时间不能为空")
                    return@addActionListener
                }
                if (startJXDatePicker.date.after(endJXDatePicker.date)) {
                    notifyText("开始时间不能大于结束时间")
                    return@addActionListener
                }
                val config = LogConfigBeans(
                    wordsTextArea.text.split(','),
                    logTypeComboBox.selectedItem?.toString() ?: "",
                    endJXDatePicker.getFormatDate(format),
                    startJXDatePicker.getFormatDate(format),
                    vinConfigPanel.text
                )
                DownloadManager.download(project, config, 1, sequenceDiagramPanel, onSuccess = {
                    progressBar.isVisible = false
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
        occurrenceTimePicker.addPropertyChangeListener("date") {
            val newValue = it.newValue
            if (newValue is Date) {
                changeTime(newValue)
            }
        }
        occurrenceTimeComboBox.addItemListener {
            val value = occurrenceTimePicker.date ?: return@addItemListener
            changeTime(value)
        }
        val defaultDownloadLine = jHorizontalLinearLayout {
            add(JBLabel("发生时间：（默认下载此时间前后半小时的log，也可手动选择起止时间）").apply {
                minimumSize = Dimension(60, 30)
            })
            add(occurrenceTimePicker)
            add(occurrenceTimeComboBox)
        }
        val timeLine = jHorizontalLinearLayout {
            add(startTitle)
            add(startJXDatePicker)
            add(endTitle)
            add(endJXDatePicker)
        }
        val logTypeTitle = JBLabel("  业务类型：").apply {
            preferredSize = Dimension(80, 30)
        }
        val firstLine = jHorizontalLinearLayout {
            add(vinTitle)
            add(vinConfigPanel)
            add(logTypeTitle)
            add(logTypeComboBox)
        }
        val boundedRangeModel = DefaultBoundedRangeModel(0, 10, 0, 100)
        scrollBar.model = boundedRangeModel
        var lastScrollBarValue = scrollBar.value
        scrollBar.addAdjustmentListener {
            sequenceDiagramPanel.diagramDelegate.onScrolling(it.value - lastScrollBarValue)
            lastScrollBarValue = it.value
        }
        sequenceDiagramPanel.diagramDelegate.addScrollListener { scrollTotal ->
            //修改scrollBar的显示状态
            //scrollBar.value = abs(scrollTotal)
        }
        scrollBar.addMouseWheelListener {
            val isScrollEvent = JBScrollPane.isScrollEvent(it)
            val isFromSeqPanel = it.source is JBScrollBar
            if (isScrollEvent && isFromSeqPanel) {
                sequenceDiagramPanel.diagramDelegate.onScrolling(it.preciseWheelRotation.toInt())
            }
        }
        rootView.add(
            firstLine,
            sequenceDiagramPanel,
            run,
            lifecycleSelector,

            timeLine,
            defaultDownloadLine,

            wordsLine,
            progressBar,
            openFolder,
            scrollBar
        )

        val firstLineCons = springLayout.getConstraints(firstLine)
        firstLineCons.x = Spring.constant(20)
        firstLineCons.y = Spring.constant(20)

        defaultDownloadLine.topToBottom(firstLine)
        defaultDownloadLine.leftToLeft(firstLine)
        timeLine.topToBottom(defaultDownloadLine)
        timeLine.leftToLeft(firstLine)

        wordsLine.topToBottom(timeLine)
        wordsLine.leftToLeft(timeLine)

        run.topToBottom(wordsLine)
        run.leftToLeft(firstLine)
        lifecycleSelector.leftToRight(run, 5)
        lifecycleSelector.topToTop(run)

        sequenceDiagramPanel.topToBottom(run, 20)
        sequenceDiagramPanel.leftToLeft(firstLine)
        progressBar.bottomToTop(sequenceDiagramPanel)
        progressBar.leftToLeft(sequenceDiagramPanel)
        openFolder.topToBottom(sequenceDiagramPanel)
        openFolder.leftToLeft(vinConfigPanel)
        scrollBar.topToTop(sequenceDiagramPanel)
        scrollBar.leftToRight(sequenceDiagramPanel)
    }

    private fun changeTime(newValue: Date) {
        val splitTime = timeHashMap[occurrenceTimeComboBox.selectedItem?.toString()] ?: 30
        val startTime = Date(newValue.time - splitTime * 60 * 1000)
        val endTime = Date(newValue.time + splitTime * 60 * 1000)

        startJXDatePicker.date = startTime
        endJXDatePicker.date = endTime
    }

    private fun updateVinConfig(vin: String) {

        if (vinConfigMap.contains(vin)) {
            val vinType = vinConfigMap[vin]
            val array = carConfigsMap[vinType?.get(0).toString()]

            if (array != null) {
                logTypeComboBox.model = DefaultComboBoxModel(array)
                return
            }

        }
        if (oldJob?.isActive == true) {
            oldJob?.cancel()
        }
        oldJob = GlobalScope.launch {
            withContext(Dispatchers.IO) {
                DownloadManager.requestUrl<VinConfig>(
                    "https://dip-data-msg-parsing-service.prod.k8s.chehejia.com/v1-0/msg-parsing/common/vehicles/pagination?pageNum=1&pageSize=100&vinContains=$vin",
                    object : TypeToken<BaseResp<VinConfig>>() {}.type
                ) {
                    vinConfigMap[vin] = it?.data?.list?.firstOrNull()?.vehSeriesNo ?: "X"
                    if (it?.data?.list?.firstOrNull() == null) {
                        notifyText("发生一次异常此vin码 $vin 未找到对应车型 返回数据$it，但默认应用了X平台匹配规则")
                    }
                    val vinType = vinConfigMap[vin]
                    val array = carConfigsMap[vinType?.get(0).toString()] ?: arrayOf()
                    logTypeComboBox.model = DefaultComboBoxModel(array)
                }
            }
        }
    }

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

    private val timeHashMap = hashMapOf(
        "半小时" to 30,
        "1小时" to 60,
        "2小时" to 60 * 2,
        "3小时" to 60 * 3,
        "半天" to 60 * 12,
        "一天" to 60 * 24,
        "一周" to 60 * 24 * 7
    )
}