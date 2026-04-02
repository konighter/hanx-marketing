package com.hzltd.module.amz.service;

import cn.hutool.http.HttpRequest;
import com.hzltd.module.infra.service.file.FileService;
import jakarta.annotation.Resource;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

@Log4j2
@Service
public class ErplusFileService  {

    @Resource
    private FileService fileService;

    public String createFile(String url, String fileName, String path) {
        try{
            byte[] bytes = HttpRequest.get(url).execute().bodyBytes();
            return fileService.createFile(bytes, fileName, path, null);
        } catch (Exception e) {
            log.error("[createFile][创建文件({}) 失败]", fileName, e);
            return null;
        }

    }







}
