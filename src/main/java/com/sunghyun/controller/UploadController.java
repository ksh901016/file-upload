package com.sunghyun.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLDecoder;
import java.util.UUID;

import javax.annotation.Resource;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.sunghyun.util.MediaUtils;
import com.sunghyun.util.UploadFileUtils;
@Controller
public class UploadController {
	private static final Logger logger = LoggerFactory.getLogger(UploadController.class);
	
	@Resource(name = "uploadPath")
	private String uploadPath;
	
	
	@RequestMapping(value="/uploadForm", method = RequestMethod.GET)
    public void uploadForm() {
        
    }
	
	@ResponseBody
    @RequestMapping("/displayFile")
    public ResponseEntity<byte[]> displayFile(String fileName) throws IOException{
        
        InputStream in = null;
        ResponseEntity<byte[]> entity = null;
        
        logger.info("FILE NAME : " + fileName);
        logger.info("DECODING : " + URLDecoder.decode(fileName, "UTF-8"));
        logger.info("DECODING : " + new String(fileName.getBytes("UTF-8"), "ISO-8859-1"));
        
        try {
            String formatName = fileName.substring(fileName.lastIndexOf(".")+1);
            
            MediaType mType = MediaUtils.getMediaType(formatName); // springframework mediaType
            
            HttpHeaders headers = new HttpHeaders(); // springframework http
            
            in = new FileInputStream(uploadPath + fileName);
            
            if(mType != null) {
                headers.setContentType(mType);
            }else {
                fileName = fileName.substring(fileName.indexOf("_")+1);
                headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
                headers.add("Content-Disposition", "attachment; filename=\"" + new String(fileName.getBytes("UTF-8"), "ISO-8859-1")+"\"");
                
            }
            
            entity = new ResponseEntity<byte[]>(IOUtils.toByteArray(in), headers, HttpStatus.CREATED); // apache.common IOUtils
            
        }catch(Exception e) {
            e.printStackTrace();
            entity = new ResponseEntity<byte[]>(HttpStatus.BAD_REQUEST);
        }finally {
            in.close();
        }
        
        return entity;
    }
	
	@RequestMapping(value="/uploadForm", method = RequestMethod.POST)
	@ResponseBody
    public String uploadForm(MultipartFile file, Model model) throws IOException {
        logger.info("fileName : " + file.getOriginalFilename());
        logger.info("fileSize : " + file.getSize());
        logger.info("contentType : " + file.getContentType());
        
        String savedName = uploadFile(file.getOriginalFilename(), file.getBytes());
        
        model.addAttribute("savedName", savedName);
        
        return "uploadResult";
    }
	
	@RequestMapping(value="/uploadAjax", method = RequestMethod.GET)
	public void uploadAjax() {
	    
	}
	
	@RequestMapping(value="/uploadAjax", method = RequestMethod.POST, produces = "text/plain;charset=UTF-8")
	public ResponseEntity<String> uploadAjax(MultipartFile file) throws IOException{
	    logger.info("fileName : " + file.getOriginalFilename());
        logger.info("fileSize : " + file.getSize());
        logger.info("contentType : " + file.getContentType());
        
        return new ResponseEntity<>(UploadFileUtils.uploadFile(uploadPath, file.getOriginalFilename(), file.getBytes()), HttpStatus.CREATED);
	}
    
    private String uploadFile(String originalName, byte[] fileData) throws IOException {
        UUID uuid = UUID.randomUUID();
        
        String savedName = uuid.toString() + "-" + originalName;
        
        File target = new File(uploadPath, savedName);
        FileCopyUtils.copy(fileData, target);
        
        return savedName;
    }
}
