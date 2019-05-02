package com.stablevideo.service;

import org.bytedeco.javacv.FrameGrabber.Exception;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

import java.awt.image.BufferedImage;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class VideoStabiliserServiceImplTest {

	@Test
	public void testWhenGetVideoImagesCalledCorrectNumberOfImagesReturned() throws Exception {
		Path testVideoPath = Paths.get("src","test","resources","SampleVideo_1280x720_2mb.mp4");
		VideoStabiliserServiceImpl service = new VideoStabiliserServiceImpl();
		List<BufferedImage> images = null;
		images = service.getVideoImages(testVideoPath);

		assertEquals(images.size(), 337);
	}
	
}
