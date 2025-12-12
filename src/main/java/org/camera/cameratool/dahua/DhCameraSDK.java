package org.camera.cameratool.dahua;

import com.sun.jna.*;
import com.sun.jna.ptr.IntByReference;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.camera.cameratool.pojo.dto.Camera;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CountDownLatch;

public interface DhCameraSDK extends Library {
    //	String libraryPath = "E:\\step-two-project\\cr\\cameratool\\lib\\windowslib\\dh\\dhnetsdk.dll";
    String libraryPath = DhConfig.libraryPath;

    // 加载库文件
    DhCameraSDK INSTANCE = Native.load(libraryPath, DhCameraSDK.class);

    // 初始化SDK
    boolean CLIENT_Init(DisConnectCallback cbDisConnect, Pointer dwUser);

    // 清理SDK
    void CLIENT_Cleanup();

    // 登录设备
    long CLIENT_LoginEx2(String pchDVRIP, long wDVRPort, String pchUserName, String pchassword,
                         int emSpecCap, int pCapParam, NET_DEVICEINFO_Ex lpDeviceInfo, int error);

    // 注销设备
    boolean CLIENT_Logout(long lLoginID);

    // 抓取图片
    boolean CLIENT_CapturePicture(long hPlayHandle, String pchPicFileName, String eFormat);

    boolean CLIENT_SnapPictureEx(long lLoginID, SNAP_PARAMS snapParams, int reserved);

    // 开始实时播放
    long CLIENT_RealPlayEx(long lLoginID, int nChannelID, Pointer hWnd, String rType);

    long CLIENT_RealPlay(long lLoginID, int nChannelID, Pointer hWnd);

    // 抓取图片并保存到文件
    boolean CLIENT_SnapPictureToFile(long lLoginID, SNAP_PARAMS snapParams, String filePath);

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    class SNAP_PARAMS extends Structure {
        public int Channel;
        //画质；1~6
        public int Quality;
        //画面大小；0：QCIF，1：CIF，2：D1
        public int ImageSize;
        //抓图模式；0：表示请求一帧，1：表示定时发送请求，2：表示连续请求
        public int mode;
        //时间单位秒；若mode=1表示定时发送请求时，此时间有效
        public int InterSnap;
        //请求序列号
        public int CmdSerial;
        //保留
        public int Reserved;

        @Override
        protected List<String> getFieldOrder() {
            return Arrays.asList("Channel", "Quality", "ImageSize", "mode", "InterSnap",
                    "CmdSerial", "Reserved");
        }

//        public static class ByReference extends SNAP_PARAMS implements Structure.ByReference {
//        }
    }

    // 断开连接回调接口
    interface DisConnectCallback extends Callback {
        void invoke(long dwUser);
    }

    // 设备信息结构体
    class NET_DEVICEINFO_Ex extends Structure {
        public byte[] sSerialNumber = new byte[48];
        public byte byAlarmInPortNum;
        public byte byAlarmOutPortNum;
        public byte byDiskNum;
        public byte byDVRType;
        public byte byChanNum;
        public byte byStartChan;
        public byte byAudioChanNum;
        public byte byIPChanNum;
        public byte[] byRes = new byte[24];

        @Override
        protected List<String> getFieldOrder() {
            return Arrays.asList("sSerialNumber", "byAlarmInPortNum", "byAlarmOutPortNum", "byDiskNum", "byDVRType",
                    "byChanNum", "byStartChan", "byAudioChanNum", "byIPChanNum", "byRes");
        }
    }

    // 获取最后一个错误码
    long CLIENT_GetLastError();

    interface fSnapRev extends Callback {
        void invoke(long lLoginID, Pointer pBuf, int RevLen, int EncodeType, int CmdSerial, Pointer dwUser);
    }

    void CLIENT_SetSnapRevCallBack(fSnapRev cbSnapRev, Pointer dwUser);

    long CLIENT_LoginWithHighLevelSecurity(NET_IN_LOGIN_WITH_HIGHLEVEL_SECURITY pInParam, NET_OUT_LOGIN_WITH_HIGHLEVEL_SECURITY pOutParam);

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    class NET_IN_LOGIN_WITH_HIGHLEVEL_SECURITY extends Structure {
        public int dwSize;
        public String szIP;
        public long nPort;
        public String szUserName;
        public String szPassword;
        public int emSpecCap;
        public Pointer pCapParam;

        @Override
        protected List<String> getFieldOrder() {
            return Arrays.asList("dwSize", "szIP", "nPort", "szUserName", "szPassword", "emSpecCap", "pCapParam");
        }
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    class NET_OUT_LOGIN_WITH_HIGHLEVEL_SECURITY extends Structure {
        public int dwSize;
        public long lLoginID;
        public NET_DEVICEINFO_Ex stuDeviceInfo;

        @Override
        protected List<String> getFieldOrder() {
            return Arrays.asList("dwSize", "lLoginID", "stuDeviceInfo");
        }
    }

    public static long loginWithHighLevelSecurity(Camera camera) {
        NET_IN_LOGIN_WITH_HIGHLEVEL_SECURITY inParam = new NET_IN_LOGIN_WITH_HIGHLEVEL_SECURITY();
        inParam.dwSize = inParam.size();
        inParam.szIP = camera.getCameraIp();
        inParam.nPort = camera.getCameraPort();
        inParam.szUserName = camera.getCameraUsername();
        inParam.szPassword = camera.getCameraPassword();
        inParam.emSpecCap = 0;  // or other specific capabilities
        inParam.pCapParam = null;  // or specific parameters

        NET_OUT_LOGIN_WITH_HIGHLEVEL_SECURITY outParam = new NET_OUT_LOGIN_WITH_HIGHLEVEL_SECURITY();
        outParam.dwSize = outParam.size();

        long loginHandle = DhCameraSDK.INSTANCE.CLIENT_LoginWithHighLevelSecurity(inParam, outParam);

        if (loginHandle == 0) {
            long loginError = DhCameraSDK.INSTANCE.CLIENT_GetLastError();
            throw new RuntimeException("登录错误，错误码：" + convertErrorCode(loginError));
        } else {
            System.out.println("登录成功, 登录句柄：" + outParam.lLoginID);
            return outParam.lLoginID;
        }
    }

//
//    public static void main(String[] args) {
//        System.out.println(libraryPath);
//        DhCameraSDK.INSTANCE.CLIENT_Init(null, null);
//        long i = DhCameraSDK.INSTANCE.CLIENT_GetLastError();
//        System.out.println("初始化错误码：" + i);
//
//
//        NET_DEVICEINFO_Ex net_deviceinfo_ex = new NET_DEVICEINFO_Ex();
//        Camera camera = new Camera("173.15.46.240", (short)37777, "admin", "admin123");
//        long l = DhCameraSDK.INSTANCE.CLIENT_LoginEx2(camera.getCameraIp(), camera.getCameraPort(),
//                camera.getCameraUsername(), camera.getCameraPassword(), 0, 0, net_deviceinfo_ex, 0);
//        if (l == 0) {
//            long loginError = DhCameraSDK.INSTANCE.CLIENT_GetLastError();
//            throw new RuntimeException("登录错误，错误码：" + convertErrorCode(loginError));
//        } else {
//            System.out.println("登录成功, 登录句柄：" + l);
//        }
//        CountDownLatch latch = new CountDownLatch(1);
//        // 设置抓图回调
//        DhCameraSDK.fSnapRev snapCallback = (lLoginID, pBuf, RevLen, EncodeType, CmdSerial, dwUser) -> {
//            System.out.println("回调函数被调用");
//            byte[] data = pBuf.getByteArray(0, RevLen);
//            try (FileOutputStream fos = new FileOutputStream("snapshot.jpg")) {
//                fos.write(data);
//                System.out.println("图片已保存到 snapshot.jpg");
//                latch.countDown();  // 回调函数完成后减少计数
//            } catch (IOException e) {
//                e.printStackTrace();
//                latch.countDown();  // 回调函数完成后减少计数
//            }
//
//        };
//        DhCameraSDK.INSTANCE.CLIENT_SetSnapRevCallBack(snapCallback, net_deviceinfo_ex.getPointer());
//        System.out.println("回调函数设置错误码：" + convertErrorCode(DhCameraSDK.INSTANCE.CLIENT_GetLastError()));
//
//
//        SNAP_PARAMS snapParams = new SNAP_PARAMS(0, 2, 1, 0, 0, 0, 0);
//
//        boolean capturePicture = DhCameraSDK.INSTANCE.CLIENT_SnapPictureEx(l, snapParams, 0);
//        if (!capturePicture) {
//            System.out.println("抓图错误码：" + convertErrorCode(DhCameraSDK.INSTANCE.CLIENT_GetLastError()));
//        } else {
//            System.out.println("成功抓图");
//        }
//
//        try {
//            latch.await();  // 等待回调函数完成
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//
//
////        long l = loginWithHighLevelSecurity(camera);
//
//
//////        String filePath = "cameratool\\snapshot.jpeg";
//////        String currentPath = System.getProperty("user.dir");
////////        Path filePath = Paths.get(currentPath, "test.jpg");
//////        String filePath = "test.jpg";
//////        System.out.println("文件路径: " + "test.jpg");
//////        //抓图 todo 思考是怎么传，是直接像原来一样调用许总远程接口上传图片服务器，还是直接存到那个文件夹里去？
////        SNAP_PARAMS.ByReference snapParams = new SNAP_PARAMS.ByReference();
////        snapParams.Channel = 1;
////        snapParams.Quality = 2;
////        snapParams.ImageSize = 1;
////        snapParams.mode = 0;
////        snapParams.InterSnap = 0;
////        snapParams.CmdSerial = 0;
////        snapParams.Reserved = 0;
////        boolean capturePicture = DhCameraSDK.INSTANCE.CLIENT_SnapPictureToFile(l, snapParams, filePath);
////        boolean capturePicture = DhCameraSDK.INSTANCE.CLIENT_SnapPictureToFile(l, snapParams, "testdh.jpg");
//
//
//
//        DhCameraSDK.INSTANCE.CLIENT_Logout(l);
//        System.out.println(convertErrorCode(DhCameraSDK.INSTANCE.CLIENT_GetLastError()));
//        DhCameraSDK.INSTANCE.CLIENT_Cleanup();
//        System.out.println(DhCameraSDK.INSTANCE.CLIENT_GetLastError());
//    }

    public static long loginEx2(Camera camera) {
        NET_DEVICEINFO_Ex deviceInfo = new NET_DEVICEINFO_Ex();
        IntByReference error = new IntByReference();
        long loginHandle = DhCameraSDK.INSTANCE.CLIENT_LoginEx2(camera.getCameraIp(), camera.getCameraPort(),
                camera.getCameraUsername(), camera.getCameraPassword(), 0, 0, deviceInfo, 0);

        if (loginHandle == 0) {
            long loginError = DhCameraSDK.INSTANCE.CLIENT_GetLastError();
            throw new RuntimeException("登录错误，错误码：" + convertErrorCode(loginError));
        } else {
            System.out.println("登录成功, 登录句柄：" + loginHandle);
            return loginHandle;
        }
    }

    public static String convertErrorCode(long errorCode) {
        long x = errorCode & 0x7FFFFFFF;
        return "_EC(" + x + ")";
    }
}
