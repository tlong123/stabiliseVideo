package com.stablevideo.controller;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.stablevideo.entity.Video;
import com.stablevideo.exception.UnusableFileException;
import com.stablevideo.service.VideoStabiliserService;
import com.stablevideo.storage.FileStorageService;

@RestController
public class DefaultController {
	
	@Autowired
	FileStorageService fileStorageService;
	
	@Autowired
	VideoStabiliserService videoStabiliserService;
	
	Logger logger = LoggerFactory.getLogger(DefaultController.class);

	@PostMapping("/stabiliseVideo")
	public ResponseEntity<InputStreamResource> getStableVideo(@RequestParam("file") MultipartFile file) {
		
		logger.info("new request received");
		
		Path storageLocation;
		Video stableVideo;
		try {
			storageLocation = fileStorageService.storeOriginal(file);
			stableVideo = videoStabiliserService.stabiliseVideo(storageLocation);
		} catch ( UnusableFileException e ) {
			HttpHeaders headers = new HttpHeaders();
			headers.add("Error", "file provided cannot be used");
			return new ResponseEntity<>( headers, HttpStatus.NOT_ACCEPTABLE );
		} catch ( IOException e ) {
			HttpHeaders headers = new HttpHeaders();
			headers.add("Error", "Failed to store file on server");
			return new ResponseEntity<>( headers, HttpStatus.BAD_REQUEST );
		}
		
		InputStream response = stableVideo.getVideoData();
		
		return new ResponseEntity<>(new InputStreamResource(response), new HttpHeaders(), HttpStatus.OK);
	}
}
