package org.camera.cameratool.dahua;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class DhConfig {

    public static String libraryPath;

    @Value("${dh.libpath}")
    public void setLibraryPath(String path) {
        libraryPath = path;
    }
}
