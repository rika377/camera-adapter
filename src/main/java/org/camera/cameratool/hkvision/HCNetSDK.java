package org.camera.cameratool.hkvision;

import com.sun.jna.*;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.win32.StdCallLibrary;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

public interface HCNetSDK extends Library {

//	String libraryPath = "E:\\step-two-project\\cr\\cameratool\\lib\\windowslib\\hk\\HCNetSDK.dll";
	//	String libraryPath = System.getProperty("user.dir") + "/blade-service/blade-camera/lib/HCNetSDK.dll"; //linux
// 获取当前工作目录的绝对路径
//	Path currentPath = Paths.get("").toAbsolutePath();
	//	String libraryPath = currentPath + "/blade-service/blade-camera/lib/HCNetSDK.dll"; //linux
	//Windows
//	HCNetSDK INSTANCE = (HCNetSDK) Native.loadLibrary(HCNetSDKConfig.libraryPath, HCNetSDK.class);
	//Linux
//	HCNetSDK INSTANCE = (HCNetSDK) Native.loadLibrary(libraryPath, HCNetSDK.class);
	HCNetSDK INSTANCE = (HCNetSDK) Native.loadLibrary(HCNetSDKConfig.libraryPath, HCNetSDK.class);
//	HCNetSDK INSTANCE = System.loadLibrary(HCNetSDKConfig.libraryPath);

	//Linux
	public boolean NET_DVR_SetSDKInitCfg(int enumType, Pointer lpInBuff);//TODO 数据类型可能会有问题

	public class NET_DVR_LOCAL_SDK_PATH extends Structure{

		// 字符串表示路径
		public byte[] sPath= new byte[256];

		// 字节数组表示byRes
		public byte[] byRes = new byte[128];

		// 构造函数
		public NET_DVR_LOCAL_SDK_PATH() {
//			sPath = sPath = new String(new byte[4096]);
		}
	}
	public class BYTE_ARRAY extends Structure{
		public byte[] path = new byte[256];
	}


	// 设备信息结构体
	class NET_DVR_DEVICEINFO_V30 extends HIKSDKStructure {
		public byte[] sSerialNumber = new byte[48]; // 设备序列号
		public byte byAlarmInPortNum;
		public byte byAlarmOutPortNum;
		public byte byDiskNum;
		public byte byDVRType;
		public byte byChanNum;
		public byte byStartChan;
		public byte byAudioChanNum;
		public byte byIPChanNum;
		public byte[] byRes1 = new byte[24];
		public byte byDVRVersion;
		public byte byRes2;
		public short wDVRNameLen;
		public byte[] sDVRName = new byte[128]; // 设备名称
		public int byRes3;
	}

	// 获取错误码
	int NET_DVR_GetLastError();


	// 设备配置命令
	int NET_DVR_GET_DEVICECFG = 100; // 示例值，根据实际命令进行修改
	int SET_PRESET = 8; //设置预置点
	int CLE_PRESET = 9; //清除预置点
	int GOTO_PRESET = 39; //转到预置点

	boolean NET_DVR_CaptureJPEGPicture(long lUserID, long lChannel, NET_DVR_JPEGPARA lpJpegPara, String sPicFileName);

	public static class NET_DVR_JPEGPARA extends Structure {
		public int wPicSize;
		public int wPicQuality;

		@Override
		protected List<String> getFieldOrder() {
			return Arrays.asList("wPicSize", "wPicQuality");
		}
	}

	// 登录设备
	int NET_DVR_Login_V30(String sDVRIP, long wDVRPort, String sUserName, String sPassword, NET_DVR_DEVICEINFO_V30 lpDeviceInfo);

	// 控制摄像头转动
	boolean NET_DVR_PTZControlWithSpeed(long lRealHandle, int dwPTZCommand, int dwStop, int dwSpeed);

	boolean NET_DVR_PTZControlWithSpeed_Other(long lUserId, long lChannel, int dwPTZCommand, int dwStop, int dwSpeed);

	// 记录预设点位
	boolean NET_DVR_PTZPreset(long lRealHandle, int dwPTZPresetCmd, int dwPresetIndex);

	// 调用摄像头拍照
	boolean NET_DVR_CapturePictureBlock(Long lRealHandle, String jpegName, int dwTimeOut);

	// 获取摄像头当前位置
	boolean NET_DVR_PTZGetPos(int lRealHandle, IntByReference lpPanPos, IntByReference lpTiltPos, IntByReference lpZoomPos);


	// 注销设备
	boolean NET_DVR_Logout(long lUserID);

	//初始化
	boolean NET_DVR_Init();

	//清空
	boolean NET_DVR_Cleanup();

	//开启实时预览
	long NET_DVR_RealPlay_V40(long lUserID, NET_DVR_PREVIEWINFO lpPreviewInfo, REALDATACALLBACK fRealDataCallBack_V30, Pointer pUser);

	//获取预览时的播放窗口句柄
	int NET_DVR_GetRealPlayerIndex(long lRealHandle);

	boolean NET_DVR_StopRealPlay(long lRealHandle);

	boolean NET_DVR_SetLogToFile(int nLogLevel, String strLogDir,
								 boolean bAutoDel);

	class NET_DVR_PREVIEWINFO extends Structure {
		public int lChannel;
		public int dwStreamType;
		public int dwLinkMode;
		public Pointer hPlayWnd;
		public boolean bBlocked;
		public boolean bPassbackRecord;
		public byte byPreviewMode;
		public byte[] byStreamID = new byte[128];
		public byte byProtoType;
		public byte byRes1;
		public byte byVideoCodingType;
		public int dwDisplayBufNum;
		public byte byNPQMode;
		public byte byRecvMetaData;
		public byte byDataType;
		public byte[] byRes = new byte[213];
	}


	public interface LoginResultCallback {
		void onLoginResult(long lUserID, int dwResult, Pointer lpDeviceInfo, Pointer pUser);
	}


	interface REALDATACALLBACK extends Callback {
		void invoke(NativeLong lRealHandle, int dwDataType, Pointer pBuffer, int dwBufSize, Pointer pUser) throws IOException;
	}


	//领悟后的新代码

	/**
	 * 云台控制操作(不用启动图象预览)
	 *
	 * @param lUserID      用户句柄（NET_DVR_Login_V40等登录接口的返回值）
	 * @param lChannel     通道号
	 * @param dwPTZCommand 云台控制命令（具体映射值见设备网络SDK文档）
	 * @param dwStop       云台停止动作或开始动作：0－开始，1－停止
	 * @return
	 */
	boolean NET_DVR_PTZControl_Other(long lUserID, long lChannel, int dwPTZCommand, int dwStop);
//	boolean NET_DVR_PTZControlWithSpeed(long lRealHandle, int dwPTZCommand, int dwStop, int dwSpeed);

	boolean NET_DVR_PTZPreset_Other(long lUserID, long lChannel, int dwPTZPresetCmd, int dwPresetIndex);

	public class NET_DVR_USER_LOGIN_INFO extends Structure {
		//		public String sDeviceAddress = "173.25.143.12";
		public String sDeviceAddress = "173.15.142.34";
		public byte byUseTransport;
		public short wPort = 8000;
		public String sUserName = "admin";
		//		public String sPassword = "bz12345678";
		public String sPassword = "hik12345";
		public Pointer cbLoginResult;
		public Pointer pUser;
		public boolean bUseAsynLogin;
		public byte byProxyType;
		public byte byUseUTCTime;
		public byte byLoginMode;
		public byte byHttps;
		public long iProxyID;
		public long byVerifyMode;
		public byte[] byRes3 = new byte[119];
	}

	public class NET_DVR_DEVICEINFO_V40 extends Structure {
		public NET_DVR_DEVICEINFO_V30 struDeviceV30;
		public byte bySupportLock;
		public byte byRetryLoginTime;
		public byte byPasswordLevel;
		public byte byProxyType;
		public int dwSurplusLockTime;
		public byte byCharEncodeType;
		public byte bySupportDev5;
		public byte byLoginMode;
		public int byRes3;
		public int iResidualValidity;
		public byte byResidualValidity;
		public byte bySingleStartDTalkChan;
		public byte bySingleDTalkChanNums;
		public byte byPassWordResetLevel;
		public byte bySupportStreamEncrypt;
		public byte byMarketType;
		public byte[] byRes2 = new byte[238];
	}

	long NET_DVR_Login_V40(NET_DVR_USER_LOGIN_INFO pLoginInfo, NET_DVR_DEVICEINFO_V40 lpDeviceInfo);

	boolean NET_DVR_CapturePicture(long lRealHandle, String sPicFileName);


	/**
	 * ================================================下面方法为热成像产品==============================================================
	 */
	// 定义报警回调函数接口
	public interface MSGCallBack extends Callback {
		void invoke(int lCommand, Pointer pAlarmer, Pointer pAlarmInfo, int dwBufLen, Pointer pUser);
	}
	// 设置报警回调函数
	boolean NET_DVR_SetDVRMessageCallBack_V50(int iIndex, MSGCallBack fMessageCallBack, Pointer pUser);
	// 设置报警布防通道
	int NET_DVR_SetupAlarmChan_V41(long lUserID, Pointer lpSetupParam);
	// 撤防
	boolean NET_DVR_CloseAlarmChan_V30(int lAlarmHandle);

	// 实现报警回调函数，接收报警数据
	public static class AlarmCallback implements MSGCallBack {
		@Override
		public void invoke(int lCommand, Pointer pAlarmer, Pointer pAlarmInfo, int dwBufLen, Pointer pUser) {
			System.out.println("进入了回调函数");
			NET_DVR_THERMOMETRY_ALARM alarmInfo = new NET_DVR_THERMOMETRY_ALARM(pAlarmInfo);
			alarmInfo.read();
			printThermometryAlarmInfo(alarmInfo);
			System.out.println("温度报警信息：报警等级：" + alarmInfo.byAlarmLevel + " 报警类型：" + alarmInfo.byAlarmType);
//			System.out.println("通道号："+alarmInfo.dwChannel);
			System.out.println("命令类型："+lCommand);
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

	// 定义常量
	public static class Constants {
		public static final int COMM_THERMOMETRY_ALARM = 0x1000; // 温度报警类型
		public static final int COMM_THERMOMETRY_DIFF_ALARM = 0x1001; // 温差报警类型
	}

	public class NET_VCA_POINT extends Structure {
		public float fX;  // X坐标
		public float fY;  // Y坐标

		@Override
		protected List<String> getFieldOrder() {
			return Arrays.asList("fX", "fY");
		}

		public static class ByReference extends NET_VCA_POINT implements Structure.ByReference {}
		public static class ByValue extends NET_VCA_POINT implements Structure.ByValue {}
	}
	// 定义报警信息结构体
	public class NET_DVR_THERMOMETRY_ALARM extends Structure {

		public static class ByReference extends NET_DVR_THERMOMETRY_ALARM implements Structure.ByReference {
			public ByReference(Pointer p) {
				super(p);
			}
		}
		public static class ByValue extends NET_DVR_THERMOMETRY_ALARM implements Structure.ByValue {
			public ByValue(Pointer p) {
				super(p);
			}
		}

		public int dwSize;                    // 结构体大小
		public int dwChannel;                 // 通道号
		public byte byRuleID;                 // 规则ID
		public byte byThermometryUnit;        // 测温单位: 0- 摄氏度（℃），1- 华氏度（℉），2- 开尔文(K)
		public short wPresetNo;               // 预置点号
		public NET_VCA_POINT struPtzInfo;      // PTZ坐标信息
		public byte byAlarmLevel;             // 报警等级：0- 预警，1- 报警
		public byte byAlarmType;              // 报警类型：0- 最高温度，1- 最低温度，2- 平均温度
		public byte byAlarmRule;              // 报警规则：0- 大于，1- 小于
		public byte byRuleCalibType;          // 规则标定类型：0- 点，1- 框，2- 线
		public NET_VCA_POINT struPoint;       // 点测温坐标（当规则标定类型为点的时候生效）
		public NET_VCA_POINT struRegion;    // 区域测温坐标（当规则标定类型为框或线的时候生效）
		public float fRuleTemperature;        // 配置规则温度，精确到小数点后一位，取值范围：-40~1000
		public float fCurrTemperature;        // 当前温度，精确到小数点后一位，取值范围：-40~1000
		public int dwPicLen;                  // 可见光图片长度
		public int dwThermalPicLen;           // 热成像图片长度
		public int dwThermalInfoLen;          // 热成像附加信息长度
		public Pointer pPicBuff;              // 可见光图片指针，存放可见光图片数据，JPEG格式
		public Pointer pThermalPicBuff;       // 热成像图片指针，存放热成像图片数据，JPEG格式
		public Pointer pThermalInfoBuff;      // 热成像附加信息指针，存放热成像信息
		public NET_VCA_POINT struHighestPoint;// 线、框测温最高温度位置坐标（当规则标定类型为线、框的时候生效）
		public float fToleranceTemperature;   // 容差温度,精确到小数点后一位(-40-1000),（浮点数+100）
		public int dwAlertFilteringTime;      // 温度预警等待时间，单位：秒，范围为0-200秒，默认为0秒
		public int dwAlarmFilteringTime;      // 温度报警等待时间，单位：秒，范围为0-200秒，默认为0秒
		public int dwTemperatureSuddenChangeCycle; // 温度突变记录周期，单位：秒
		public float fTemperatureSuddenChangeValue; // 温度突变值,精确到小数点后一位(大于0)
		public byte byPicTransType;           // 图片数据传输方式：0-二进制，1-URL
		public byte[] byRes = new byte[39];   // 保留字节

		public NET_DVR_THERMOMETRY_ALARM(Pointer p) {
			super(p);
		}
		@Override
		protected List<String> getFieldOrder() {
			return Arrays.asList(
					"dwSize", "dwChannel", "byRuleID", "byThermometryUnit", "wPresetNo",
					"struPtzInfo", "byAlarmLevel", "byAlarmType", "byAlarmRule", "byRuleCalibType",
					"struPoint", "struRegion", "fRuleTemperature", "fCurrTemperature",
					"dwPicLen", "dwThermalPicLen", "dwThermalInfoLen", "pPicBuff", "pThermalPicBuff",
					"pThermalInfoBuff", "struHighestPoint", "fToleranceTemperature", "dwAlertFilteringTime",
					"dwAlarmFilteringTime", "dwTemperatureSuddenChangeCycle", "fTemperatureSuddenChangeValue",
					"byPicTransType", "byRes"
			);
		}
	}

	// 定义温差报警信息结构体
	public static class NET_DVR_THERMOMETRY_DIFF_ALARM extends Structure {
		public int dwSize;
		public int dwChannel;
		public byte byAlarmID1;
		public byte byAlarmID2;
		public short wPresetNo;
		public float fRuleTemperatureDiff;
		public float fCurTemperatureDiff;

		public NET_DVR_THERMOMETRY_DIFF_ALARM(Pointer p) {
			super(p);
		}

		@Override
		protected List<String> getFieldOrder() {
			return Arrays.asList("dwSize", "dwChannel", "byAlarmID1", "byAlarmID2", "wPresetNo", "fRuleTemperatureDiff",
					"fCurTemperatureDiff");
		}
	}

	//打印报警结构体信息
	// 打印结构体的字段和解释
	public static void printThermometryAlarmInfo(NET_DVR_THERMOMETRY_ALARM alarmInfo) {
		System.out.println("NET_DVR_THERMOMETRY_ALARM 结构体信息:");
		System.out.println("结构体大小 (dwSize): " + alarmInfo.dwSize);
		System.out.println("通道号 (dwChannel): " + alarmInfo.dwChannel);
		System.out.println("规则ID (byRuleID): " + alarmInfo.byRuleID);
		System.out.println("测温单位 (byThermometryUnit): " + getThermometryUnit(alarmInfo.byThermometryUnit));
		System.out.println("预置点号 (wPresetNo): " + alarmInfo.wPresetNo);
		System.out.println("PTZ坐标信息 (struPtzInfo): " + alarmInfo.struPtzInfo.toString());
		System.out.println("报警等级 (byAlarmLevel): " + getAlarmLevel(alarmInfo.byAlarmLevel));
		System.out.println("报警类型 (byAlarmType): " + getAlarmType(alarmInfo.byAlarmType));
		System.out.println("报警规则 (byAlarmRule): " + getAlarmRule(alarmInfo.byAlarmRule));
		System.out.println("规则标定类型 (byRuleCalibType): " + getRuleCalibType(alarmInfo.byRuleCalibType));
		System.out.println("点测温坐标 (struPoint): " + alarmInfo.struPoint.toString());
		System.out.println("区域测温坐标 (struRegion): " + alarmInfo.struRegion.toString());
		System.out.println("配置规则温度 (fRuleTemperature): " + alarmInfo.fRuleTemperature + "℃");
		System.out.println("当前温度 (fCurrTemperature): " + alarmInfo.fCurrTemperature + "℃");
		System.out.println("可见光图片长度 (dwPicLen): " + alarmInfo.dwPicLen);
		System.out.println("热成像图片长度 (dwThermalPicLen): " + alarmInfo.dwThermalPicLen);
		System.out.println("热成像附加信息长度 (dwThermalInfoLen): " + alarmInfo.dwThermalInfoLen);
		System.out.println("线、框测温最高温度位置坐标 (struHighestPoint): " + alarmInfo.struHighestPoint.toString());
		System.out.println("容差温度 (fToleranceTemperature): " + alarmInfo.fToleranceTemperature);
		System.out.println("温度预警等待时间 (dwAlertFilteringTime): " + alarmInfo.dwAlertFilteringTime + "秒");
		System.out.println("温度报警等待时间 (dwAlarmFilteringTime): " + alarmInfo.dwAlarmFilteringTime + "秒");
		System.out.println("温度突变记录周期 (dwTemperatureSuddenChangeCycle): " + alarmInfo.dwTemperatureSuddenChangeCycle + "秒");
		System.out.println("温度突变值 (fTemperatureSuddenChangeValue): " + alarmInfo.fTemperatureSuddenChangeValue);
		System.out.println("图片数据传输方式 (byPicTransType): " + getPicTransType(alarmInfo.byPicTransType));
	}

	// 辅助方法：转换枚举值
	private static String getThermometryUnit(byte unit) {
		switch (unit) {
			case 0: return "摄氏度 (℃)";
			case 1: return "华氏度 (℉)";
			case 2: return "开尔文 (K)";
			default: return "未知";
		}
	}

	private static String getAlarmLevel(byte level) {
		return level == 0 ? "预警" : "报警";
	}

	private static String getAlarmType(byte type) {
		switch (type) {
			case 0: return "最高温度";
			case 1: return "最低温度";
			case 2: return "平均温度";
			default: return "未知";
		}
	}

	private static String getAlarmRule(byte rule) {
		return rule == 0 ? "大于" : "小于";
	}

	private static String getRuleCalibType(byte type) {
		switch (type) {
			case 0: return "点";
			case 1: return "框";
			case 2: return "线";
			default: return "未知";
		}
	}

	private static String getPicTransType(byte type) {
		return type == 0 ? "二进制" : "URL";
	}
	//============================================================通用报警布防==================================================
	public class NET_DVR_ALARMINFO_V30 extends Structure {
		public NET_DVR_ALARMINFO_V30(Pointer p) {
			super(p);
		}
		/**
		 * 报警类型：
		 * 0 - 信号量报警，
		 * 1 - 硬盘满，
		 * 2 - 信号丢失，
		 * 3 - 移动侦测，
		 * 4 - 硬盘未格式化，
		 * 5 - 读写硬盘出错，
		 * 6 - 遮挡报警，
		 * 7 - 制式不匹配，
		 * 8 - 非法访问，
		 * 9 - 视频信号异常，
		 * 10 - 录像/抓图异常，
		 * 11 - 智能场景变化，
		 * 12 - 阵列异常，
		 * 13 - 前端/录像分辨率不匹配，
		 * 15 - 智能侦测，
		 * 16 - POE供电异常，
		 * 17 - 闪光灯异常，
		 * 18 - 磁盘满负荷异常报警，
		 * 19 - 音频丢失，
		 * 23 - 脉冲报警，
		 * 24 - 人脸库硬盘异常，
		 * 25 - 人脸库变更，
		 * 26 - 人脸库图片变更。
		 */
		public int dwAlarmType;

		/**
		 * 报警输入端口，当报警类型为 0、23 时有效。
		 */
		public int dwAlarmInputNumber;

		/**
		 * 触发的报警输出端口，值为 1 表示该报警端口输出。
		 * 如 byAlarmOutputNumber[0]=1 表示触发第 1 个报警输出口输出，
		 * byAlarmOutputNumber[1]=1 表示触发第 2 个报警输出口，依次类推。
		 */
		public byte[] byAlarmOutputNumber = new byte[MAX_ALARMOUT_V30];

		/**
		 * 触发的录像通道，值为 1 表示该通道录像。
		 * 如 byAlarmRelateChannel[0]=1 表示触发第 1 个通道录像。
		 */
		public byte[] byAlarmRelateChannel = new byte[MAX_CHANNUM_V30];

		/**
		 * 发生报警的通道。当报警类型为 2、3、6、9、10、11、13、15、16 时有效。
		 * 如 byChannel[0]=1 表示第 1 个通道报警。
		 */
		public byte[] byChannel = new byte[MAX_CHANNUM_V30];

		/**
		 * 发生报警的硬盘。当报警类型为 1、4、5 时有效，
		 * byDiskNumber[0]=1 表示 1 号硬盘异常。
		 */
		public byte[] byDiskNumber = new byte[MAX_DISKNUM_V30];

		// 常量定义，需要根据具体场景设置
		public static final int MAX_ALARMOUT_V30 = 96; // 最大报警输出端口数
		public static final int MAX_CHANNUM_V30 = 64;  // 最大通道数
		public static final int MAX_DISKNUM_V30 = 33;  // 最大硬盘数
		@Override
		protected List<String> getFieldOrder() {
			// 按字段声明顺序返回字段名列表
			return Arrays.asList(
					"dwAlarmType",
					"dwAlarmInputNumber",
					"byAlarmOutputNumber",
					"byAlarmRelateChannel",
					"byChannel",
					"byDiskNumber"
			);
		}
	}
//智能事件布防结构体
	// NET_VCA_RULE_ALARM 结构体
	public static class NET_VCA_RULE_ALARM extends Structure {
	public NET_VCA_RULE_ALARM(Pointer p) {
		super(p);
	}
		public int dwSize;
		public int dwRelativeTime;
		public int dwAbsTime;
		public NET_VCA_RULE_INFO struRuleInfo;
		public NET_VCA_TARGET_INFO struTargetInfo;
		public NET_VCA_DEV_INFO struDevInfo;
		public int dwPicDataLen;
		public byte byPicType;
		public byte byRelAlarmPicNum;
		public byte bySmart;
		public byte byPicTransType;
		public int dwAlarmID;
		public short wDevInfoIvmsChannelEx;
		public byte byRelativeTimeFlag;
		public byte byAppendInfoUploadEnabled;
		public Pointer pAppendInfo; // 指向附加信息
		public Pointer pImage;      // 指向图片的指针

		@Override
		protected List<String> getFieldOrder() {
			return Arrays.asList(
					"dwSize", "dwRelativeTime", "dwAbsTime", "struRuleInfo", "struTargetInfo",
					"struDevInfo", "dwPicDataLen", "byPicType", "byRelAlarmPicNum", "bySmart",
					"byPicTransType", "dwAlarmID", "wDevInfoIvmsChannelEx", "byRelativeTimeFlag",
					"byAppendInfoUploadEnabled", "pAppendInfo", "pImage"
			);
		}
	}

	// NET_VCA_RULE_INFO 结构体
	public static class NET_VCA_RULE_INFO extends Structure {
		public byte byRuleID;
		public byte byRes;
		public short wEventTypeEx;
		public byte[] byRuleName = new byte[64]; // 假设名称最大为64字节
		public int dwEventType;
		public Pointer uEventParam;

		@Override
		protected List<String> getFieldOrder() {
			return Arrays.asList(
					"byRuleID", "byRes", "wEventTypeEx", "byRuleName", "dwEventType", "uEventParam"
			);
		}
	}

	// NET_VCA_TARGET_INFO 结构体
	public static class NET_VCA_TARGET_INFO extends Structure {
		public int dwID;
		public NET_VCA_RECT struRect;
		public byte[] byRes = new byte[4];

		@Override
		protected List<String> getFieldOrder() {
			return Arrays.asList("dwID", "struRect", "byRes");
		}
	}

	// NET_VCA_DEV_INFO 结构体
	public static class NET_VCA_DEV_INFO extends Structure {
		public byte[] struDevIP = new byte[16]; // 假设设备IP地址为16字节
		public short wPort;
		public byte byChannel;
		public byte byIvmsChannel;

		@Override
		protected List<String> getFieldOrder() {
			return Arrays.asList("struDevIP", "wPort", "byChannel", "byIvmsChannel");
		}
	}

	// NET_VCA_EVENT_UNION 事件参数联合体
	public static class NET_VCA_EVENT_UNION extends Structure {
		// 根据具体事件类型定义该联合体
	}

	// NET_VCA_RECT 矩形结构体
	public static class NET_VCA_RECT extends Structure {
		public int dwX;
		public int dwY;
		public int dwWidth;
		public int dwHeight;

		@Override
		protected List<String> getFieldOrder() {
			return Arrays.asList("dwX", "dwY", "dwWidth", "dwHeight");
		}
	}













	// 持续运行以接收报警
	Object monitor = new Object();

	public static void main(String[] args) {

		boolean net_dvr_init = HCNetSDK.INSTANCE.NET_DVR_Init();
		if (!net_dvr_init) {
			System.out.println("摄像头初始化_NET_DVR_Init_出错：" + HCNetSDK.INSTANCE.NET_DVR_GetLastError());
		}
		HCNetSDK.NET_DVR_DEVICEINFO_V30 deviceInfo = new HCNetSDK.NET_DVR_DEVICEINFO_V30();
		int lUserID = HCNetSDK.INSTANCE.NET_DVR_Login_V30("173.25.130.113", 8000,
			"admin", "hkws12345", deviceInfo);
		if (lUserID < 0) {
			System.out.println("登录_NET_DVR_Login_V30_出错：" + HCNetSDK.INSTANCE.NET_DVR_GetLastError());
		}



		HCNetSDK sdk = HCNetSDK.INSTANCE;

		// 设置报警回调函数
		AlarmCallback callback = new AlarmCallback();
		sdk.NET_DVR_SetDVRMessageCallBack_V50(0, callback, null);


		// 设置报警布防通道
		Pointer setupParam = new Memory(1024); // 伪代码
		int alarmHandle = sdk.NET_DVR_SetupAlarmChan_V41(lUserID, setupParam);
		if (alarmHandle < 0) {
			System.out.println("布防失败");
			sdk.NET_DVR_Logout(lUserID);
			return;
		}


		System.out.println("等待报警信息...");
		synchronized (monitor) {
			try {
				monitor.wait(60000); // 主线程等待10秒，期间可接收回调函数报警信息
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		// 撤防
		sdk.NET_DVR_CloseAlarmChan_V30(alarmHandle);

		// 注销设备
		sdk.NET_DVR_Logout(lUserID);

		// 释放SDK资源
//		sdk.NET_DVR_Cleanup();
		System.out.println("程序结束");
	}


}
