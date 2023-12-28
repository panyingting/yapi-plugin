package com.qbb.builder;

import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.intellij.openapi.ui.Messages;
import com.qbb.component.CompositeConfigComponent;
import com.qbb.component.entity.FieldMappingEntity;

import java.io.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @describe:
 * @author: pyt email:panyingting220415@credithc.com
 * @create_time: 2023/12/28 17:03
 */
public class KVFieldNameOutputProcessor {

    private List<FieldMappingEntity> fieldMappingEntityList = new ArrayList<>();

    private Set<String> repeatCheckSet = new HashSet<>();

    private boolean hasFile = false;

    public void openFileAndRead() {
        String path = CompositeConfigComponent.getVersionFieldMapFilePath();
        BufferedReader reader = null;
        try {
            if ( path != null) {
                File file = getFile(path);
                reader = new BufferedReader(new FileReader(file));
                StringBuilder jsonBuilder = new StringBuilder();
                String readLine;
                while ((readLine = reader.readLine()) != null) {
                    jsonBuilder.append(readLine);
                }


                if (jsonBuilder.length() > 0) {
                    fieldMappingEntityList = new GsonBuilder().create().fromJson(jsonBuilder.toString(), new TypeToken<List<FieldMappingEntity>>(){}.getType());
                    repeatCheckSet = fieldMappingEntityList.stream().map( e-> e.getNewly()+e.getDesc()).collect(Collectors.toSet());
                }
                hasFile = true;
            }
        } catch (Exception ex) {
            Messages.showErrorDialog("解析json文件失败:"+path, "解析json失败");
        } finally {
            try {
                if (reader != null) {
                    reader.close();
                }
            } catch (Exception ex){
                // ignore
            }
        }
    }

    public void addField(String originField, String newlyField, String describe) {
        if (hasFile) {
            String repeatCheck = newlyField + describe;
            if (!repeatCheckSet.contains(repeatCheck)) {
                FieldMappingEntity fieldMappingEntity = new FieldMappingEntity();
                fieldMappingEntity.setOrigin(originField);
                fieldMappingEntity.setNewly(newlyField);
                fieldMappingEntity.setDesc(describe);
                fieldMappingEntityList.add(fieldMappingEntity);
            }
        }
    }

    public void writeToFile() {
        if (!hasFile) {
            return;
        }
        String path = CompositeConfigComponent.getVersionFieldMapFilePath();
        BufferedWriter writer = null;
        try {
            if ( path != null) {
                File file = getFile(path);
                writer = new BufferedWriter(new FileWriter(file));
                String json = new GsonBuilder().create().toJson(fieldMappingEntityList);
                writer.write(json);
            }
        } catch (Exception ex) {
            Messages.showErrorDialog("json写入文件失败:"+path, "解析json失败");
        } finally {
            try {
                if (writer != null) {
                    writer.close();
                }
            } catch (Exception ex){
                // ignore
            }
        }

    }

    private File getFile(String path) throws IOException {
        File file = new File(path);
        if (!file.exists()) {
            boolean create = file.createNewFile();
            if (!create) {
                Messages.showErrorDialog("创建文件失败:"+path, "创建文件失败");
            }
        }
        return file;
    }
}
