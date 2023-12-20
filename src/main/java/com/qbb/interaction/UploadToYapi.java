package com.qbb.interaction;

import com.google.common.base.Strings;
import com.intellij.notification.NotificationDisplayType;
import com.intellij.notification.NotificationGroup;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.psi.PsiFile;
import com.qbb.builder.BuildJsonForDubbo;
import com.qbb.builder.BuildJsonForYapi;
import com.qbb.component.ConfigPersistence;
import com.qbb.constant.ProjectTypeConstant;
import com.qbb.constant.YapiConstant;
import com.qbb.dto.*;
import com.qbb.upload.UploadYapi;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @description: ���
 * @author: chengsheng@qbb6.com
 * @date: 2019/5/15
 */
public class UploadToYapi extends AnAction {

    private static NotificationGroup notificationGroup;

    static {
        notificationGroup = new NotificationGroup("Java2Json.NotificationGroup", NotificationDisplayType.BALLOON, true);
    }


    @Override
    public void actionPerformed(AnActionEvent e) {
        Editor editor = (Editor) e.getDataContext().getData(CommonDataKeys.EDITOR);

        Project project = editor.getProject();
        String projectToken = null;
        String projectId = null;
        String yapiUrl = null;
        String projectType = null;
        String returnClass = null;
        String attachUpload = null;
        // ��ȡ����
        try {
            final java.util.List<ConfigDTO> configs = ServiceManager.getService(ConfigPersistence.class).getConfigs();
            if(configs == null || configs.size() == 0){
                Messages.showErrorDialog("����ȥ���ý�������yapi����","��ȡ����ʧ�ܣ�");
                return;
            }
            PsiFile psiFile = e.getDataContext().getData(CommonDataKeys.PSI_FILE);
            String virtualFile = psiFile.getVirtualFile().getPath();
            List<ConfigDTO> collect = configs.stream()
                    .filter(it -> {
                        if (!it.getProjectName().equals(project.getName())) {
                            return false;
                        }
                        final String str = (File.separator + it.getProjectName() + File.separator) + (it.getModuleName().equals(it.getProjectName()) ? "" : (it.getModuleName() + File.separator));
                        boolean ret = virtualFile.contains(str);
                        if (!ret) {
                            Messages.showInfoMessage(virtualFile+"��"+str, "·����ƥ��");
                        }
                        return ret;
                    }).collect(Collectors.toList());
            if (collect.isEmpty()) {
                Messages.showErrorDialog(project.getName()+"û���ҵ���Ӧ��yapi���ã����ڲ˵� > Preferences > Other setting > YapiUpload ���"+configs.get(0).getProjectName(), "Error");
                collect = configs;
            }
            final ConfigDTO configDTO = collect.get(0);
            projectToken = configDTO.getProjectToken();
            projectId = configDTO.getProjectId();
            yapiUrl = configDTO.getYapiUrl();
            projectType = configDTO.getProjectType();
        } catch (Exception e2) {
            Messages.showErrorDialog("��ȡ����ʧ�ܣ��쳣:  " + e2.getMessage(),"��ȡ����ʧ�ܣ�");
            return;
        }
//        // ����У��
//        if (Strings.isNullOrEmpty(projectToken) || Strings.isNullOrEmpty(projectId) || Strings.isNullOrEmpty(yapiUrl) || Strings.isNullOrEmpty(projectType)) {
//            Messages.showErrorDialog("������Ŀ��.ideaĿ¼�µ�misc.xml������[projectToken,projectId,yapiUrl,projectType] " ,"��ȡ����ʧ�ܣ�");
//            return;
//        }
        // �ж���Ŀ����
        if (ProjectTypeConstant.dubbo.equals(projectType)) {
            // ���dubbo���ϴ��Ľӿ��б� ��������
            ArrayList<YapiDubboDTO> yapiDubboDTOs = new BuildJsonForDubbo().actionPerformedList(e);
            if (yapiDubboDTOs != null) {
                for (YapiDubboDTO yapiDubboDTO : yapiDubboDTOs) {
                    YapiSaveParam yapiSaveParam = new YapiSaveParam(projectToken, yapiDubboDTO.getTitle(), yapiDubboDTO.getPath(), yapiDubboDTO.getParams(), yapiDubboDTO.getResponse(), Integer.valueOf(projectId), yapiUrl, yapiDubboDTO.getDesc());
                    yapiSaveParam.setStatus(yapiDubboDTO.getStatus());
                    if (!Strings.isNullOrEmpty(yapiDubboDTO.getMenu())) {
                        yapiSaveParam.setMenu(yapiDubboDTO.getMenu());
                    } else {
                        yapiSaveParam.setMenu(YapiConstant.menu);
                    }
                    try {
                        // �ϴ�
                        YapiResponse yapiResponse = new UploadYapi().uploadSave(yapiSaveParam, null, project.getBasePath());
                        if (yapiResponse.getErrcode() != 0) {
                            Messages.showErrorDialog("�ϴ�ʧ�ܣ��쳣:  " + yapiResponse.getErrmsg(),"�ϴ�ʧ�ܣ�");
                        } else {
                            String url = yapiUrl + "/project/" + projectId + "/interface/api/cat_" + yapiResponse.getCatId();
                            this.setClipboard(url);
                            Messages.showInfoMessage("�ϴ��ɹ����ӿ��ĵ�url��ַ:  " + url,"�ϴ��ɹ���");
                        }
                    } catch (Exception e1) {
                        Messages.showErrorDialog("�ϴ�ʧ�ܣ��쳣:  " + e1,"�ϴ�ʧ�ܣ�");
                    }
                }
            }
        } else if (ProjectTypeConstant.api.equals(projectType)) {
            //���api ���ϴ��Ľӿ��б� ��������
            ArrayList<YapiApiDTO> yapiApiDTOS = new BuildJsonForYapi().actionPerformedList(e, attachUpload, returnClass);
            if (yapiApiDTOS != null) {
                for (YapiApiDTO yapiApiDTO : yapiApiDTOS) {
                    YapiSaveParam yapiSaveParam = new YapiSaveParam(projectToken, yapiApiDTO.getTitle(), yapiApiDTO.getPath(), yapiApiDTO.getParams(), yapiApiDTO.getRequestBody(), yapiApiDTO.getResponse(), Integer.valueOf(projectId), yapiUrl, true, yapiApiDTO.getMethod(), yapiApiDTO.getDesc(), yapiApiDTO.getHeader());
                    yapiSaveParam.setReq_body_form(yapiApiDTO.getReq_body_form());
                    yapiSaveParam.setReq_body_type(yapiApiDTO.getReq_body_type());
                    yapiSaveParam.setReq_params(yapiApiDTO.getReq_params());
                    yapiSaveParam.setStatus(yapiApiDTO.getStatus());
                    if (!Strings.isNullOrEmpty(yapiApiDTO.getMenu())) {
                        yapiSaveParam.setMenu(yapiApiDTO.getMenu());
                    } else {
                        yapiSaveParam.setMenu(YapiConstant.menu);
                    }
                    try {
                        // �ϴ�
                        YapiResponse yapiResponse = new UploadYapi().uploadSave(yapiSaveParam, attachUpload, project.getBasePath());
                        if (yapiResponse.getErrcode() != 0) {
                            Messages.showInfoMessage("�ϴ�ʧ�ܣ�ԭ��:  " + yapiResponse.getErrmsg(),"�ϴ�ʧ�ܣ�");
                        } else {
                            String url = yapiUrl + "/project/" + projectId + "/interface/api/cat_" + yapiResponse.getCatId();
                            this.setClipboard(url);
                            Messages.showInfoMessage("�ϴ��ɹ����ӿ��ĵ�url��ַ:  " + url,"�ϴ��ɹ���");
                        }
                    } catch (Exception e1) {
                        Messages.showErrorDialog("�ϴ�ʧ�ܣ��쳣:  " + e1,"�ϴ�ʧ�ܣ�");
                    }
                }
            }
        }
    }

    /**
     * @description: ���õ����а�
     * @param: [content]
     * @return: void
     * @author: chengsheng@qbb6.com
     * @date: 2019/7/3
     */
    private void setClipboard(String content) {
        //��ȡϵͳ���а�
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        //����String��������
        StringSelection selection = new StringSelection(content);
        //����ı���ϵͳ���а�
        clipboard.setContents(selection, null);
    }
}
