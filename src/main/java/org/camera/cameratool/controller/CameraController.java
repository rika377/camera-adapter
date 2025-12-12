package org.camera.cameratool.controller;/**
 * Created with IntelliJ IDEA.
 *
 * @Author: AG的狗腿子
 * @Date: 2024/08/13/上午10:46
 * @Description:
 */

import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.camera.cameratool.pojo.dto.*;
import org.camera.cameratool.util.CameraOperateUtils;
import org.springframework.web.bind.annotation.*;

/**
 * Author:drq
 * Title:
 * Description:
 */
@Slf4j
@RestController
@RequestMapping("cameraTool")
public class CameraController {
    @Resource
    private CameraOperateUtils cameraOperateUtils;

    /**
     * 图像抓拍
     * 适配摄像头厂家：海康、大华
     *
     * @param cameraTakePhotoDtoNew
     */
    @PostMapping("capture")
    public void capture(@RequestBody CameraTakePhotoDtoNew cameraTakePhotoDtoNew) {
        CameraDto camera = cameraTakePhotoDtoNew.getCamera();
        CameraTakePhotoDto cameraTakePhotoDto = cameraTakePhotoDtoNew.getCameraTakePhotoDto();
        Integer brand = camera.getBrand();//1:Hik，2：Dh
        //完成初始化步骤（初始化+登录）
        long lUserId = cameraOperateUtils.initStep(camera);
        cameraTakePhotoDto.setLUserId(lUserId);
        //拍照
        if (brand == 1) {
            cameraOperateUtils.takePhotoHik(cameraTakePhotoDto);
            //退出，清理缓存
            cameraOperateUtils.logoutHik(lUserId);
//            cameraOperateUtils.cleanHik();
        } else {
            cameraOperateUtils.takePhotoDh(cameraTakePhotoDto);
            //退出，清理缓存
            cameraOperateUtils.logoutDh(lUserId);
//            cameraOperateUtils.cleanDh();
        }
    }

    /**
     * 转到预置点，抓拍
     *
     * @param
     */
    @PostMapping("toPresetCapture")
    public void toPresetCapture(@RequestBody ToPresetCaptureDto toPresetCaptureDto) {
        CameraDto camera = toPresetCaptureDto.getCameraDto();
        CameraPresetDto cameraPresetDto = toPresetCaptureDto.getCameraPresetDto();
        CameraTakePhotoDto cameraTakePhotoDto = toPresetCaptureDto.getCameraTakePhotoDto();
        Integer brand = camera.getBrand();//1:Hik，2：Dh
        //完成初始化步骤（初始化+登录）
        long lUserId = cameraOperateUtils.initStep(camera);
        cameraTakePhotoDto.setLUserId(lUserId);
        cameraPresetDto.setLUserId(lUserId);
        cameraPresetDto.setCommand(39);//转到预置点
        //转到预置点，拍照
        if (brand == 1) {
            cameraOperateUtils.presetOperateHik(cameraPresetDto);
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                log.error("转到预置点后睡眠出错：{}", e.getMessage());
                throw new RuntimeException(e);
            }
            cameraOperateUtils.takePhotoHik(cameraTakePhotoDto);
            //退出，清理缓存
            cameraOperateUtils.logoutHik(lUserId);
//            cameraOperateUtils.cleanHik();
        } else {
            //TODO 暂时还没测试通过大华的预置点操作
            cameraOperateUtils.takePhotoDh(cameraTakePhotoDto);
            //退出，清理缓存
            cameraOperateUtils.logoutDh(lUserId);
//            cameraOperateUtils.cleanDh();
        }
    }

    /**
     * 预置点操作
     *
     * @param
     * @param
     */
    @PostMapping("presetOperate")
    public void presetOperate(@RequestBody CameraPresetDtoNew cameraPresetDtoNew) {
        CameraDto cameraDto = cameraPresetDtoNew.getCameraDto();
        CameraPresetDto cameraPresetDto = cameraPresetDtoNew.getCameraPresetDto();
        Integer brand = cameraDto.getBrand();//1:Hik，2：Dh
        //完成初始化步骤（初始化+登录）
        long lUserId = cameraOperateUtils.initStep(cameraDto);
        cameraPresetDto.setLUserId(lUserId);
        //预置点操作
        if (brand == 1) {
            cameraOperateUtils.presetOperateHik(cameraPresetDto);
            //退出，清理缓存
            cameraOperateUtils.logoutHik(lUserId);
//            cameraOperateUtils.cleanHik();
        } else {
            //TODO 暂时还没测试通过大华的预置点操作
            //退出，清理缓存
            cameraOperateUtils.logoutDh(lUserId);
//            cameraOperateUtils.cleanDh();
        }
    }

    @PostMapping("ptzOperate")
    public void ptzOperate(@RequestBody CameraPTZControlDtoNew cameraPTZControlDtoNew) {
        CameraDto cameraDto = cameraPTZControlDtoNew.getCameraDto();
        CameraPTZControlDto cameraPTZControlDto = cameraPTZControlDtoNew.getCameraPTZControlDto();
        Integer brand = cameraDto.getBrand();//1:Hik，2：Dh
        //完成初始化步骤（初始化+登录）
        long lUserId = cameraOperateUtils.initStep(cameraDto);
        cameraPTZControlDto.setLUserId(lUserId);
        //预置点操作
        if (brand == 1) {
            cameraOperateUtils.ptzControlHik(cameraPTZControlDto);
            //退出，清理缓存
            cameraOperateUtils.logoutHik(lUserId);
//            cameraOperateUtils.cleanHik();
        } else {
            //TODO 暂时还没测试通过大华的云台操作
            //退出，清理缓存
            cameraOperateUtils.logoutDh(lUserId);
//            cameraOperateUtils.cleanDh();
        }
    }

    @PostMapping("hot_alarm")
    public int getHotDeviceAlarm(@RequestBody CameraHotDeviceDto cameraHotDeviceDto) {
        CameraDto cameraDto = cameraHotDeviceDto.getCameraDto();
        Integer brand = cameraDto.getBrand();//1:Hik，2：Dh
        //完成初始化步骤（初始化+登录）
        long lUserId = cameraOperateUtils.initStep(cameraDto);
        cameraHotDeviceDto.setLUserId(lUserId);
        int hotDeviceAlarmInfo = 0;
        //获取热成像报警信息
        if (brand == 1) {
            hotDeviceAlarmInfo = cameraOperateUtils.getHotDeviceAlarmInfo(cameraHotDeviceDto);
            System.out.println("进入了判断，报警标识：" + hotDeviceAlarmInfo);
            //退出，清理缓存
            cameraOperateUtils.logoutHik(lUserId);
//            cameraOperateUtils.cleanHik();
        } else {
            //TODO 暂时还没测试通过大华的云台操作
            //退出，清理缓存
            cameraOperateUtils.logoutDh(lUserId);
//            cameraOperateUtils.cleanDh();
        }
        return hotDeviceAlarmInfo;
    }

    @PostMapping("alarm")
    public int getDeviceAlarm(@RequestBody CameraHotDeviceDto cameraHotDeviceDto) {
        CameraDto cameraDto = cameraHotDeviceDto.getCameraDto();
        Integer brand = cameraDto.getBrand();//1:Hik，2：Dh
        //完成初始化步骤（初始化+登录）
        long lUserId = cameraOperateUtils.initStep(cameraDto);
        cameraHotDeviceDto.setLUserId(lUserId);
        int hotDeviceAlarmInfo = -3;
        //获取报警信息
        if (brand == 1) {
            hotDeviceAlarmInfo = cameraOperateUtils.getDeviceAlarmInfo(cameraHotDeviceDto);
            System.out.println("进入了判断，报警标识：" + hotDeviceAlarmInfo);
            //退出，清理缓存
            cameraOperateUtils.logoutHik(lUserId);
//            cameraOperateUtils.cleanHik();
        } else {
            //TODO 暂时还没测试通过大华的云台操作
            //退出，清理缓存
            cameraOperateUtils.logoutDh(lUserId);
//            cameraOperateUtils.cleanDh();
        }
        return hotDeviceAlarmInfo;
    }

    @PostMapping("smart_alarm")
    public int getDeviceSmartAlarm(@RequestBody CameraHotDeviceDto cameraHotDeviceDto) {
        CameraDto cameraDto = cameraHotDeviceDto.getCameraDto();
        Integer brand = cameraDto.getBrand();//1:Hik，2：Dh
        //完成初始化步骤（初始化+登录）
        long lUserId = cameraOperateUtils.initStep(cameraDto);
        cameraHotDeviceDto.setLUserId(lUserId);
        int hotDeviceAlarmInfo = -3;
        //获取热成像报警信息
        if (brand == 1) {
            hotDeviceAlarmInfo = cameraOperateUtils.getDeviceSmartAlarmInfo(cameraHotDeviceDto);
            System.out.println("进入了smart判断，报警标识：" + hotDeviceAlarmInfo);
            //退出，清理缓存
            cameraOperateUtils.logoutHik(lUserId);
//            cameraOperateUtils.cleanHik();
        } else {
            //TODO 暂时还没测试通过大华的云台操作
            //退出，清理缓存
            cameraOperateUtils.logoutDh(lUserId);
//            cameraOperateUtils.cleanDh();
        }
        return hotDeviceAlarmInfo;
    }
}
