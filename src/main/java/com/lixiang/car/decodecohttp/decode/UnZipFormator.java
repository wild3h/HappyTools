package com.lixiang.car.decodecohttp.decode;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.lixiang.car.decodecohttp.util.FileUtils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.GZIPInputStream;


public class UnZipFormator {

    public static String format(String content) {
        String jsonStr = uZip(Base64.getDecoder().decode(replaceBlank(content)));
        JsonObject jsonObject = JsonParser.parseString(jsonStr).getAsJsonObject();
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        return gson.toJson(jsonObject);
    }

    public static String uZip(byte[] compressed) {
        if (compressed == null) {
            return "";
        }
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ByteArrayInputStream in = null;
        GZIPInputStream ginzip = null;
        String decompressed = null;
        try {
            in = new ByteArrayInputStream(compressed);
            ginzip = new GZIPInputStream(in);

            byte[] buffer = new byte[1024];
            int offset = -1;
            while ((offset = ginzip.read(buffer)) != -1) {
                out.write(buffer, 0, offset);
            }
            decompressed = out.toString();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (ginzip != null) {
                try {
                    ginzip.close();
                } catch (IOException ignored) {
                }
            }
            if (in != null) {
                try {
                    in.close();
                } catch (IOException ignored) {
                }
            }
            try {
                out.close();
            } catch (IOException ignored) {
            }
        }
        return decompressed;
    }

    public static String replaceBlank(String str) {
        String dest = "";
        if (str != null) {
            Pattern p = Pattern.compile("\\s*|\t|\r|\n");
            Matcher m = p.matcher(str);
            dest = m.replaceAll("");
        }
        return dest;
    }

    public static void main(String[] args) {
        String value = "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";
        System.out.println(UnZipFormator.format(value));
    }
}
