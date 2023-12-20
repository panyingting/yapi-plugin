package com.qbb.upload;

import com.google.common.base.Strings;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.qbb.constant.YapiConstant;
import com.qbb.dto.*;
import com.qbb.util.HttpClientUtil;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;

import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * �ϴ���yapi
 *
 * @author chengsheng@qbb6.com
 * @date 2019/1/31 11:41 AM
 */
public class UploadYapi {


    private Gson gson=new Gson();


    /**
     * @description: ���ñ���ӿ�
     * @param: [yapiSaveParam, attachUpload, path]
     * @return: com.qbb.dto.YapiResponse
     * @author: chengsheng@qbb6.com
     * @date: 2019/5/15
     */ 
    public YapiResponse  uploadSave(YapiSaveParam yapiSaveParam,String attachUpload,String path) throws IOException {
        if(Strings.isNullOrEmpty(yapiSaveParam.getTitle())){
            yapiSaveParam.setTitle(yapiSaveParam.getPath());
        }
        YapiHeaderDTO yapiHeaderDTO=new YapiHeaderDTO();
        if("form".equals(yapiSaveParam.getReq_body_type())){
            yapiHeaderDTO.setName("Content-Type");
            yapiHeaderDTO.setValue("application/x-www-form-urlencoded");
            yapiSaveParam.setReq_body_form(yapiSaveParam.getReq_body_form());
        }else{
            yapiHeaderDTO.setName("Content-Type");
            yapiHeaderDTO.setValue("application/json");
            yapiSaveParam.setReq_body_type("json");
        }
        if(Objects.isNull(yapiSaveParam.getReq_headers())){
            List list=new ArrayList();
            list.add(yapiHeaderDTO);
            yapiSaveParam.setReq_headers(list);
        }else{
            yapiSaveParam.getReq_headers().add(yapiHeaderDTO);
        }
        this.changeDesByPath(yapiSaveParam);
        YapiResponse yapiResponse= this.getCatIdOrCreate(yapiSaveParam);
        if(yapiResponse.getErrcode()==0){
            String response=HttpClientUtil.ObjectToString(HttpClientUtil.getHttpclient().execute(this.getHttpPost(yapiSaveParam.getYapiUrl()+YapiConstant.yapiSave,gson.toJson(yapiSaveParam))),"utf-8");
            YapiResponse yapiResponseResult= gson.fromJson(response,YapiResponse.class);
            yapiResponseResult.setCatId(yapiSaveParam.getCatid());
            return yapiResponseResult;
        }else{
            return yapiResponse;
        }
    }



    /**
     * ���httpPost
     * @return
     */
    private HttpPost getHttpPost(String url, String body) {
        HttpPost httpPost = null;
        try {
            httpPost = new HttpPost(url);
            httpPost.setHeader("Content-type", "application/json;charset=utf-8");
            HttpEntity reqEntity = new StringEntity(body == null ? "" : body, "UTF-8");
            httpPost.setEntity(reqEntity);
        } catch (Exception e) {
        }
        return httpPost;
    }

    /**
     * @description: �ϴ��ļ�
     * @param: [url, filePath]
     * @return: java.lang.String
     * @author: chengsheng@qbb6.com
     * @date: 2019/5/15
     */ 
    public String uploadFile(String url,String filePath){
        HttpPost httpPost = null;
        try {
            httpPost = new HttpPost(url);
            FileBody bin = new FileBody(new File(filePath));
            HttpEntity reqEntity = MultipartEntityBuilder.create().addPart("file", bin).build();
            httpPost.setEntity(reqEntity);
            return  HttpClientUtil.ObjectToString(HttpClientUtil.getHttpclient().execute(httpPost),"utf-8");
        } catch (Exception e) {
        }
        return "";
    }


    private HttpGet getHttpGet(String url){
        try {
            return HttpClientUtil.getHttpGet(url, "application/json", "application/json; charset=utf-8");
        } catch (IOException e) {
        }
        return null;
    }
    /**
     * @description: �������
     * @param: [yapiSaveParam]
     * @return: com.qbb.dto.YapiResponse
     * @author: chengsheng@qbb6.com
     * @date: 2019/7/28
     */ 
    public void changeDesByPath(YapiSaveParam yapiSaveParam){
        try{
            String response = HttpClientUtil.ObjectToString(HttpClientUtil.getHttpclient().execute(this.getHttpGet(yapiSaveParam.getYapiUrl()+ YapiConstant.yapiGetByPath+"?token="+yapiSaveParam.getToken()+"&path="+yapiSaveParam.getPath())),"utf-8");
            YapiResponse yapiResponse=gson.fromJson(response,YapiResponse.class);
            if(yapiResponse.getErrcode()==0) {
                YapiInterfaceResponse yapiInterfaceResponse=gson.fromJson(gson.toJson(yapiResponse.getData()),YapiInterfaceResponse.class);
                if(!Strings.isNullOrEmpty(yapiInterfaceResponse.getDesc())){
                    //���ԭ��������Ϊ�գ���ô�ͽ���ǰ����+��һ���汾�������Զ��岿��
                    if(yapiInterfaceResponse.getDesc().contains("java��")){
                        yapiSaveParam.setDesc(yapiInterfaceResponse.getDesc().substring(0,yapiInterfaceResponse.getDesc().indexOf("java��"))+yapiSaveParam.getDesc()+yapiInterfaceResponse.getDesc().substring(yapiInterfaceResponse.getDesc().indexOf("</pre>"),yapiInterfaceResponse.getDesc().length()));
                    }else{
                        yapiSaveParam.setDesc(yapiInterfaceResponse.getDesc().substring(0,yapiInterfaceResponse.getDesc().indexOf("<pre>"))+yapiSaveParam.getDesc()+yapiInterfaceResponse.getDesc().substring(yapiInterfaceResponse.getDesc().indexOf("</pre>"),yapiInterfaceResponse.getDesc().length()));
                    }
                }
                if(Objects.nonNull(yapiInterfaceResponse.getCatid())){
                    yapiSaveParam.setCatid(yapiInterfaceResponse.getCatid().toString());
                }
            }
        }catch (Exception e){

        }
    }

    /**
     * @description: ��÷�����ߴ����������
     * @param: [yapiSaveParam]
     * @return: com.qbb.dto.YapiResponse
     * @author: chengsheng@qbb6.com
     * @date: 2019/5/15
     */ 
    public YapiResponse getCatIdOrCreate(YapiSaveParam yapiSaveParam){
        // ������治���ڣ����Զ���˵�Ϊ�գ���ʹ��Ĭ��Ŀ¼
        if(Strings.isNullOrEmpty(yapiSaveParam.getMenu())){
            yapiSaveParam.setMenu(YapiConstant.menu);
        }
        String response= null;
        try {
            response = HttpClientUtil.ObjectToString(HttpClientUtil.getHttpclient().execute(this.getHttpGet(yapiSaveParam.getYapiUrl()+ YapiConstant.yapiCatMenu+"?project_id="+yapiSaveParam.getProjectId()+"&token="+yapiSaveParam.getToken())),"utf-8");
            YapiResponse yapiResponse=gson.fromJson(response,YapiResponse.class);
            if(yapiResponse.getErrcode()==0) {
                List<YapiCatResponse> list = (List<YapiCatResponse>) yapiResponse.getData();
                list=gson.fromJson(gson.toJson(list),new TypeToken<List<YapiCatResponse>>() {
                }.getType());
                String[] menus=yapiSaveParam.getMenu().split("/");
                // ѭ���༶�˵����ж��Ƿ���ڣ���������ھʹ���
                //  ����༶�˵���������
                Integer parent_id=-1;
                Integer now_id=null;
                for(int i=0;i<menus.length;i++){
                    if(Strings.isNullOrEmpty(menus[i])){
                        continue;
                    }
                    boolean needAdd=true;
                    now_id=null;
                    for (YapiCatResponse yapiCatResponse : list) {
                        if (yapiCatResponse.getName().equals(menus[i])) {
                            needAdd=false;
                            now_id=yapiCatResponse.get_id();
                            break;
                        }
                    }
                    if(needAdd){
                         now_id=this.addMenu(yapiSaveParam,parent_id,menus[i]);
                    }
                    if(i==(menus.length-1)) {
                        yapiSaveParam.setCatid(now_id.toString());
                    }else{
                        parent_id=now_id;
                    }
                }
            }
            return  new YapiResponse();
        } catch (Exception e) {
            try {
                //�����������������yapi �汾��֧��
                yapiSaveParam.setCatid(addMenu(yapiSaveParam,-1,yapiSaveParam.getMenu()).toString());
                return new YapiResponse();
            } catch (IOException e1) {
            }
            return  new YapiResponse(0,e.toString());
        }
    }


    /**
     * @description: �����˵�
     * @param: [yapiSaveParam, parent_id]
     * @return: java.lang.Integer
     * @author: chengsheng@qbb6.com
     * @date: 2019/7/28
     */ 
    private Integer addMenu(YapiSaveParam yapiSaveParam,Integer parent_id,String menu) throws IOException{
        YapiCatMenuParam  yapiCatMenuParam=new YapiCatMenuParam(menu,yapiSaveParam.getProjectId(),yapiSaveParam.getToken(),parent_id);
        String responseCat=HttpClientUtil.ObjectToString(HttpClientUtil.getHttpclient().execute(this.getHttpPost(yapiSaveParam.getYapiUrl()+YapiConstant.yapiAddCat,gson.toJson(yapiCatMenuParam))),"utf-8");
        YapiCatResponse yapiCatResponse=gson.fromJson(gson.fromJson(responseCat,YapiResponse.class).getData().toString(),YapiCatResponse.class);
        return yapiCatResponse.get_id();
    }


}
