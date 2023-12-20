package com.qbb.component;

import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.options.SearchableConfigurable;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectUtil;
import com.intellij.openapi.ui.Messages;
import com.intellij.ui.ToolbarDecorator;
import com.intellij.ui.components.JBList;
import com.qbb.dto.ConfigDTO;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.UUID;

/**
 * @author zhangyunfan
 * @version 1.0
 * @ClassName: ConfigComponent
 * @Description: ���ý���
 * @date 2020/12/25
 */
public class ConfigComponent implements SearchableConfigurable {

    private ConfigPersistence configPersistence = ConfigPersistence.getInstance();

    @NotNull
    @Override
    public String getId() {
        return UUID.randomUUID().toString().replaceAll("-", "");
    }

    @Nls(capitalization = Nls.Capitalization.Title)
    @Override
    public String getDisplayName() {
        return "YapiUpload";
    }

    private JBList<ConfigDTO> list;

    private DefaultListModel<ConfigDTO> defaultListModel;

    @Nullable
    @Override
    public JComponent createComponent() {
        final List<ConfigDTO> configDTOS = configPersistence.getConfigs();
        defaultListModel = new DefaultListModel<>();
        for (int i = 0, len = configDTOS == null ? 0 : configDTOS.size(); i < len; i++) {
            defaultListModel.addElement(configDTOS.get(i));
        }
        list = new JBList<>(defaultListModel);
        list.setLayout(new BorderLayout());
        list.setCellRenderer(new ItemComponent());

        // ������
        ToolbarDecorator decorator = ToolbarDecorator.createDecorator(list);
        decorator.setPreferredSize(new Dimension(0, 300));
        // ����
        decorator.setAddAction(actionButton -> addAction());
        // �༭
        decorator.setEditAction(anActionButton -> editAction());

        return decorator.createPanel();
    }

    private void editAction() {
        int index = list.getSelectedIndex();
        final Project project = ProjectUtil.guessCurrentProject(list);
        ItemAddEditDialog itemAddEditDialog = new ItemAddEditDialog(defaultListModel.get(index), project);
        if (itemAddEditDialog.showAndGet()) {
            final ConfigDTO config = itemAddEditDialog.getConfigDTO();
            for (int i = 0; i < defaultListModel.getSize(); i++) {
                if (i == index) {
                    continue;
                }
                final ConfigDTO dto = defaultListModel.get(i);
                if (dto.getProjectName().equals(config.getProjectName()) && dto.getModuleName().equals(config.getModuleName())) {
                    Messages.showErrorDialog("�༭�����ˣ�����Ӹ�ģ�����ã�", "Error");
                    return;
                }
            }
            defaultListModel.set(index, config);
            try {
                apply();
            } catch (ConfigurationException e) {
                e.printStackTrace();
            }
        } else {
            Messages.showInfoMessage("�ر�", "Info");
        }
    }

    private void addAction() {
        final Project project = ProjectUtil.guessCurrentProject(list);
        ItemAddEditDialog itemAddEditDialog = new ItemAddEditDialog(null, project);
        if (itemAddEditDialog.showAndGet()) {
            final ConfigDTO config = itemAddEditDialog.getConfigDTO();
            final Enumeration<ConfigDTO> elements = defaultListModel.elements();
            while (elements.hasMoreElements()) {
                final ConfigDTO dto = elements.nextElement();
                if (dto.getProjectName().equals(config.getProjectName()) && dto.getModuleName().equals(config.getModuleName())) {
                    Messages.showErrorDialog("��ӳ����ˣ�����Ӹ�ģ�����ã�", "Error");
                    return;
                }
            }
            defaultListModel.addElement(config);
            try {
                apply();
            } catch (ConfigurationException e) {
                e.printStackTrace();
            }
        } else {
            Messages.showInfoMessage("�ر�", "Info");
        }
    }

    @Override
    public boolean isModified() {
        if (configPersistence.getConfigs() == null) {
            return true;
        }
        //���û��޸����ò������ڵ����OK����Apply����ťǰ����ܻ��Զ����ø÷������ж��Ƿ����޸ģ��������ư�ť��OK����Apply�����Ƿ���á�
        return defaultListModel.size() == configPersistence.getConfigs().size();
    }

    @Override
    public void apply() throws ConfigurationException {
        //�û������OK����Apply����ť�����ø÷�����ͨ���������������Ϣ�־û���
        final Enumeration<ConfigDTO> elements = defaultListModel.elements();
        List<ConfigDTO> list = new ArrayList<>();
        while (elements.hasMoreElements()) {
            list.add(elements.nextElement());
        }
        configPersistence.setConfigs(list);
    }
}
