package com.stablevideo.service;

import java.io.IOException;
import java.nio.file.Path;

import com.stablevideo.entity.Video;

public interface VideoStabiliserService {
	
	public Video stabiliseVideo(Path storageLocation) throws IOException;
}
