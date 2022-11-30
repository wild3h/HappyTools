package com.lixiang.car.decodecohttp.util;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.fileEditor.OpenFileDescriptor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.newvfs.RefreshQueue;

import java.io.*;

/**
 * @author shuzijun
 */
public class FileUtils {


    public static void saveFile(String path, String body) {
        saveFile(new File(path), body);
    }

    public static void saveFile(File file, String body) {
        try {
            if (body == null) {
                return;
            }
            if (!file.getParentFile().exists()) {
                file.getParentFile().mkdirs();
            }
            if (!file.exists()) {
                file.createNewFile();
            }
            FileOutputStream fileOutputStream = new FileOutputStream(file, Boolean.FALSE);
            fileOutputStream.write(body.getBytes("UTF-8"));
            fileOutputStream.close();
        } catch (IOException io) {
            System.out.println("保存文件错误");
            io.printStackTrace();
        }
    }

    public static String getFileBody(String filePath) {
        return getFileBody(new File(filePath));
    }

    public static String getFileBody(File file) {
        String all = "";
        if (file.exists()) {
            Long filelength = file.length();
            byte[] filecontent = new byte[filelength.intValue()];
            try {
                FileInputStream in = new FileInputStream(file);
                in.read(filecontent);
                in.close();
                all = new String(filecontent, "UTF-8");
            } catch (IOException i) {
                i.printStackTrace();

            }
        }
        return all;
    }


    public static String trim(String str) {
        return str.replaceAll("[\\s|\\t]", "");
    }


    public static void openFileEditor(File file, Project project) {
        ApplicationManager.getApplication().invokeLater(() -> {
            VirtualFile vf = LocalFileSystem.getInstance().refreshAndFindFileByIoFile(file);
            OpenFileDescriptor descriptor = new OpenFileDescriptor(project, vf);
            FileEditorManager.getInstance(project).openTextEditor(descriptor, false);
            RefreshQueue.getInstance().refresh(false, false, null, vf);
        });
    }


    public static void openFileEditorAndSaveState(File file, Project project) {
        ApplicationManager.getApplication().invokeLater(() -> {
            VirtualFile vf = LocalFileSystem.getInstance().refreshAndFindFileByIoFile(file);
            OpenFileDescriptor descriptor = new OpenFileDescriptor(project, vf);
            FileEditorManager.getInstance(project).openTextEditor(descriptor, false);
        });
    }

    public static void saveEditDocument(VirtualFile file) {
        if (FileDocumentManager.getInstance().isFileModified(file)) {
            try {
                ApplicationManager.getApplication().invokeLaterOnWriteThread((() -> {
                    ApplicationManager.getApplication().runWriteAction(() -> {
                        FileDocumentManager.getInstance().saveDocument(FileDocumentManager.getInstance().getDocument(file));
                    });
                }));
            } catch (Throwable ignore) {
                ignore.printStackTrace();
            }

        }
    }

    public static String separator() {
        if (File.separator.equals("\\")) {
            return "/";
        } else {
            return "";
        }
    }

    public static String defaultFileFolder() {
        return System.getProperty("java.io.tmpdir");
    }
}