package com.lixiang.car.happytools.tools

import com.lixiang.car.happytools.tools.compose.ComposeTransform

object MainTest {
    @JvmStatic
    fun main(args: Array<String>) {
        val recognizeComposeFunction = ComposeTransform.parseComposeFunction("Image(\n" +
            "            bitmap = LegoR.drawableAsImageBitmap(id = \"mine_btn_edit\"),\n" +
            "            contentDescription = resources.getString(R.string.mine_detail_edit),\n" +
            "            modifier = Modifier\n" +
            "                .width(140.dp)\n" +
            "                .height(140.dp)\n" +
            "                .clickable {\n" +
            "                    CommonLogUtils.i(\"test\", \"clickable\")\n" +
            "                    mViewModel.switchEditMode(true)\n" +
            "                    sendAccessibilityClickEvent()\n" +
            "                }\n" +
            "        )")
        println(recognizeComposeFunction)
    }
}