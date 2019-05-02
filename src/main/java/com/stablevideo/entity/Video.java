package com.stablevideo.entity;

import java.io.InputStream;

public class Video {

		private InputStream videoData;
		
		public Video() {}
		
		public Video(InputStream stream) {
			this.videoData = stream;
		}

		public InputStream getVideoData() {
			return videoData;
		}

		public void setVideoData(InputStream videoData) {
			this.videoData = videoData;
		}
}
