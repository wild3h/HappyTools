package com.lixiang.car.decodecohttp.util;

import com.intellij.openapi.project.Project;

import java.io.File;

/**
 * @author shuzijun
 */
public class CodeManager {

    public static void openCode(Project project, String text) {


        String filePath = FileUtils.defaultFileFolder() + File.separator + "decodeCohttp" + File.separator + "test.json";

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