package com.qbb.util;

import com.google.common.base.Strings;
import com.intellij.openapi.project.Project;
import com.intellij.psi.JavaPsiFacade;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiField;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.impl.source.PsiClassImpl;
import com.intellij.psi.impl.source.PsiJavaFileImpl;
import com.intellij.psi.javadoc.PsiDocComment;
import com.intellij.psi.javadoc.PsiDocTag;
import com.intellij.psi.search.GlobalSearchScope;
import com.qbb.constant.YapiStatusEnum;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * ��������,��ű��⣬�˵��������ȹ�����
 *
 * @author chengsheng@qbb6.com
 * @date 2019/4/30 4:13 PM
 */
public class DesUtil {


    static Pattern humpPattern = Pattern.compile("[A-Z]");

    static final String DASH = "-";

    /**
     * ȥ���ַ�����β���ֵ�ĳ���ַ�.
     * @param source Դ�ַ���.
     * @param element ��Ҫȥ�����ַ�.
     * @return String.
     */
    public static String trimFirstAndLastChar(String source,char element) {
        boolean beginIndexFlag = true;
        boolean endIndexFlag = true;
        do {
            if(Strings.isNullOrEmpty(source.trim()) || source.equals(String.valueOf(element))){
                source="";
                break;
            }
            int beginIndex = source.indexOf(element) == 0 ? 1 : 0;
            int endIndex = source.lastIndexOf(element) + 1 == source.length() ? source.lastIndexOf(element) : source.length();
            source = source.substring(beginIndex, endIndex);
            beginIndexFlag = (source.indexOf(element) == 0);
            endIndexFlag = (source.lastIndexOf(element) + 1 == source.length());
        } while (beginIndexFlag || endIndexFlag);
        return source;
    }


    /**
     * @description: �������
     * @param: [psiMethodTarget]
     * @return: java.lang.String
     * @author: chengsheng@qbb6.com
     * @date: 2019/2/2
     */
    public static String getDescription(PsiMethod psiMethodTarget){
        if(psiMethodTarget.getDocComment()!=null) {
            PsiDocTag[] psiDocTags = psiMethodTarget.getDocComment().getTags();
            for (PsiDocTag psiDocTag : psiDocTags) {
                if (psiDocTag.getText().contains("@description") || psiDocTag.getText().contains("@Description")) {
                    return trimFirstAndLastChar(psiDocTag.getText().replace("@description", "").replace("@Description", "").replace(":", "").replace("*", "").replace("\n", " "), ' ');
                }
            }
            return trimFirstAndLastChar(psiMethodTarget.getDocComment().getText().split("@")[0].replace("@description", "").replace("@Description", "").replace(":", "").replace("*", "").replace("/", "").replace("\n", " "), ' ').trim();
        }
        return null;
    }

    /**
     * @description: ͨ��paramName �������
     * @param: [psiMethodTarget, paramName]
     * @return: java.lang.String
     * @author: chengsheng@qbb6.com
     * @date: 2019/5/22
     */ 
    public static String getParamDesc(PsiMethod psiMethodTarget,String paramName){
        if(psiMethodTarget.getDocComment()!=null) {
            PsiDocTag[] psiDocTags = psiMethodTarget.getDocComment().getTags();
            for (PsiDocTag psiDocTag : psiDocTags) {
                if ((psiDocTag.getText().contains("@param") || psiDocTag.getText().contains("@Param")) && (!psiDocTag.getText().contains("[")) && psiDocTag.getText().contains(paramName)) {
                    return trimFirstAndLastChar(psiDocTag.getText().replace("@param", "").replace("@Param", "").replace(paramName,"").replace(":", "").replace("*", "").replace("\n", " "), ' ').trim();
                }
            }
        }
        return "";
    }

    /**
     * @description: �������ע��
     * @param: [psiDocComment]
     * @return: java.lang.String
     * @author: chengsheng@qbb6.com
     * @date: 2019/4/27
     */
    public static String getFiledDesc(PsiDocComment psiDocComment){
        if(Objects.nonNull(psiDocComment)) {
            String fileText = psiDocComment.getText();
            if (!Strings.isNullOrEmpty(fileText)) {
                return trimFirstAndLastChar(fileText.replace("*", "").replace("/", "").replace(" ", "").replace("\n", ",").replace("\t", ""), ',').split("\\{@link")[0];
            }
        }
        return "";
    }
    /**
     * @description: �������url
     * @param: []
     * @return: java.lang.String
     * @author: chengsheng@qbb6.com
     * @date: 2019/5/18
     */ 
    public static String getUrlReFerenceRDesc(String text){
        if(Strings.isNullOrEmpty(text)){
            return text;
        }
        if(!text.contains("*/")){
            return null;
        }
        return DesUtil.trimFirstAndLastChar(text.split("\\*/")[0].replace("@description","").replace("@Description","").split("@")[0].replace(":","").replace("*","").replace("/","").replace("\n"," "),' ');
    }
    
    /**
     * @description: ��ò˵�
     * @param: [text]
     * @return: java.lang.String
     * @author: chengsheng@qbb6.com
     * @date: 2019/5/18
     */
    public static String getMenu(String text) {
        if (Strings.isNullOrEmpty(text) || !text.contains("*/")) {
            return null;
        }
        String[] menuList = text.split("\\*/")[0].split("@menu");
        if (menuList.length > 1) {
            return DesUtil.trimFirstAndLastChar(menuList[1].split("\\*")[0].replace("*", "").replace(":","").replace("\n", " ").replace(" ",""), ' ').trim();
        } else {
            return null;
        }
    }

    /**
     * ���·��
     * @param text
     * @return
     */
    public static String getPath(String text){
        if (Strings.isNullOrEmpty(text) || !text.contains("*/")) {
            return null;
        }
        String[] menuList = text.split("\\*/")[0].split("@path");
        if (menuList.length > 1) {
            return DesUtil.trimFirstAndLastChar(menuList[1].split("\\*")[0].replace("*", "").replace(":","").replace("\n", " ").replace(" ",""), ' ').trim();
        } else {
            return null;
        }
    }

    /**
     * @description: ���״̬
     * @param: [text]
     * @return: java.lang.String
     * @author: chengsheng@qbb6.com
     * @date: 2019/5/18
     */
    public static String getStatus(String text) {
        if (Strings.isNullOrEmpty(text) || !text.contains("*/")) {
            return null;
        }
        String[] menuList = text.split("\\*/")[0].split("@status");
        if (menuList.length > 1) {
            return YapiStatusEnum.getStatus(DesUtil.trimFirstAndLastChar(menuList[1].split("\\*")[0].replace("*", "").replace(":","").replace("\n", " ").replace(" ",""), ' ').trim());
        } else {
            return null;
        }
    }

    /**
     * @description: ���link ��ע
     * @param: [remark, project, field]
     * @return: java.lang.String
     * @author: chengsheng@qbb6.com
     * @date: 2019/5/18
     */
    public static String getLinkRemark(String remark, Project project, PsiField field){
        // ���Ի��@link �ĳ�������
        if(Objects.isNull(field.getDocComment())){
            return remark;
        }
        String[] linkString=field.getDocComment().getText().split("@link");
        if(linkString.length>1){
            //˵����link
            String linkAddress=linkString[1].split("}")[0].trim();
            PsiClass psiClassLink= JavaPsiFacade.getInstance(project).findClass(linkAddress, GlobalSearchScope.allScope(project));
            if(Objects.isNull(psiClassLink)) {
                //����û�л��ȫ·�������Ի��ȫ·��
                String[] importPaths=field.getParent().getContext().getText().split("import");
                if(importPaths.length>1){
                    for(String importPath:importPaths){
                        importPath=importPath.split(";")[0];
                        if(importPath.contains(linkAddress.split("\\.")[0])){
                            linkAddress=importPath.split(linkAddress.split("\\.")[0])[0]+linkAddress;
                            psiClassLink=JavaPsiFacade.getInstance(project).findClass(linkAddress.trim(),GlobalSearchScope.allScope(project));
                            break;
                        }
                    }
                }
                if(Objects.isNull(psiClassLink)){
                    //�����ͬ�����
                    linkAddress= ((PsiJavaFileImpl) ((PsiClassImpl) field.getParent()).getContext()).getPackageName()+"."+linkAddress;
                    psiClassLink= JavaPsiFacade.getInstance(project).findClass(linkAddress, GlobalSearchScope.allScope(project));
                }
                //���С�ڵ���һΪ������import����������
            }
            if(Objects.nonNull(psiClassLink)){
                //˵�������link ��class
                PsiField[] linkFields= psiClassLink.getFields();
                if(linkFields.length>0){
                    remark+=","+psiClassLink.getName()+"[";
                    for (int i=0;i<linkFields.length;i++){
                        PsiField psiField=linkFields[i];
                        if(i>0){
                            remark+=",";
                        }
                        // �Ȼ������
                        remark+=psiField.getName();
                        // ����value,ͨ��= ����ȡ��ã��ڶ���ֵ���ٽ�ȡ;
                        String[] splitValue = psiField.getText().split("=");
                        if(splitValue.length>1){
                            String value=splitValue[1].split(";")[0];
                            remark+=":"+value;
                        }
                        String filedValue=DesUtil.getFiledDesc(psiField.getDocComment());
                        if(!Strings.isNullOrEmpty(filedValue)){
                            remark+="("+filedValue+")";
                        }
                    }
                    remark+="]";
                }
            }
        }
        return remark;
    }


    /**
     * @description: ��ô�start ��ʼ end �����м������
     * @param: [content, start, end]
     * @return: java.util.List<java.lang.String>
     * @author: chengsheng@qbb6.com
     * @date: 2019/7/2
     */ 
    public static List<PsiClass> getFieldLinks(Project project,PsiField field){
        if(Objects.isNull(field.getDocComment())){
            return new ArrayList<>();
        }
       List<PsiClass> result=new ArrayList<>();
       String[] linkstr=field.getDocComment().getText().split("@link");
       for(int i=1;i<linkstr.length;i++){
           try {
               String linkAddress = linkstr[i].split("}")[0].trim();
               PsiClass psiClassLink = JavaPsiFacade.getInstance(project).findClass(linkAddress, GlobalSearchScope.allScope(project));
               if (Objects.isNull(psiClassLink)) {
                   //����û�л��ȫ·�������Ի��ȫ·��
                   String[] importPaths = field.getParent().getContext().getText().split("import");
                   if (importPaths.length > 1) {
                       for (String importPath : importPaths) {
                           importPath = importPath.split(";")[0];
                           if (importPath.contains(linkAddress.split("\\.")[0])) {
                               linkAddress = importPath.split(linkAddress.split("\\.")[0])[0] + linkAddress;
                               psiClassLink = JavaPsiFacade.getInstance(project).findClass(linkAddress.trim(), GlobalSearchScope.allScope(project));
                               if (Objects.nonNull(psiClassLink)) {
                                   result.add(psiClassLink);
                               }
                               break;
                           }
                       }
                   }
                   if (Objects.isNull(psiClassLink)) {
                       //�����ͬ�����
                       linkAddress = ((PsiJavaFileImpl) ((PsiClassImpl) field.getParent()).getContext()).getPackageName() + "." + linkAddress;
                       psiClassLink = JavaPsiFacade.getInstance(project).findClass(linkAddress, GlobalSearchScope.allScope(project));
                       if (Objects.nonNull(psiClassLink)) {
                           result.add(psiClassLink);
                       }
                   }
                   //���С�ڵ���һΪ������import����������
               } else {
                   result.add(psiClassLink);
               }
           }catch (Exception e){
           }
       }
       return result;
    }
    
    /**
     * @description: ��װ·��
     * @param: [path, subPath]
     * @return: void
     * @author: chengsheng@qbb6.com
     * @date: 2019/9/25
     */ 
    public static void addPath(StringBuilder path,String subPath){
        if(subPath.startsWith("/")){
            path.append(subPath);
        }else{
            path.append("/").append(subPath);
        }
    }

    /**
     * �շ�ת��  ����swagger
     *
     * @param camelCase
     * @return
     */
    public static String camelToLine(String camelCase, String split) {
        if(Strings.isNullOrEmpty(split)){
            split=DASH;
        }
        Matcher matcher = humpPattern.matcher(camelCase);
        StringBuffer sb = new StringBuffer();
        while (matcher.find()) {
            matcher.appendReplacement(sb, split + matcher.group(0).toLowerCase());
        }
        matcher.appendTail(sb);
        String result = sb.toString();
        if (result.startsWith(split)) {
            result = result.substring(split.length());
        }
        return result;
    }

}
