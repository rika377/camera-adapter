package org.camera.cameratool.pojo.dto;/**
 * Created with IntelliJ IDEA.
 *
 * @Author: AG的狗腿子
 * @Date: 2024/08/14/下午4:03
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
public class CameraPresetDtoNew implements Serializable {
    private CameraDto cameraDto;
    private CameraPresetDto cameraPresetDto;
}
