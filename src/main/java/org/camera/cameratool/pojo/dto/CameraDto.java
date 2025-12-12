package org.camera.cameratool.pojo.dto;/**
 * Created with IntelliJ IDEA.
 *
 * @Author: AG的狗腿子
 * @Date: 2024/08/14/下午3:31
 * @Description:
 */

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.Serializable;

/**
 * Author:drq
 * Title:
 * Description:
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CameraDto implements Serializable {
    /**
     * 摄像设备类型【1：硬盘录像机，2：激光云台，0：单个摄像头】
     */
    private Integer flag;

    /**
     * 摄像头厂商
     * 1：海康威视
     * 2：大华
     */
    @NotNull
    private Integer brand;

    /**
     * 摄像头IP地址
     */
    @NotBlank
    private String cameraIp;

    /**
     * 摄像头端口号
     */
    @NotNull
    private long cameraPort;

    /**
     * 摄像头用户名
     */
    @NotBlank
    private String cameraUsername;

    /**
     * 摄像头密码
     */
    @NotBlank
    private String cameraPassword;
}
