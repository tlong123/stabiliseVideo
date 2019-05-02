package com.stablevideo.imageprocessing;

import static org.bytedeco.opencv.global.opencv_imgproc.Canny;
import static org.bytedeco.opencv.global.opencv_imgproc.GaussianBlur;
import static org.bytedeco.opencv.global.opencv_imgproc.cvtColor;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.bytedeco.javacv.Java2DFrameConverter;
import org.bytedeco.javacv.OpenCVFrameConverter;
import org.bytedeco.opencv.opencv_core.Mat;
import org.bytedeco.opencv.opencv_core.Size;
import org.opencv.imgproc.Imgproc;

public class CannyImageGenerator {
	
	public static List<BufferedImage> getCannyImages(List<BufferedImage> images) {
		
		List<Mat> matrices = new ArrayList<>(images.size());
		images.stream().forEach(image -> matrices.add(new Mat()));
		
		images.stream().parallel().forEach(image -> { 
			Mat mat = new OpenCVFrameConverter.ToMat().convert( new Java2DFrameConverter().convert( image ) );
			cvtColor(mat, mat, Imgproc.COLOR_BGR2GRAY);
			GaussianBlur(mat, mat, new Size(7, 7), 0);
			Canny(mat, mat, 2, 6, 3, false);
			matrices.set(images.indexOf(image), mat);
		} );
				
		return matrices.stream().map(matrix -> new Java2DFrameConverter().convert( new OpenCVFrameConverter.ToIplImage().convert(matrix) ) ).collect(Collectors.toList());	    
	}
}
