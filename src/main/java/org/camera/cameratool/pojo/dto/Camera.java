package org.camera.cameratool.pojo.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;

/**
 * 摄像头信息表
 *
 * @TableName camera
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Camera implements Serializable {
	public Camera(String cameraIp, long cameraPort, String cameraUsername, String cameraPassword){
		this.cameraIp = cameraIp;
		this.cameraPort = cameraPort;
		this.cameraUsername = cameraUsername;
		this.cameraPassword = cameraPassword;
	}
	/**
	 * 摄像头id
	 */
	private Long cameraId;

	/**
	 * 摄像头名称
	 */
	private String cameraName;

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

	/**
	 * 摄像头厂家
	 */
	private String cameraManufactor;
	/**
	 * 站场id
	 */
	private Long stationId;

	/**
	 * 创建人id
	 */
	private Long creatorId;

	/**
	 * 创建人
	 */
	private String creator;

	/**
	 * 创建时间
	 */
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date createTime;

	/**
	 * 修改人id
	 */
	private Long modificationId;

	/**
	 * 修改人
	 */
	private String modificationName;

	/**
	 * 修改时间
	 */
	private Date modificationTime;
	/**
	 * 协议
	 */
	private String agreement;
	/**
	 * 给前端使用的字符串通道号集合【做回显用】
	 */
	private String channelList;
}
