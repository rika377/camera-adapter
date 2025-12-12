package org.camera.cameratool.pojo.dto;

import jakarta.validation.constraints.NotBlank;
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
@NoArgsConstructor
@AllArgsConstructor
public class CameraTakePhotoDto implements Serializable {
	private long lUserId;
	@NotNull
	private Integer channel;
	@NotBlank
	private String path;
}
