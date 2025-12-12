package org.camera.cameratool.util;

import com.sun.jna.Memory;
import com.sun.jna.Pointer;
import lombok.extern.slf4j.Slf4j;
import org.camera.cameratool.dahua.DhCameraSDK;
import org.camera.cameratool.exception.ServiceException;
import org.camera.cameratool.hkvision.HCNetSDK;
import org.camera.cameratool.hkvision.HCNetSDKConfig;
import org.camera.cameratool.pojo.dto.*;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Date;
import java.util.concurrent.CountDownLatch;

/**
 * Author:drq
 * Title:
 * Description:
 */
@Slf4j
@Component
public class CameraOperateUtils {
    private final int normal = 0;
    private final int error = -47999;

    /**
     * linux需要在初始化前做一些操作
     * 目前没有操作过这些
     */
    public void initLinux() {
        //①与组件库不在一个包下，加载不到时
        if ("1".equals(HCNetSDKConfig.flag1)) {
            HCNetSDK.NET_DVR_LOCAL_SDK_PATH struComPath = new HCNetSDK.NET_DVR_LOCAL_SDK_PATH();
            System.arraycopy(HCNetSDKConfig.pathTwo.getBytes(), 0, struComPath.sPath, 0, HCNetSDKConfig.pathTwo.length());
            struComPath.write();
            boolean b = HCNetSDK.INSTANCE.NET_DVR_SetSDKInitCfg(2, struComPath.getPointer());
        }
        //②两个开源库无法加载时
        // 设置libcrypto.so所在路径
        if ("1".equals(HCNetSDKConfig.flag2)) {
            HCNetSDK.BYTE_ARRAY byteArray = new HCNetSDK.BYTE_ARRAY();
            System.arraycopy(HCNetSDKConfig.pathThree.getBytes(), 0, byteArray.path, 0, HCNetSDKConfig.pathThree.length());
            byteArray.write();
            HCNetSDK.INSTANCE.NET_DVR_SetSDKInitCfg(3, byteArray.getPointer());
        }
        // 设置libssl.so所在路径
        if ("1".equals(HCNetSDKConfig.flag3)) {
            HCNetSDK.BYTE_ARRAY ptrByteArray2 = new HCNetSDK.BYTE_ARRAY();
            System.arraycopy(HCNetSDKConfig.pathFour.getBytes(), 0, ptrByteArray2.path, 0, HCNetSDKConfig.pathFour.length());
            ptrByteArray2.write();
            HCNetSDK.INSTANCE.NET_DVR_SetSDKInitCfg(4, ptrByteArray2.getPointer());
        }
    }

    /**
     * 初始化海康威视SDK
     */
    public void initHik() {
        boolean net_dvr_init = HCNetSDK.INSTANCE.NET_DVR_Init();
        if (!net_dvr_init) {
            log.error("海康威视摄像头初始化net_dvr_init出错：{}", HCNetSDK.INSTANCE.NET_DVR_GetLastError());
            throw new ServiceException("海康威视摄像头初始化net_dvr_init出错");
        } else {
            log.info("摄像头初始化成功");
        }
    }

    /**
     * 初始化大华SDK
     */
    public void initDh() {
        boolean dh_clientInit = DhCameraSDK.INSTANCE.CLIENT_Init(null, null);
        if (!dh_clientInit) {
            log.error("大华摄像头初始化dh_clientInit出错：{}", DhCameraSDK.INSTANCE.CLIENT_GetLastError());
            throw new ServiceException("大华摄像头初始化dh_clientInit出错");
        }
    }

    /**
     * 登录摄像设备(海康)
     *
     * @param camera
     * @return
     */
    public int loginHik(CameraDto camera) {
        HCNetSDK.NET_DVR_DEVICEINFO_V30 deviceInfo = new HCNetSDK.NET_DVR_DEVICEINFO_V30();
        int lUserId = HCNetSDK.INSTANCE.NET_DVR_Login_V30(camera.getCameraIp(), camera.getCameraPort(),
                camera.getCameraUsername(), camera.getCameraPassword(), deviceInfo);
        if (lUserId < 0) {
            log.error("海康威视摄像头登录_NET_DVR_Login_V30_出错：{}", HCNetSDK.INSTANCE.NET_DVR_GetLastError());
            throw new ServiceException("海康威视摄像头登录_NET_DVR_Login_V30_出错");
        } else {
            log.info("摄像头登录成功，lUserId = {}", lUserId);
        }
        return lUserId;
    }

    /**
     * 预置点操作（海康）
     *
     * @param cameraPresetDto
     */
    public void presetOperateHik(CameraPresetDto cameraPresetDto) {
        boolean net_dvr_ptzPreset_other = HCNetSDK.INSTANCE.NET_DVR_PTZPreset_Other
                (cameraPresetDto.getLUserId(), cameraPresetDto.getChannel(), cameraPresetDto.getCommand(), cameraPresetDto.getPreset());
        if (!net_dvr_ptzPreset_other) {
            log.error("海康威视预置点操作_NET_DVR_PTZPreset_Other_出错：{}", HCNetSDK.INSTANCE.NET_DVR_GetLastError());
            throw new ServiceException("海康威视预置点操作_NET_DVR_PTZPreset_Other_出错");
        } else {
            log.info("presetOperateHik()预置点操作成功");
        }
    }

    /**
     * 云台控制
     *
     * @param cameraPTZControlDto
     */
    public void ptzControlHik(CameraPTZControlDto cameraPTZControlDto) {
        boolean net_dvr_ptzControlWithSpeed_other =
                HCNetSDK.INSTANCE.NET_DVR_PTZControlWithSpeed_Other
                        (cameraPTZControlDto.getLUserId(), cameraPTZControlDto.getChannel(), cameraPTZControlDto.getCommand(), 0, 1);
        if (!net_dvr_ptzControlWithSpeed_other) {
            log.error("控制云台运转_NET_DVR_PTZControlWithSpeed_Other_出错：" + HCNetSDK.INSTANCE.NET_DVR_GetLastError());
        }
        try {
            Thread.sleep(2000); //转动持续1s
        } catch (InterruptedException e) {
        }
        //停止
        boolean net_dvr_ptzControlWithSpeed_other1 = HCNetSDK.INSTANCE.NET_DVR_PTZControlWithSpeed_Other
                (cameraPTZControlDto.getLUserId(), cameraPTZControlDto.getChannel(), cameraPTZControlDto.getCommand(), 1, 1);
        if (!net_dvr_ptzControlWithSpeed_other1) {
            log.error("控制云台停止_NET_DVR_PTZControlWithSpeed_Other_出错：" + HCNetSDK.INSTANCE.NET_DVR_GetLastError());
        } else {
            log.info("ptzControlHik()控制云台成功");
        }
    }

    public void takePhotoHik(CameraTakePhotoDto cameraTakePhotoDto) {
        //线程睡眠5s
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
        }
        // 指定要创建的文件路径
        String path = cameraTakePhotoDto.getPath();
        //拍照
        HCNetSDK.NET_DVR_JPEGPARA jpegPara = new HCNetSDK.NET_DVR_JPEGPARA();
        jpegPara.wPicSize = 0;
        jpegPara.wPicQuality = 50;
        boolean capturePicture = HCNetSDK.INSTANCE.NET_DVR_CaptureJPEGPicture
                (cameraTakePhotoDto.getLUserId(), cameraTakePhotoDto.getChannel(), jpegPara, path);
        log.info("拍照存储路径：" + path);
        if (!capturePicture) {
            log.error("拍照_NET_DVR_CaptureJPEGPicture_出错：" + HCNetSDK.INSTANCE.NET_DVR_GetLastError());
        } else {
            log.info("takePhotoHik()抓拍成功");
        }
    }

    public void takePhotoDh(CameraTakePhotoDto cameraTakePhotoDto) {
        //线程睡眠5s
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
        }

        CountDownLatch latch = new CountDownLatch(1);
        // 设置抓图回调
        DhCameraSDK.fSnapRev snapCallback = (lLoginID, pBuf, RevLen, EncodeType, CmdSerial, dwUser) -> {
            System.out.println("回调函数被调用");
            byte[] data = pBuf.getByteArray(0, RevLen);
            try (FileOutputStream fos = new FileOutputStream(cameraTakePhotoDto.getPath())) {
                fos.write(data);
                log.info("图片已保存到：{}", cameraTakePhotoDto.getPath());
                latch.countDown();  // 回调函数完成后减少计数
            } catch (IOException e) {
                e.printStackTrace();
                latch.countDown();  // 回调函数完成后减少计数
            }

        };
        DhCameraSDK.INSTANCE.CLIENT_SetSnapRevCallBack(snapCallback, null);
        System.out.println("回调函数设置错误码：" + convertErrorCode(DhCameraSDK.INSTANCE.CLIENT_GetLastError()));

        //单个摄像头，通道号为 0 才能拍照。
        DhCameraSDK.SNAP_PARAMS snapParams = new DhCameraSDK.SNAP_PARAMS(cameraTakePhotoDto.getChannel(), 2, 1, 0, 0, 0, 0);

        boolean capturePicture = DhCameraSDK.INSTANCE.CLIENT_SnapPictureEx(cameraTakePhotoDto.getLUserId(), snapParams, 0);
        if (!capturePicture) {
            System.out.println("抓图错误码：" + convertErrorCode(DhCameraSDK.INSTANCE.CLIENT_GetLastError()));
        } else {
            System.out.println("成功抓图");
        }

        try {
            latch.await();  // 等待回调函数完成
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    /**
     * 初始化以及登录摄像头（所有前置操作都一样）
     * todo 要测试一下，linux和 windows 调用，除了包的加载，有什么不一样
     *
     * @param camera
     * @return
     */
    public long initStep(CameraDto camera) {
        if (camera == null) {
            log.error("摄像头对象camera不能为null");
            return 0;
        }
        if ("2".equals(HCNetSDKConfig.operatingSystem)) { //linux
            switch (camera.getBrand()) {
                //海康威视
                case 1:
                    //在linux下需要
                    initLinux();
                    //初始化SDK
                    initHik();
                    //登录摄像设备
                    return loginHik(camera);
                //大华
                case 2:
                    initDh();
                    return loginDh(camera);
            }
        } else if ("1".equals(HCNetSDKConfig.operatingSystem)) {//win
            switch (camera.getBrand()) {
                //海康威视
                case 1:
                    //初始化SDK
                    initHik();
                    //登录摄像设备
                    return loginHik(camera);
                //大华
                case 2:
                    initDh();
                    return loginDh(camera);
            }
        }
        return error;
    }

    /**
     * 登录摄像头（大华）
     *
     * @param camera
     * @return
     */
    private long loginDh(CameraDto camera) {
        long loginEx2 = DhCameraSDK.INSTANCE.CLIENT_LoginEx2(camera.getCameraIp(), camera.getCameraPort(), camera.getCameraUsername(),
                camera.getCameraPassword(), 0, 0, null, 0);
        if (loginEx2 == 0) {
            log.error("大华摄像头登录clientGetLastError出错：{}", DhCameraSDK.INSTANCE.CLIENT_GetLastError());
            throw new ServiceException("大华摄像头登录clientGetLastError出错");
        }
        return loginEx2;
    }

    /**
     * @Description: 释放内存
     * @Param: []
     * @return: boolean
     * @Author: AG的狗腿子
     * @Date: 2023/10/24
     */
    public void cleanHik() {
        boolean cleanup = HCNetSDK.INSTANCE.NET_DVR_Cleanup();
        if (!cleanup) {
            log.error("释放空间失败，错误代码：" + HCNetSDK.INSTANCE.NET_DVR_GetLastError());
        }
    }

    public void cleanDh() {
        DhCameraSDK.INSTANCE.CLIENT_Cleanup();
        log.info("释放空间:错误代码：{}", HCNetSDK.INSTANCE.NET_DVR_GetLastError());
    }

    public void logoutHik(long lUserId) {
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
        }
        boolean logout = HCNetSDK.INSTANCE.NET_DVR_Logout(lUserId);
        if (!logout) {
            log.error("登出失败，错误代码：" + HCNetSDK.INSTANCE.NET_DVR_GetLastError());
        }
    }

    public void logoutDh(long lUserId) {
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            log.error("退出延迟失败,{}", DhCameraSDK.INSTANCE.CLIENT_GetLastError());
        }
        boolean logout = DhCameraSDK.INSTANCE.CLIENT_Logout(lUserId);
        if (!logout) {
            log.error("登出失败，错误代码：{}", DhCameraSDK.INSTANCE.CLIENT_GetLastError());
        }
    }

    public String convertErrorCode(long errorCode) {
        long x = errorCode & 0x7FFFFFFF;
        return "_EC(" + x + ")";
    }

    //========================================================热成像事件报警========================================================
    // 持续运行以接收报警
    final Object monitor = new Object();
    /**
     * 是否报警
     */
    int ifAlarm = -1;

    /**
     * 热成像获取报警
     *
     * @param cameraHotDeviceDto
     * @return
     */
    public int getHotDeviceAlarmInfo(CameraHotDeviceDto cameraHotDeviceDto) {
        ifAlarm = -1;
        CameraDto cameraDto = cameraHotDeviceDto.getCameraDto();
        HCNetSDK sdk = HCNetSDK.INSTANCE;

        // 设置报警回调函数
        AlarmCallback callback = new AlarmCallback();
        sdk.NET_DVR_SetDVRMessageCallBack_V50(0, callback, null);


        // 设置报警布防通道
        Pointer setupParam = new Memory(1024); // 伪代码
        int alarmHandle = sdk.NET_DVR_SetupAlarmChan_V41(cameraHotDeviceDto.getLUserId(), setupParam);
        if (alarmHandle < 0) {
            System.out.println("布防失败");
            sdk.NET_DVR_Logout(cameraHotDeviceDto.getLUserId());
            return -2;
        }


        System.out.println("等待报警信息...");
        synchronized (monitor) {
            try {
                monitor.wait(10000); // 主线程等待6秒，期间可接收回调函数报警信息
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        // 撤防
        sdk.NET_DVR_CloseAlarmChan_V30(alarmHandle);
        return ifAlarm;
    }

    // 实现报警回调函数，接收报警数据
    public class AlarmCallback implements HCNetSDK.MSGCallBack {
        @Override
        public void invoke(int lCommand, Pointer pAlarmer, Pointer pAlarmInfo, int dwBufLen, Pointer pUser) {
            System.out.println("进入了回调函数");
            HCNetSDK.NET_DVR_THERMOMETRY_ALARM alarmInfo = new HCNetSDK.NET_DVR_THERMOMETRY_ALARM(pAlarmInfo);
            alarmInfo.read();
            if (pAlarmInfo == null) {
                ifAlarm = -1;//不报警
            } else if (alarmInfo.byAlarmLevel == 0) {
                ifAlarm = 0;//预警
            } else if (alarmInfo.byAlarmLevel == 1) {
                ifAlarm = 1;//报警
            }
//            printThermometryAlarmInfo(alarmInfo);
            System.out.println("温度报警信息：报警等级：" + alarmInfo.byAlarmLevel + " 报警类型：" + alarmInfo.byAlarmType);
//			System.out.println("通道号："+alarmInfo.dwChannel);
            System.out.println("命令类型：" + lCommand);
            // 处理温度报警信息
//			if (lCommand == Constants.COMM_THERMOMETRY_ALARM) {
//
//			} else if (lCommand == Constants.COMM_THERMOMETRY_DIFF_ALARM) {
//				NET_DVR_THERMOMETRY_DIFF_ALARM diffAlarmInfo = new NET_DVR_THERMOMETRY_DIFF_ALARM(pAlarmInfo);
//				diffAlarmInfo.read();
//				System.out.println("温差报警信息：当前温差：" + diffAlarmInfo.fCurTemperatureDiff + " 规则温差：" + diffAlarmInfo.fRuleTemperatureDiff);
//			}
        }
    }

//========================================================普通事件报警========================================================

    // 持续运行以接收报警
    final Object monitor2 = new Object();
    /**
     * 是否报警
     */
    int ifAlarm2 = -1;

    /**
     * 通用获取报警
     *
     * @param cameraHotDeviceDto
     * @return
     */
    public int getDeviceAlarmInfo(CameraHotDeviceDto cameraHotDeviceDto) {
        ifAlarm2 = -1;
        CameraDto cameraDto = cameraHotDeviceDto.getCameraDto();
        HCNetSDK sdk = HCNetSDK.INSTANCE;

        // 设置报警回调函数
        AlarmCallback2 callback = new AlarmCallback2();
        sdk.NET_DVR_SetDVRMessageCallBack_V50(0, callback, null);


        // 设置报警布防通道
        Pointer setupParam = new Memory(1024); // 伪代码
        int alarmHandle = sdk.NET_DVR_SetupAlarmChan_V41(cameraHotDeviceDto.getLUserId(), setupParam);
        if (alarmHandle < 0) {
            System.out.println("布防失败");
            sdk.NET_DVR_Logout(cameraHotDeviceDto.getLUserId());
            return -2;
        }


        System.out.println("等待报警信息...");
        synchronized (monitor2) {
            try {
                monitor2.wait(5000); // 主线程等待6秒，期间可接收回调函数报警信息
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        // 撤防
        sdk.NET_DVR_CloseAlarmChan_V30(alarmHandle);
        return ifAlarm2;
    }

    // 实现报警回调函数，接收报警数据
    public class AlarmCallback2 implements HCNetSDK.MSGCallBack {
        @Override
        public void invoke(int lCommand, Pointer pAlarmer, Pointer pAlarmInfo, int dwBufLen, Pointer pUser) {
            ifAlarm2 = 1;
            System.out.println("进入了回调函数");
            HCNetSDK.NET_DVR_ALARMINFO_V30 alarmInfo = new HCNetSDK.NET_DVR_ALARMINFO_V30(pAlarmInfo);
            alarmInfo.read();
            System.out.println("dwAlarmType 报警类型：" + alarmInfo.dwAlarmType);
            System.out.println("dwAlarmInputNumber  报警输入端口：" + alarmInfo.dwAlarmInputNumber);
            System.out.println("byAlarmOutputNumber   报警输出端口：" + Arrays.toString(alarmInfo.byAlarmOutputNumber));
            System.out.println("byAlarmRelateChannel   触发的录像通道：" + Arrays.toString(alarmInfo.byAlarmRelateChannel));
            System.out.println("byChannel 发生报警的通道：" + Arrays.toString(alarmInfo.byChannel));
        }
    }

    //========================================================智能smart事件报警========================================================
// 持续运行以接收报警
    final Object monitorSmart = new Object();
    /**
     * 是否报警
     */
    static int ifAlarmSmart = -1;

    /**
     * smart智能事件获取报警
     *
     * @param cameraHotDeviceDto
     * @return
     */
    public int getDeviceSmartAlarmInfo(CameraHotDeviceDto cameraHotDeviceDto) {
        ifAlarmSmart = -1;
        CameraDto cameraDto = cameraHotDeviceDto.getCameraDto();
        HCNetSDK sdk = HCNetSDK.INSTANCE;

        // 设置报警回调函数
        AlarmCallbackSmart callback = new AlarmCallbackSmart();
        sdk.NET_DVR_SetDVRMessageCallBack_V50(0, callback, null);


        // 设置报警布防通道
        Pointer setupParam = new Memory(1024); // 伪代码
        int alarmHandle = sdk.NET_DVR_SetupAlarmChan_V41(cameraHotDeviceDto.getLUserId(), setupParam);
        if (alarmHandle < 0) {
            System.out.println("布防失败");
            sdk.NET_DVR_Logout(cameraHotDeviceDto.getLUserId());
            return -2;
        }


        System.out.println("报警布防：" + new Date());
        synchronized (monitorSmart) {
            try {
                monitorSmart.wait(60000); // 主线程等待6秒，期间可接收回调函数报警信息
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        System.out.println("报警撤防：" + new Date());

        // 撤防
        sdk.NET_DVR_CloseAlarmChan_V30(alarmHandle);
        return ifAlarmSmart;
    }

    // 实现报警回调函数，接收报警数据
    public static class AlarmCallbackSmart implements HCNetSDK.MSGCallBack {
        @Override
        public void invoke(int lCommand, Pointer pAlarmer, Pointer pAlarmInfo, int dwBufLen, Pointer pUser) {
            System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>报警时间：" + new Date());
            System.out.println("lCommand:" + lCommand);
//            HCNetSDK.NET_VCA_RULE_ALARM netVcaRuleAlarm = new HCNetSDK.NET_VCA_RULE_ALARM(pAlarmInfo);
//            HCNetSDK.NET_VCA_RULE_INFO struRuleInfo = netVcaRuleAlarm.struRuleInfo;
//            byte[] byRuleName = struRuleInfo.byRuleName;
//            int dwEventType = struRuleInfo.dwEventType;
//            System.out.println("dwEventType:" + dwEventType);
//            System.out.println("byRuleName:" + Arrays.toString(byRuleName));
//            System.out.println("NET_VCA_RULE_ALARM:" + netVcaRuleAlarm);
//            System.out.println("NET_VCA_RULE_INFO:" + struRuleInfo);
            System.out.println("<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<");
            ifAlarmSmart = 1;
        }
    }

}
