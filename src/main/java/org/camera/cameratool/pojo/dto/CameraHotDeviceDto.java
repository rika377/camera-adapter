package org.camera.cameratool.pojo.dto;/**
 * Created with IntelliJ IDEA.
 *
 * @Author: AG的狗腿子
 * @Date: 2024/11/08/下午1:41
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
public class CameraHotDeviceDto implements Serializable {
    private CameraDto cameraDto;
    private long lUserId;
}
