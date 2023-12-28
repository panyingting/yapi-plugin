package com.qbb.interaction;

import com.google.common.base.Strings;
import com.intellij.notification.NotificationDisplayType;
import com.intellij.notification.NotificationGroup;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.qbb.builder.BuildJsonForDubbo;
import com.qbb.builder.BuildJsonForYapi;
import com.qbb.component.CompositeConfigComponent;
import com.qbb.constant.ProjectTypeConstant;
import com.qbb.constant.YapiConstant;
import com.qbb.dto.*;
import com.qbb.dto.wrapper.YapiApiDTOPathProcessor;
import com.qbb.upload.UploadYapi;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.util.ArrayList;

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

        CompositeConfigComponent configComponent = new CompositeConfigComponent(e);
        Editor editor = e.getDataContext().getData(CommonDataKeys.EDITOR);

        Project project = editor.getProject();
        String projectToken = configComponent.getProjectToken();
        String projectId = configComponent.getProjectId();
        String yapiUrl = configComponent.getYapiUrl();
        String projectType = configComponent.getProjectType();
        String returnClass = configComponent.getReturnClass();
        String attachUpload = configComponent.getAttachUpload();

        // ����У��
        if (Strings.isNullOrEmpty(projectToken) || Strings.isNullOrEmpty(projectId) || Strings.isNullOrEmpty(yapiUrl) || Strings.isNullOrEmpty(projectType)) {
            Messages.showErrorDialog("������Ŀ��.ideaĿ¼�µ�misc.xml������[projectToken,projectId,yapiUrl,projectType] " ,"��ȡ����ʧ�ܣ�");
            return;
        }
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
                for (YapiApiDTO apiDTO : yapiApiDTOS) {
                    String path = YapiApiDTOPathProcessor.getPath(apiDTO);
                    String title = YapiApiDTOPathProcessor.getTitle(apiDTO);
                    YapiSaveParam yapiSaveParam = new YapiSaveParam(projectToken, title, path, apiDTO.getParams(), apiDTO.getRequestBody(), apiDTO.getResponse(), Integer.valueOf(projectId), yapiUrl, true, apiDTO.getMethod(), apiDTO.getDesc(), apiDTO.getHeader());
                    yapiSaveParam.setReq_body_form(apiDTO.getReq_body_form());
                    yapiSaveParam.setReq_body_type(apiDTO.getReq_body_type());
                    yapiSaveParam.setReq_params(apiDTO.getReq_params());
                    yapiSaveParam.setStatus(apiDTO.getStatus());
                    if (!Strings.isNullOrEmpty(apiDTO.getMenu())) {
                        yapiSaveParam.setMenu(apiDTO.getMenu());
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
