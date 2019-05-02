package com.stablevideo.storage;

import java.io.IOException;
import java.nio.file.Path;

import org.springframework.web.multipart.MultipartFile;

import com.stablevideo.exception.UnusableFileException;

public interface FileStorageService {

	public Path storeOriginal(MultipartFile file) throws UnusableFileException, IOException;
}
