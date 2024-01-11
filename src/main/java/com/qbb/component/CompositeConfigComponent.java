package com.qbb.component;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.psi.PsiFile;
import com.qbb.dto.ConfigDTO;

import java.io.File;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @describe: misc xml 获取配置信息
 * @author: pyt email:panyingting220415@credithc.com
 * @create_time: 2023/12/28 16:03
 */
public class CompositeConfigComponent {
    private static String versionFieldMapFilePath = null;

    private String projectToken = null;
    private String projectId = null;
    private String yapiUrl = null;
    private String projectType = null;
    private String returnClass = null;
    private String attachUpload = null;
    public CompositeConfigComponent(AnActionEvent e) {
        initConfig(e);
    }

    private void initConfig(AnActionEvent e) {
        if (!initConfigByXml(e)) {
            initConfigByPanel(e);
        }
    }

    public boolean initConfigByXml(AnActionEvent e) {
        Editor editor = e.getDataContext().getData(CommonDataKeys.EDITOR);

        // 获取配置
        try {

            String projectConfig = new String(editor.getProject().getProjectFile().contentsToByteArray(), "utf-8");
            String[] modules = projectConfig.split("moduleList\">");
            if (modules.length > 1) {
                String[] moduleList = modules[1].split("</")[0].split(",");
                PsiFile psiFile = (PsiFile) e.getDataContext().getData(CommonDataKeys.PSI_FILE);
                String virtualFile = psiFile.getVirtualFile().getPath();
                for (int i = 0; i < moduleList.length; i++) {
                    if (virtualFile.contains(moduleList[i])) {
                        projectToken = projectConfig.split(moduleList[i] + "\\.projectToken\">")[1].split("</")[0];
                        projectId = projectConfig.split(moduleList[i] + "\\.projectId\">")[1].split("</")[0];
                        yapiUrl = projectConfig.split(moduleList[i] + "\\.yapiUrl\">")[1].split("</")[0];
                        projectType = projectConfig.split(moduleList[i] + "\\.projectType\">")[1].split("</")[0];
                        if (projectConfig.split(moduleList[i] + "\\.returnClass\">").length > 1) {
                            returnClass = projectConfig.split(moduleList[i] + "\\.returnClass\">")[1].split("</")[0];
                        }
                        String[] attachs = projectConfig.split(moduleList[i] + "\\.attachUploadUrl\">");
                        if (attachs.length > 1) {
                            attachUpload = attachs[1].split("</")[0];
                        }
                        break;
                    }
                }
            } else {
                projectToken = projectConfig.split("projectToken\">")[1].split("</")[0];
                projectId = projectConfig.split("projectId\">")[1].split("</")[0];
                yapiUrl = projectConfig.split("yapiUrl\">")[1].split("</")[0];
                projectType = projectConfig.split("projectType\">")[1].split("</")[0];

                if (projectConfig.split("versionFieldMapFilePath\">").length > 1) {
                    versionFieldMapFilePath = projectConfig.split("versionFieldMapFilePath\">")[1].split("</")[0];
                }

                if (projectConfig.split("returnClass\">").length > 1) {
                    returnClass = projectConfig.split("returnClass\">")[1].split("</")[0];
                }

                String[] attachs = projectConfig.split("attachUploadUrl\">");
                if (attachs.length > 1) {
                    attachUpload = attachs[1].split("</")[0];
                }
            }

            return true;

        } catch (Exception e2) {
            Messages.showErrorDialog("misc xml 获取配置信息失败:" + e2.getMessage(),"获取XML配置失败！");
            return false;
        }
    }

    public boolean initConfigByPanel(AnActionEvent e) {
        Editor editor = e.getDataContext().getData(CommonDataKeys.EDITOR);

        Project project = editor.getProject();
        // 获取配置
        try {
            final java.util.List<ConfigDTO> configs = ServiceManager.getService(ConfigPersistence.class).getConfigs();
            if(configs == null || configs.size() == 0){
                Messages.showErrorDialog("请先去配置界面配置yapi配置","获取配置失败！");
                return false;
            }
            PsiFile psiFile = e.getDataContext().getData(CommonDataKeys.PSI_FILE);
            String virtualFile = psiFile.getVirtualFile().getPath().replaceAll("\\W+", "");
            List<ConfigDTO> collect = configs.stream()
                    .filter(it -> {
                        if (!it.getProjectName().equals(project.getName())) {
                            return false;
                        }
                        String str = (File.separator + it.getProjectName() + File.separator) + (it.getModuleName().equals(it.getProjectName()) ? "" : (it.getModuleName() + File.separator));
                        str = str.replaceAll("\\W+", "");
                        boolean ret = virtualFile.contains(str);
                        if (!ret) {
                            Messages.showInfoMessage(virtualFile+"："+str, "路径不匹配");
                        }
                        return ret;
                    }).collect(Collectors.toList());
            if (collect.isEmpty()) {
                collect = configs;
            }
            final ConfigDTO configDTO = collect.get(0);
            projectToken = configDTO.getProjectToken();
            projectId = configDTO.getProjectId();
            yapiUrl = configDTO.getYapiUrl();
            projectType = configDTO.getProjectType();

            return true;
        } catch (Exception e2) {
            Messages.showErrorDialog("otherSetting获取配置失败，异常:  " + e2.getMessage(),"获取配置失败！");
            return false;
        }
    }

    public String getProjectToken() {
        return projectToken;
    }

    public String getProjectId() {
        return projectId;
    }

    public String getYapiUrl() {
        return yapiUrl;
    }

    public String getProjectType() {
        return projectType;
    }

    public static String getVersionFieldMapFilePath() {
        return versionFieldMapFilePath;
    }

    public String getReturnClass() {
        return returnClass;
    }

    public String getAttachUpload() {
        return attachUpload;
    }
}
