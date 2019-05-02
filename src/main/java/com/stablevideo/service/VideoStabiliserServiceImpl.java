package com.stablevideo.service;

import java.awt.FlowLayout;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.bytedeco.ffmpeg.global.avcodec;
import org.bytedeco.ffmpeg.global.swscale;
import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.FFmpegFrameRecorder;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.FrameGrabber;
import org.bytedeco.javacv.FrameGrabber.Exception;
import org.bytedeco.javacv.FrameRecorder;
import org.bytedeco.javacv.Java2DFrameConverter;
import org.springframework.stereotype.Component;

import com.stablevideo.entity.Video;
import com.stablevideo.imageprocessing.CannyImageGenerator;

@Component
public class VideoStabiliserServiceImpl implements VideoStabiliserService {
	
	Logger logger = LoggerFactory.getLogger(VideoStabiliserServiceImpl.class);

	
	
	public Video stabiliseVideo(Path storageLocation) throws IOException {
		
		Instant start = Instant.now();
		
		List<BufferedImage> images = CannyImageGenerator.getCannyImages(getVideoImages(storageLocation));
		
		Instant finish = Instant.now();
		
		logger.info("processing time: " + Duration.between(start, finish).toMillis() + " ms");
		
		start = Instant.now();
		
		Path finalVideo = recordOutputVideo(storageLocation, images);
		
		finish = Instant.now();
		
		logger.info("recording time: " + Duration.between(start, finish).toMillis() + "ms");
		
		return new Video(Files.newInputStream(finalVideo));
	}
	
	
	
	public List<BufferedImage> getVideoImages(Path storageLocation) throws Exception {
		
		File file = new File(storageLocation.toUri());
		FFmpegFrameGrabber frameGrabber = new FFmpegFrameGrabber(file);
		frameGrabber.start();
		int numberOfFrames = frameGrabber.getLengthInFrames();
		
		logger.info("number of frames in video: " + numberOfFrames);
		
		List<BufferedImage> images = new ArrayList<>(numberOfFrames);
		
		Frame frame = null;
		
		while ( (frame = frameGrabber.grabImage()) != null ) {
			     
			if ( frame.image != null ) {
				images.add(new Java2DFrameConverter().convert(frame));
			} else if ( images.size() > 0 ) {
				
				images.add(images.get(images.size()-1));
			}
		}; 
		
		frameGrabber.close();
		
		logger.info("images size is" + images.size());
	
		return images;
	}
	
	
	
	public Path recordOutputVideo(Path storageLocation, List<BufferedImage> images) throws FrameGrabber.Exception, FrameRecorder.Exception {
		
		FFmpegFrameGrabber frameGrabber = new FFmpegFrameGrabber(new File(storageLocation.toUri()));
		frameGrabber.start();
		Path newFileLocation = storageLocation.getParent().resolve("wireframeVideo.mp4");
		
		FFmpegFrameRecorder recorder = new FFmpegFrameRecorder(newFileLocation.toFile(), 
															  images.get(0).getWidth(), 
															  images.get(0).getHeight());
		recorder.setInterleaved(true);
		recorder.setVideoOption("tune", "zerolatency");
		recorder.setVideoOption("preset", "ultrafast");
		recorder.setVideoOption("crf", "28");
		recorder.setVideoBitrate(frameGrabber.getVideoBitrate());
        recorder.setVideoCodec(frameGrabber.getVideoCodec());
        recorder.setFormat(frameGrabber.getFormat());
        recorder.setFrameRate(frameGrabber.getFrameRate());
        recorder.setGopSize((int) (frameGrabber.getFrameRate() * 2));
        recorder.setAudioOption("crf", "0");
        recorder.setAudioQuality(0);
        recorder.setAudioBitrate(frameGrabber.getAudioBitrate());
        recorder.setSampleRate(frameGrabber.getSampleRate());
        recorder.setAudioChannels(frameGrabber.getAudioChannels());
        recorder.setAudioCodec(avcodec.AV_CODEC_ID_AAC);
        recorder.setImageScalingFlags(swscale.SWS_BICUBIC);
        
        recorder.start();
        
        Frame originalFrame = null;
                
        Java2DFrameConverter converter = new Java2DFrameConverter();
                
        	for ( int index = 0; index < images.size(); index++ ) {
        		
        		originalFrame = frameGrabber.grab();
        		
        		Frame newFrame = converter.getFrame(images.get(index));
        		newFrame.audioChannels = originalFrame.audioChannels;
        		newFrame.sampleRate = originalFrame.sampleRate;
        		newFrame.samples = originalFrame.samples;

	    	  	recorder.record(newFrame);
    
        	}

        	recorder.close();
        	frameGrabber.close();
					
		return newFileLocation;
	}
	
	
	
	public void displayImage(BufferedImage image) {
		
		ImageIcon icon = new ImageIcon(image);
		JFrame frame = new JFrame();
        frame.setLayout(new FlowLayout());
        frame.setSize(image.getWidth(),image.getHeight());
        JLabel lbl=new JLabel();
        lbl.setIcon(icon);
        frame.add(lbl);
        frame.setVisible(true);
	}
}
