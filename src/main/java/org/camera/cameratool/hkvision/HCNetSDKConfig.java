package org.camera.cameratool.hkvision;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class HCNetSDKConfig {

    public static String libraryPath;
	public static String pathTwo;
	public static String pathThree;
	public static String pathFour;
	public static String flag1;
	public static String flag2;
	public static String flag3;
	public static String operatingSystem;

    @Value("${hik.libpath}")
    public void setLibraryPath(String path) {
		libraryPath = path;
    }

	@Value("${camera.hiktwo}")
	public void setSPath(String path) {
		pathTwo = path;
	}

	@Value("${camera.hikthree}")
	public void setThreePath(String path) {
		pathThree = path;
	}

	@Value("${camera.hikfour}")
	public void setFourPath(String path) {
		pathFour = path;
	}

	@Value("${camera.flag1}")
	public void setFlag1(String path) {
		flag1 = path;
	}

	@Value("${camera.flag2}")
	public void setFlag2(String path) {
		flag2 = path;
	}
	@Value("${camera.flag3}")
	public void setFlag3(String path) {
		flag3 = path;
	}
	@Value("${camera.system}")
	public void setOperatingSystem(String sys) {
		operatingSystem = sys;
	}

}
