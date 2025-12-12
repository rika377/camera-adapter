package org.camera.cameratool.pojo.dto;

import jakarta.validation.constraints.NotNull;
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
public class CameraPresetDto implements Serializable {
	private long lUserId;
	@NotNull
	private Integer channel;
	@NotNull
	private Integer	command;
	@NotNull
	private Integer	preset;
	private int brand;
}
