package com.stablevideo.storage;

import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.MockitoAnnotations;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import com.stablevideo.exception.UnusableFileException;

@RunWith(MockitoJUnitRunner.class)
public class FileStorageServiceImplTest {

	@Mock
	MultipartFile file;
	
	MultipartFile mockFile;
	
	@Before
	public void setup() {
	    MockitoAnnotations.initMocks(this);
	}
	
	@Test(expected = UnusableFileException.class)
	public void testWhenNonMp4FileProvidedExceptionThrows() throws UnusableFileException, IOException {
		Mockito.when(file.getOriginalFilename()).thenReturn("failVideo.mpeg");
		FileStorageServiceImpl storageService = new FileStorageServiceImpl();
		storageService.storeOriginal(file);
	}
	
	@Test(expected = UnusableFileException.class)
	public void testWhenEmptyMp4FileProvidedExceptionThrows() throws UnusableFileException, IOException {
		Mockito.when(file.getOriginalFilename()).thenReturn("failVideo.mp4");
		FileStorageServiceImpl storageService = new FileStorageServiceImpl();
		storageService.storeOriginal(file);
	}
	
	@Test
	public void testWhenMp4FileProvidedNewFileSaved() throws UnusableFileException, IOException {
		
		Path path = Paths.get("src", "test", "resources", "SampleVideo_1280x720_2mb.mp4");

		String name = "SampleVideo_1280x720_2mb.mp4";
		String originalFileName = "SampleVideo_1280x720_2mb.mp4";
		String contentType = "application/octet-stream";
		byte[] content = null;
		
		try {
		    content = Files.readAllBytes(path);
		} catch (final IOException e) {}
		
		mockFile = new MockMultipartFile(name, originalFileName, contentType, content);
		
		FileStorageServiceImpl storageService = new FileStorageServiceImpl();
		
		Path resultPath = null;

		resultPath = storageService.storeOriginal(mockFile);
		
		assertTrue(Files.exists(resultPath));
		
		try {
			Files.deleteIfExists(resultPath);
			Files.deleteIfExists(resultPath.getParent());

		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
