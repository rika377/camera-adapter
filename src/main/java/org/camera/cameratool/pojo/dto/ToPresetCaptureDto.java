package org.camera.cameratool.pojo.dto;/**
 * Created with IntelliJ IDEA.
 *
 * @Author: AG的狗腿子
 * @Date: 2024/08/15/上午9:30
 * @Description:
 */

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * Author:drq
 * Title:
 * Description:
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ToPresetCaptureDto implements Serializable {
    private CameraDto cameraDto;
    private CameraTakePhotoDto cameraTakePhotoDto;
    private CameraPresetDto cameraPresetDto;
}
