package com.sunghyun.controller;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.multipart.MultipartFile;
@Controller
public class UploadController {
	private static final Logger logger = LoggerFactory.getLogger(UploadController.class);
	
	@Resource(name = "uploadPath")
	private String uploadPath;
	
	
	@RequestMapping(value="/uploadForm", method = RequestMethod.GET)
    public void uploadForm() {
        
    }
	
	@RequestMapping(value="/uploadForm", method = RequestMethod.POST)
    public String uploadForm(MultipartFile file, Model model) throws IOException {
        logger.info("fileName : " + file.getOriginalFilename());
        logger.info("fileSize : " + file.getSize());
        logger.info("contentType : " + file.getContentType());
        
        String savedName = uploadFile(file.getOriginalFilename(), file.getBytes());
        
        model.addAttribute("savedName", savedName);
        
        return "uploadResult";
    }
    
    private String uploadFile(String originalName, byte[] fileData) throws IOException {
        UUID uuid = UUID.randomUUID();
        
        String savedName = uuid.toString() + "-" + originalName;
        
        File target = new File(uploadPath, savedName);
        FileCopyUtils.copy(fileData, target);
        
        return savedName;
    }
}
