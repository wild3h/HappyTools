package com.lixiang.car.happytools.tools.compose

import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.ProjectManager
import com.intellij.openapi.wm.ToolWindowManager
import com.lixiang.car.happytools.tools.data.compose.*
import com.lixiang.car.happytools.tools.util.jsonFormat
import com.lixiang.car.happytools.tools.util.setTextWrite
import kastree.ast.Node
import org.jetbrains.kotlin.cli.common.CLIConfigurationKeys
import org.jetbrains.kotlin.cli.common.messages.MessageRenderer
import org.jetbrains.kotlin.cli.common.messages.PrintingMessageCollector
import org.jetbrains.kotlin.cli.jvm.compiler.EnvironmentConfigFiles
import org.jetbrains.kotlin.cli.jvm.compiler.KotlinCoreEnvironment
import org.jetbrains.kotlin.com.intellij.openapi.project.Project
import org.jetbrains.kotlin.com.intellij.openapi.util.Disposer
import org.jetbrains.kotlin.config.CompilerConfiguration
import org.jetbrains.kotlin.config.JVMConfigurationKeys
import org.jetbrains.kotlin.config.JvmTarget
import org.jetbrains.kotlin.psi.KtFile


object ComposeTransform {

    private var classList: ArrayList<String> = arrayListOf()
    private var methodList: ArrayList<String> = arrayListOf()
    private var proj: Project
    private lateinit var editor: Editor

    init {
        val configuration = CompilerConfiguration()
        configuration.put(CLIConfigurationKeys.MESSAGE_COLLECTOR_KEY, PrintingMessageCollector(System.out, MessageRenderer.PLAIN_FULL_PATHS, true))
        configuration.put(JVMConfigurationKeys.JVM_TARGET, JvmTarget.JVM_1_8)
        proj = KotlinCoreEnvironment.createForProduction(
            Disposer.newDisposable(),
            configuration,
            EnvironmentConfigFiles.JVM_CONFIG_FILES
        ).project

    }

    fun parseKotlinToPsi(code: String, editor: Editor) {
        this.editor = editor
        // look https://github.com/maldinixiang/ktvisitor-kastree.git
        // https://github.com/cretz/kastree
        val classParser = Parser()
        val classFile = classParser.parseFile(code)
        classFile.decls.forEach {
            when (it) {
                is Node.Decl.Structured -> {
                    getFunInfo(it)
                }

                is Node.Decl.Func -> {
                    it.name?.let { funName -> methodList.add(funName) }
                }

                else -> {}
            }
        }
    }

    private fun getFunInfo(structured: Node.Decl.Structured) {
        structured.members.forEach {
            when (it) {
                is Node.Decl.Func -> {
                    val functionName = it.name
                    val body = it.body
                    if (body is Node.Decl.Func.Body.Block) {
                        val composeViews = arrayListOf<BaseComposeView>()
                        body.block.stmts.forEach { expr ->
                            if (expr is Node.Stmt.Expr) {
                                val call = expr.expr
                                if (call is Node.Expr.Call) {
                                    val composeView = dfsFunction(call)
                                    if (composeView is BaseComposeView) {
                                        composeViews.add(composeView)
                                    }
                                }
                            }
                        }
                        val strBuilder = StringBuilder()
                        composeViews.forEach {
                            strBuilder.append(it.toString())
                        }
                        val lego = strBuilder.toString().jsonFormat()
                        editor.document.setTextWrite(lego)
                    }
                }

                else -> {}
            }
        }
    }


    private fun dfsFunction(function: Node.Expr.Call): BaseJson? {
        function.expr.let {
            when (it) {
                is Node.Expr.Name -> {
                    val strBuilder = StringBuilder()
                    strBuilder.append(it.name)
                    strBuilder.append("(")
                    val composeView: BaseComposeView? = when (it.name) {
                        "Image" -> {
                            CPImage()
                        }

                        "Text" -> {
                            CPText()
                        }

                        "Box" -> {
                            CPBox()
                        }

                        "CircularProgressIndicator" -> {
                            CPCircularProgressIndicator()
                        }

                        "Column" -> {
                            CPColumn()
                        }

                        "Divider" -> {
                            CPDivider()
                        }

                        "Grid" -> {
                            CPGrid()
                        }

                        "LazyColumn" -> {
                            CPLazyColumn()
                        }

                        "LazyRow" -> {
                            CPLazyRow()
                        }

                        "LinearProgressIndicator" -> {
                            CPLinearProgressIndicator()
                        }

                        "Spacer" -> {
                            CPSpacer()
                        }

                        "Row" -> {
                            CPRow()
                        }

                        else -> {
                            null
                        }
                    }
                    function.args.forEach {
                        val key = it.name ?: ""
                        it.expr.let {
                            val str: String? = when (key) {
                                "modifier" -> {
                                    val modifier = dfsModifier(it)
                                    composeView?.modifier = modifier
                                    null
                                }

                                "contentPadding" -> {
                                    if (composeView != null) {
                                        dfsContentPadding(it, composeView, key)
                                    }
                                    null
                                }

                                else -> {
                                    when (it) {
                                        is Node.Expr.BinaryOp -> {
                                            dfsBinaryOp(it)
                                        }

                                        is Node.Expr.Call -> {
                                            //Text(style= TextStyle(color = Color.Red))
                                            if (composeView != null) {
                                                dfsParams(it, key, composeView)
                                            }
                                            null
                                        }

                                        is Node.Expr.Name -> {
                                            it.name
                                        }

                                        is Node.Expr.StringTmpl -> {
                                            val str = StringBuilder()
                                            it.elems.forEach { elem ->
                                                when (elem) {
                                                    is Node.Expr.StringTmpl.Elem.Regular -> {
                                                        str.append(elem.str)
                                                    }

                                                    is Node.Expr.StringTmpl.Elem.ShortTmpl -> {
                                                        str.append(elem.str)
                                                    }

                                                    is Node.Expr.StringTmpl.Elem.UnicodeEsc -> {
                                                        str.append(elem.digits)

                                                    }

                                                    is Node.Expr.StringTmpl.Elem.RegularEsc -> {
                                                        str.append(elem.char)
                                                    }

                                                    is Node.Expr.StringTmpl.Elem.LongTmpl -> {
                                                        str.append(elem.expr)
                                                    }
                                                }
                                            }
                                            str.toString()
                                        }

                                        is Node.Expr.Const -> {
                                            it.value
                                        }

                                        else -> {
                                            null
                                        }
                                    }
                                }
                            }
                            str?.let {
                                composeView?.also { view ->
                                    view[key] = str
                                }
                            }
                        }
                    }
                    function.lambda?.func?.block?.stmts?.forEach {
                        if (it is Node.Stmt.Expr) {
                            val call = it.expr
                            if (call is Node.Expr.Call) {
                                val childView = dfsFunction(call)
                                if (composeView is BaseComposeViewGroup) {
                                    if (childView != null) {
                                        if (childView is BaseComposeView) {
                                            composeView.child.add(childView)
                                        }
                                    }
                                }
                            }
                        }
                    }
                    return composeView
                }

                else -> {}
            }
        }
        return null
    }

    private fun dfsContentPadding(it: Node.Expr, composeView: BaseComposeView, key: String) {
        val padding = Padding()
        if (it is Node.Expr.Call) {
            val paddingArgs = it.args
            if (paddingArgs.size == 1) {
                val arg = paddingArgs.first()
                if (arg.name == null) {
                    val expr = arg.expr
                    when (expr) {
                        is Node.Expr.Const -> {
                            val value = expr.value.replace(" ", "").toDouble().toInt()
                            padding.putAll(value)
                        }

                        is Node.Expr.BinaryOp -> {
                            val binaryOp = expr
                            val const = binaryOp.lhs
                            if (const is Node.Expr.Const) {
                                val value = const.value.replace(" ", "").toDouble().toInt()
                                padding.putAll(value)
                            }
                        }

                        else -> {}
                    }
                } else {
                    val name = arg.name ?: "default"
                    val expr = arg.expr
                    when (expr) {
                        is Node.Expr.Const -> {
                            val value = expr.value.replace(" ", "").toDouble().toInt()
                            padding.put(name to value)
                        }

                        is Node.Expr.BinaryOp -> {
                            val binaryOp = expr
                            val const = binaryOp.lhs
                            if (const is Node.Expr.Const) {
                                val value = const.value.replace(" ", "").toDouble().toInt()
                                padding.put(name to value)
                            }
                        }

                        else -> {}
                    }
                }
            } else {
                paddingArgs.forEach { arg ->
                    val argName = arg.name ?: "default"
                    val expr = arg.expr
                    when (expr) {
                        is Node.Expr.Const -> {
                            val value = expr.value.replace(" ", "").toDouble().toInt()
                            padding.put(argName to value)
                        }

                        is Node.Expr.BinaryOp -> {
                            val binaryOp = expr
                            val const = binaryOp.lhs
                            if (const is Node.Expr.Const) {
                                val value = const.value.replace(" ", "").toDouble().toInt()
                                padding.put(argName to value)
                            }
                        }

                        else -> {}
                    }
                }
            }
        }
        composeView[key] = padding.toJson()
    }

    private fun dfsParams(function: Node.Expr.Call, paramsName: String, composeView: BaseComposeView) {
        function.expr.let {
            when (it) {
                is Node.Expr.Name -> {
                    val strBuilder = StringBuilder()
                    strBuilder.append(it.name)
                    strBuilder.append("(")
                    val cpParams = CPParams(it.name)
                    function.args.forEach {
                        val key = it.name ?: ""
                        it.expr.let {
                            val str: String? = when (it) {
                                is Node.Expr.BinaryOp -> {
                                    dfsBinaryOp(it)
                                }

                                is Node.Expr.Call -> {
                                    dfsCallsForArgs(it)
                                }

                                is Node.Expr.Name -> {
                                    it.name
                                }

                                is Node.Expr.StringTmpl -> {
                                    val str = StringBuilder()
                                    it.elems.forEach { elem ->
                                        when (elem) {
                                            is Node.Expr.StringTmpl.Elem.Regular -> {
                                                str.append(elem.str)
                                            }

                                            is Node.Expr.StringTmpl.Elem.ShortTmpl -> {
                                                str.append(elem.str)
                                            }

                                            is Node.Expr.StringTmpl.Elem.UnicodeEsc -> {
                                                str.append(elem.digits)

                                            }

                                            is Node.Expr.StringTmpl.Elem.RegularEsc -> {
                                                str.append(elem.char)
                                            }

                                            is Node.Expr.StringTmpl.Elem.LongTmpl -> {
                                                str.append(elem.expr)
                                            }
                                        }
                                    }
                                    str.toString()
                                }

                                is Node.Expr.Const -> {
                                    it.value
                                }

                                else -> {
                                    null
                                }
                            }
                            str?.let {
                                cpParams[key] = str
                            }
                        }
                    }
                    composeView[paramsName] = cpParams
                }

                else -> {}
            }
        }
    }

    private fun dfsCallsForArgs(call: Node.Expr.Call): String? {
        val expr = call.expr
        val strBuilder = StringBuilder()
        if (expr is Node.Expr.Name) {
            strBuilder.append(expr.name)
            strBuilder.append("(")
            call.args.forEach {
                it.expr.let {
                    val str = when (it) {
                        is Node.Expr.BinaryOp -> {
                            dfsBinaryOp(it)
                        }

                        is Node.Expr.Call -> {
                            dfsCallsForArgs(it)
                        }

                        is Node.Expr.Name -> {
                            it.name
                        }

                        is Node.Expr.StringTmpl -> {
                            val str = StringBuilder()
                            it.elems.forEach { elem ->
                                when (elem) {
                                    is Node.Expr.StringTmpl.Elem.Regular -> {
                                        str.append(elem.str)
                                    }

                                    is Node.Expr.StringTmpl.Elem.ShortTmpl -> {
                                        str.append(elem.str)
                                    }

                                    is Node.Expr.StringTmpl.Elem.UnicodeEsc -> {
                                        str.append(elem.digits)

                                    }

                                    is Node.Expr.StringTmpl.Elem.RegularEsc -> {
                                        str.append(elem.char)
                                    }

                                    is Node.Expr.StringTmpl.Elem.LongTmpl -> {
                                        str.append(elem.expr)
                                    }
                                }
                            }
                            str.toString()
                        }

                        is Node.Expr.Const -> {
                            it.value
                        }

                        else -> {
                            null
                        }
                    }
                    strBuilder.append(str)
                }
            }
            strBuilder.append(")")
        }
        return strBuilder.toString()
    }

    private fun dfsModifier(expr: Node.Expr): Modifier {
        val list = dfsCalls(expr)
        val modifier = Modifier()
        list.forEach {
            if (it.expr is Node.Expr.Name) {
                val name = (it.expr as Node.Expr.Name).name
                val args = it.args
                when (name) {
                    "width", "height", "requiredWidth", "requiredHeight" -> {
                        //width=100.dp
                        args.forEach { arg ->
                            val expr = arg.expr
                            when (expr) {
                                is Node.Expr.Const -> {
                                    val value = expr.value.replace(" ", "").toDouble().toInt()
                                    modifier.put(name to value)
                                }

                                is Node.Expr.BinaryOp -> {
                                    val binaryOp = expr
                                    val const = binaryOp.lhs
                                    if (const is Node.Expr.Const) {
                                        val value = const.value.replace(" ", "").toDouble().toInt()
                                        modifier.put(name to value)
                                    }
                                }

                                else -> {}
                            }
                        }
                    }

                    "fillMaxWidth", "fillMaxHeight", "fillMaxSize" -> {
                        if (args.isEmpty()) {
                            modifier.put(name to 1.0)
                        } else {
                            args.forEach { arg ->
                                val expr = arg.expr
                                when (expr) {
                                    is Node.Expr.Const -> {
                                        val value = expr.value.replace(" ", "").toDouble()
                                        modifier.put(name to value)
                                    }

                                    is Node.Expr.BinaryOp -> {
                                        val binaryOp = dfsBinaryOp(expr) ?: ""
                                        modifier.put(name to binaryOp)
                                    }

                                    else -> {}
                                }
                            }
                        }
                    }

                    "alpha" -> {
                        args.forEach { arg ->
                            val expr = arg.expr
                            when (expr) {
                                is Node.Expr.Const -> {
                                    val value = expr.value.replace(" ", "").toDouble()
                                    modifier.put(name to value)
                                }

                                is Node.Expr.BinaryOp -> {
                                    val binaryOp = dfsBinaryOp(expr) ?: ""
                                    modifier.put(name to binaryOp)
                                }

                                else -> {}
                            }
                        }
                    }

                    "padding" -> {
                        val padding = Padding()
                        if (args.size == 1) {
                            val arg = args.first()
                            if (arg.name == null) {
                                val expr = arg.expr
                                when (expr) {
                                    is Node.Expr.Const -> {
                                        val value = expr.value.replace(" ", "").toDouble().toInt()
                                        padding.putAll(value)
                                    }

                                    is Node.Expr.BinaryOp -> {
                                        val binaryOp = expr
                                        val const = binaryOp.lhs
                                        if (const is Node.Expr.Const) {
                                            val value = const.value.replace(" ", "").toDouble().toInt()
                                            padding.putAll(value)
                                        }
                                    }

                                    else -> {}
                                }
                            } else {
                                val name = arg.name ?: "default"
                                val expr = arg.expr
                                when (expr) {
                                    is Node.Expr.Const -> {
                                        val value = expr.value.replace(" ", "").toDouble().toInt()
                                        padding.put(name to value)
                                    }

                                    is Node.Expr.BinaryOp -> {
                                        val binaryOp = expr
                                        val const = binaryOp.lhs
                                        if (const is Node.Expr.Const) {
                                            val value = const.value.replace(" ", "").toDouble().toInt()
                                            padding.put(name to value)
                                        }
                                    }

                                    else -> {}
                                }
                            }
                        } else {
                            args.forEach { arg ->
                                val argName = arg.name ?: "default"
                                val expr = arg.expr
                                when (expr) {
                                    is Node.Expr.Const -> {
                                        val value = expr.value.replace(" ", "").toDouble().toInt()
                                        padding.put(argName to value)
                                    }

                                    is Node.Expr.BinaryOp -> {
                                        val binaryOp = expr
                                        val const = binaryOp.lhs
                                        if (const is Node.Expr.Const) {
                                            val value = const.value.replace(" ", "").toDouble().toInt()
                                            padding.put(argName to value)
                                        }
                                    }

                                    else -> {}
                                }
                            }
                        }
                        modifier.putPadding(padding)
                    }

                    else -> {
                        args.forEach { arg ->
                            val expr = arg.expr
                            when (expr) {
                                is Node.Expr.Const -> {
                                    val value = expr.value
                                    modifier.put(name to value)
                                }

                                is Node.Expr.StringTmpl -> {
                                    val value = expr.elems.firstOrNull()
                                    when (value) {
                                        is Node.Expr.StringTmpl.Elem.Regular -> {
                                            modifier.put(name to value.str)
                                        }

                                        is Node.Expr.StringTmpl.Elem.ShortTmpl -> {
                                            modifier.put(name to value.str)
                                        }

                                        is Node.Expr.StringTmpl.Elem.UnicodeEsc -> {
                                            modifier.put(name to value.digits)
                                        }

                                        is Node.Expr.StringTmpl.Elem.RegularEsc -> {
                                            modifier.put(name to value.char)
                                        }

                                        is Node.Expr.StringTmpl.Elem.LongTmpl -> {
                                            modifier.put(name to value.expr)
                                        }

                                        else -> {
                                        }
                                    }
                                }

                                is Node.Expr.BinaryOp -> {
                                    val binaryOp = dfsBinaryOp(expr) ?: ""
                                    modifier.put(name to binaryOp)
                                }

                                else -> {

                                }
                            }
                        }
                    }
                }
            }
        }
        return modifier
    }

    private fun dfsCalls(it: Node.Expr): List<Node.Expr.Call> {
        return when (it) {
            is Node.Expr.BinaryOp -> {
                arrayListOf<Node.Expr.Call>().apply {
                    addAll(dfsCalls(it.lhs))
                    addAll(dfsCalls(it.rhs))
                }
            }

            is Node.Expr.Call -> {
                return listOf(it)
            }

            else -> {
                emptyList()
            }
        }
    }

    private fun dfsBinaryOp(binaryOp: Node.Expr.BinaryOp): String? {
        val strBuilder = StringBuilder()
        val lhsStr = binaryOp.lhs.let {
            when (it) {
                is Node.Expr.BinaryOp -> {
                    dfsBinaryOp(it)
                }

                is Node.Expr.Call -> {
                    dfsCallsForArgs(it)
                }

                is Node.Expr.Name -> {
                    it.name
                }

                is Node.Expr.Const -> {
                    it.value
                }

                else -> {
                    null
                }
            }
        }
        strBuilder.append(lhsStr)
        val operStr = binaryOp.oper.let {
            when (it) {
                is Node.Expr.BinaryOp.Oper.Token -> {
                    doOperation(it)
                }

                else -> {
                    null
                }
            }
        }
        strBuilder.append(operStr)
        val rhsStr = binaryOp.rhs.let {
            when (it) {
                is Node.Expr.BinaryOp -> {
                    dfsBinaryOp(it)
                }

                is Node.Expr.Call -> {
                    dfsCallsForArgs(it)
                }

                is Node.Expr.Name -> {
                    it.name
                }

                else -> {
                    null
                }
            }
        }
        strBuilder.append(rhsStr)
        return strBuilder.toString()
    }

    private fun doOperation(it: Node.Expr.BinaryOp.Oper.Token): String {
        return it.token.str
    }
}