package com.stablevideo.storage;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.FileAttribute;
import java.nio.file.attribute.PosixFilePermission;
import java.nio.file.attribute.PosixFilePermissions;
import java.util.Set;
import java.util.UUID;

import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import com.stablevideo.exception.UnusableFileException;

@Component
public class FileStorageServiceImpl implements FileStorageService {

	public Path storeOriginal(MultipartFile file) throws UnusableFileException, IOException {
		
		if ( ! file.getOriginalFilename().split("\\.")[1].equals("mp4") || file.getSize() == 0 ) {
			throw new UnusableFileException();
		} 
		
		Set<PosixFilePermission> perms = PosixFilePermissions.fromString("rwxr-x---");
		FileAttribute<Set<PosixFilePermission>> attr = PosixFilePermissions.asFileAttribute(perms);
		
		UUID uniqueFolder = UUID.randomUUID();
		
		Path folderPath = Files.createDirectories(Paths.get("/Users", "tlong", "stabiliseVideos", uniqueFolder.toString()));
		Path filePath = folderPath.resolve("video.mp4");
		Files.createFile(filePath, attr);
		Files.write(filePath, file.getBytes());

		return filePath;
	}
}
