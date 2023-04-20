package com.lixiang.car.happytools.tools

import com.lixiang.car.happytools.tools.compose.ComposeTransform

object MainTest {
    @JvmStatic
    fun main(args: Array<String>) {
        val code = """
    Image(
        bitmap = LegoR.drawableAsImageBitmap(id = "mine_btn_edit"),
        contentDescription = resources.getString(R.string.mine_detail_edit),
        modifier = Modifier
            .width(140.dp)
            .height(140.dp)
            .clickable {
                CommonLogUtils.i("test", "clickable")
                mViewModel.switchEditMode(true)
                sendAccessibilityClickEvent()
            }
    )
""".trimIndent()
        val recognizeComposeFunction = ComposeTransform.parseComposeFunction(code)
        println(recognizeComposeFunction)
    }
}