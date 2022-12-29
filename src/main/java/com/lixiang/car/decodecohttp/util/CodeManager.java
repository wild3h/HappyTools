package com.lixiang.car.decodecohttp.util;

import com.intellij.openapi.project.Project;

import java.io.File;

/**
 * @author shuzijun
 */
public class CodeManager {

    @Deprecated
    public static void openCode(Project project, String text) {
        openCode(project, text,"test.json");
    }

    public static void openCode(Project project, String text,String fileName) {
        String filePath = FileUtils.defaultFileFolder() + File.separator + "decodeCohttp" + File.separator + fileName;

        File file = new File(filePath);
        if (file.exists()) {
            file.delete();
        }
        try {
            FileUtils.saveFile(file, text);
            FileUtils.openFileEditorAndSaveState(file, project);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}