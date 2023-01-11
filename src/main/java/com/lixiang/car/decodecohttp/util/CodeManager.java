package com.lixiang.car.decodecohttp.util;

import com.intellij.openapi.project.Project;
import com.lixiang.car.decodecohttp.decodecohttp.util.NotifyUtil;

import java.io.File;
import java.util.UUID;

/**
 * @author shuzijun
 */
public class CodeManager {

    @Deprecated
    public static void openCode(Project project, String text) {
        openCode(project, text, "json-res-"+ UUID.randomUUID() +".json");
    }

    public static void openCode(Project project, String text, String fileName) {
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

    public static boolean saveAndOpen(Project project, String path, String text, String fileName) {
        String filePath = path + File.separator + fileName;

        File file = new File(filePath);
        if (file.exists()) {
            return false;
        }
        try {
            FileUtils.saveFile(file, text);
            FileUtils.openFileEditorAndSaveState(file, project);
        } catch (Exception e) {
            e.printStackTrace();
            NotifyUtil.INSTANCE.notifyMessage(e.getCause().toString());
        }
        return true;
    }
}