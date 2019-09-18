package com.sunmi.sunmiK1Ext.fragment;

import android.Manifest;
import android.animation.Animator;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ImageFormat;
import android.graphics.Matrix;
import android.graphics.Outline;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CameraMetadata;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.CaptureResult;
import android.hardware.camera2.TotalCaptureResult;
import android.hardware.camera2.params.Face;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.media.Image;
import android.media.ImageReader;
import android.os.Handler;
import android.os.HandlerThread;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.util.Size;
import android.util.SparseIntArray;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.view.ViewOutlineProvider;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.sunmi.sunmiK1Ext.BaseFragment;
import com.sunmi.sunmiK1Ext.MainActivity;
import com.sunmi.sunmiK1Ext.R;
import com.sunmi.sunmiK1Ext.dialog.Create;
import com.sunmi.sunmiK1Ext.dialog.CustomDialog;
import com.sunmi.sunmiK1Ext.present.CountDownPresent;
import com.sunmi.sunmiK1Ext.view.CircularProgressView;

import java.nio.ByteBuffer;
import java.util.Arrays;

public class FaceFragment extends BaseFragment implements CountDownPresent.CountDownCallback {


    //为了使照片竖直显示
    private static final SparseIntArray ORIENTATIONS = new SparseIntArray();

    static {
        ORIENTATIONS.append(Surface.ROTATION_0, 90);
        ORIENTATIONS.append(Surface.ROTATION_90, 0);
        ORIENTATIONS.append(Surface.ROTATION_180, 270);
        ORIENTATIONS.append(Surface.ROTATION_270, 180);
    }

    private TextView msg;
    private TextView time, time_msg;
    private TextView tips;
    private CircularProgressView circularProgressView;
    private FrameLayout animSuccess, faceId;

    private TextureView cView;//用于相机预览
    private TextureView rView;//用于标注人脸
    private ImageView imageView;//拍照照片显示
    private TextView textView;
    private Button btnFront;
    private Button btnBack;
    private Button btnClose;
    private Button btnCapture;
    private TextView btnSkip;

    private Surface previewSurface;//预览Surface
    private ImageReader cImageReader;
    private Surface captureSurface;//拍照Surface

    HandlerThread cHandlerThread;//相机处理线程
    Handler cHandler;//相机处理
    CameraDevice cDevice;
    CameraCaptureSession cSession;

    CameraDevice.StateCallback cDeviceOpenCallback = null;//相机开启回调

    CaptureRequest.Builder previewRequestBuilder;//预览请求构建
    CaptureRequest previewRequest;//预览请求
    CameraCaptureSession.CaptureCallback previewCallback;//预览回调

    CaptureRequest.Builder captureRequestBuilder;
    CaptureRequest captureRequest;
    CameraCaptureSession.CaptureCallback captureCallback;
    int[] faceDetectModes;

    Size captureSize;

    boolean isFront;
    Paint pb;
    Bitmap bitmap;
    // Rect rRect;//相机成像矩形
    Size cPixelSize;//相机成像尺寸
    int cOrientation;

    long faceTime = 0;
    boolean checkFaceing = false;
    boolean checkFaceSuccess = false;

    private void checkFace(int length) {
        if (checkFaceSuccess) {
            return;
        }
        if (length == 1) {
            checkFaceSuccess = true;
            checkFaceSuccess();
        }
    }

    private synchronized void checkFaceSuccess() {
        executeCapture();
    }

    private void goNext() {

        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                msg.setText(R.string.face_success);
                time.setVisibility(View.GONE);
                time_msg.setText(R.string.face_success_next);
                tips.setVisibility(View.GONE);
                closeCamera();
                ((MainActivity) getActivity()).hotelUser.setPhoto_face(bitmap);
            }
        });
        postDelayed(new Runnable() {
            @Override
            public void run() {
                if (!isDetached()) {
                    showAnim();
                }
            }
        }, 2000);

    }

    void showAnim() {
        circularProgressView.startAnim(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                animSuccess.setVisibility(View.VISIBLE);
                goToPhone();
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
    }


    void goToPhone() {
        postDelayed(new Runnable() {
            @Override
            public void run() {
                if (!isDetached()) {
//                    replaceContent(PhotoFragment.newInstance(new PhotoFragment(), null, null));
//                    replaceContent(PhoneFragment.newInstance(new PhoneFragment(), null, null));
                    replaceContent(MifareCardFragment.newInstance(new MifareCardFragment(), null, null));
                }
            }
        }, 2000);

    }

    private void autoOpen() {
        postDelayed(new Runnable() {
            @Override
            public void run() {
                if (cView.isAvailable()) {
                    openCamera(false);
                } else {
                    autoOpen();
                }

            }
        }, 500);
    }

    public void setStop() {
        super.setStop();
        closeCamera();
    }

    public void setResume() {
        super.setResume();
        autoOpen();
    }

    @Override
    protected int getViewId() {
        return R.layout.activity_face;
    }

    @Override
    protected void initView(View view) {
        cView = view.findViewById(R.id.cView);
        rView = view.findViewById(R.id.rView);
        imageView = view.findViewById(R.id.imageView);
        textView = view.findViewById(R.id.textView);
        btnFront = view.findViewById(R.id.btnFront);
        btnBack = view.findViewById(R.id.btnBack);
        btnClose = view.findViewById(R.id.btnClose);
        btnCapture = view.findViewById(R.id.btnCapture);
        circularProgressView = (CircularProgressView) view.findViewById(R.id.circularProgressView);

        animSuccess = (FrameLayout) view.findViewById(R.id.anim_success);
        faceId = (FrameLayout) view.findViewById(R.id.faceId);


        msg = view.findViewById(R.id.msg);
        time = view.findViewById(R.id.tv_count_down);
        tips = view.findViewById(R.id.tips);
        time_msg = view.findViewById(R.id.time_msg);
        btnSkip = view.findViewById(R.id.btnSkip);

        //隐藏背景色，以免标注人脸时挡住预览画面
        rView.setAlpha(0.9f);

        btnFront.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openCamera(true);
            }
        });

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openCamera(false);
            }
        });

        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeCamera();
            }
        });

        btnCapture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                executeCapture();
            }
        });

        btnSkip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnSkip.setVisibility(View.GONE);
                goNext();
            }
        });
//        imageView.setClipToOutline(true);
//        cView.setClipToOutline(true);
//        rView.setClipToOutline(true);
        animSuccess.setClipToOutline(true);
        faceId.setClipToOutline(true);


//        cView.setOutlineProvider(getProvider());
//        rView.setOutlineProvider(getProvider());
//        imageView.setOutlineProvider(getProvider());
//        animSuccess.setOutlineProvider(getProvider());
        faceId.setOutlineProvider(getProvider());

    }

    @NonNull
    private ViewOutlineProvider getProvider() {
        return new ViewOutlineProvider() {
            @Override
            public void getOutline(View view, Outline outline) {
                outline.setRoundRect(0, (view.getHeight() - view.getMeasuredWidth()) / 2
                        , view.getMeasuredWidth(),
                        (view.getHeight() + view.getMeasuredWidth()) / 2,
                        view.getMeasuredWidth() / 2);
            }
        };
    }

    @Override
    protected void initData() {

    }

    @Override
    protected void initAction() {
        autoOpen();
        CountDownPresent.getInstance().setCountDownCallback(this);
        CountDownPresent.getInstance().setStartTime(30);
    }


    //TODO 摄像头静音尝试
    private void openCamera(boolean isFront) {
        closeCamera();
        this.isFront = isFront;
        String cId = null;
        if (isFront) {
            cId = CameraCharacteristics.LENS_FACING_BACK + "";
        } else {
            cId = CameraCharacteristics.LENS_FACING_FRONT + "";
        }

        CameraManager cManager = (CameraManager) getActivity().getSystemService(Context.CAMERA_SERVICE);
        try {
            //获取开启相机的相关参数
            CameraCharacteristics characteristics = cManager.getCameraCharacteristics(cId);
            StreamConfigurationMap map = characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
            Size[] previewSizes = map.getOutputSizes(SurfaceTexture.class);//获取预览尺寸
            Size[] captureSizes = map.getOutputSizes(ImageFormat.JPEG);//获取拍照尺寸
            cOrientation = characteristics.get(CameraCharacteristics.SENSOR_ORIENTATION);//获取相机角度
            Rect cRect = characteristics.get(CameraCharacteristics.SENSOR_INFO_ACTIVE_ARRAY_SIZE);//获取成像区域
            cPixelSize = characteristics.get(CameraCharacteristics.SENSOR_INFO_PIXEL_ARRAY_SIZE);//获取成像尺寸，同上

            //可用于判断是否支持人脸检测，以及支持到哪种程度
            faceDetectModes = characteristics.get(CameraCharacteristics.STATISTICS_INFO_AVAILABLE_FACE_DETECT_MODES);//支持的人脸检测模式
            int maxFaceCount = characteristics.get(CameraCharacteristics.STATISTICS_INFO_MAX_FACE_COUNT);//支持的最大检测人脸数量

            //此处写死640*480，实际从预览尺寸列表选择
            Size sSize = new Size(previewSizes[0].getWidth(), previewSizes[0].getHeight());//previewSizes[0];

            //设置预览尺寸（避免控件尺寸与预览画面尺寸不一致时画面变形）
            cView.getSurfaceTexture().setDefaultBufferSize(sSize.getWidth(), sSize.getHeight());

            if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                showToast("请授予摄像头权限");
                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.CAMERA}, 0);
                return;
            }
            //根据摄像头ID，开启摄像头
            try {
                cManager.openCamera(cId, getCDeviceOpenCallback(), getCHandler());
            } catch (CameraAccessException e) {
                Log(Log.getStackTraceString(e));
            }catch (Exception e){       //smile刷脸服务会占用摄像头，导致打不开
                btnSkip.setVisibility(View.VISIBLE);
                showToast("摄像头已被强大势力占用，击败它：拔出摄像头，整机重启，清理后台程序，再插上摄像头，打开系统相机确认即可ヾ(◍°∇°◍)ﾉﾞ");
            }

        } catch (CameraAccessException e) {
            Log(Log.getStackTraceString(e));
        }catch (Exception e){
            btnSkip.setVisibility(View.VISIBLE);
            showToast("受神秘力量干扰，搜索不到摄像头了呢(ｷ｀ﾟДﾟ´)!!");
        }
    }

    private void closeCamera() {

        if (cSession != null) {
            cSession.close();
            cSession = null;
        }
        if (cDevice != null) {
            cDevice.close();
            cDevice = null;
        }
        if (cImageReader != null) {
            cImageReader.close();
            cImageReader = null;
            captureRequestBuilder = null;
        }
        if (cHandlerThread != null) {
            cHandlerThread.quitSafely();
            try {
                cHandlerThread.join();
                cHandlerThread = null;
                cHandler = null;
            } catch (InterruptedException e) {
                Log(Log.getStackTraceString(e));
            }
        }

    }

    /**
     * 初始化并获取相机开启回调对象。当准备就绪后，发起预览请求
     */
    private CameraDevice.StateCallback getCDeviceOpenCallback() {
        if (cDeviceOpenCallback == null) {
            cDeviceOpenCallback = new CameraDevice.StateCallback() {
                @Override
                public void onOpened(@NonNull CameraDevice camera) {
                    cDevice = camera;
                    try {
                        //创建Session，需先完成画面呈现目标（此处为预览和拍照Surface）的初始化
                        camera.createCaptureSession(Arrays.asList(getPreviewSurface(), getCaptureSurface()), new CameraCaptureSession.StateCallback() {
                            @Override
                            public void onConfigured(@NonNull CameraCaptureSession session) {
                                cSession = session;
                                //构建预览请求，并发起请求
                                Log("[发出预览请求]");
                                if (isDetached()) {
                                    return;
                                }
                                try {
                                    getActivity().runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            imageView.setVisibility(View.GONE);
                                        }
                                    });
                                    session.setRepeatingRequest(getPreviewRequest(), getPreviewCallback(), getCHandler());
                                } catch (CameraAccessException e) {
                                    Log(Log.getStackTraceString(e));
                                }
                            }

                            @Override
                            public void onConfigureFailed(@NonNull CameraCaptureSession session) {
                                session.close();
                            }
                        }, getCHandler());
                    } catch (CameraAccessException e) {
                        Log(Log.getStackTraceString(e));
                    }
                }

                @Override
                public void onDisconnected(@NonNull CameraDevice camera) {
                    camera.close();
                }

                @Override
                public void onError(@NonNull CameraDevice camera, int error) {
                    camera.close();
                }
            };
        }
        return cDeviceOpenCallback;
    }

    /**
     * 初始化并获取相机线程处理
     *
     * @return
     */
    private Handler getCHandler() {
        if (cHandler == null) {
            //单独开一个线程给相机使用
            cHandlerThread = new HandlerThread("cHandlerThread");
            cHandlerThread.start();
            cHandler = new Handler(cHandlerThread.getLooper());
        }
        return cHandler;
    }

    /**
     * 获取支持的最高人脸检测级别
     *
     * @return
     */
    private int getFaceDetectMode() {
        if (faceDetectModes == null) {
            return CaptureRequest.STATISTICS_FACE_DETECT_MODE_FULL;
        } else {
            return faceDetectModes[faceDetectModes.length - 1];
        }
    }


    /*---------------------------------预览相关---------------------------------*/

    /**
     * 初始化并获取预览回调对象
     *
     * @return
     */
    private CameraCaptureSession.CaptureCallback getPreviewCallback() {
        if (previewCallback == null) {
            previewCallback = new CameraCaptureSession.CaptureCallback() {
                public void onCaptureCompleted(@NonNull CameraCaptureSession session, @NonNull CaptureRequest request, @NonNull TotalCaptureResult result) {
                    FaceFragment.this.onCameraImagePreviewed(result);
                }
            };
        }
        return previewCallback;
    }

    /**
     * 生成并获取预览请求
     *
     * @return
     */
    private CaptureRequest getPreviewRequest() {
        previewRequest = getPreviewRequestBuilder().build();
        return previewRequest;
    }

    /**
     * 初始化并获取预览请求构建对象，进行通用配置，并每次获取时进行人脸检测级别配置
     *
     * @return
     */
    private CaptureRequest.Builder getPreviewRequestBuilder() {
        if (previewRequestBuilder == null) {
            try {
                previewRequestBuilder = cSession.getDevice().createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
                previewRequestBuilder.addTarget(getPreviewSurface());
                previewRequestBuilder.set(CaptureRequest.CONTROL_MODE, CameraMetadata.CONTROL_MODE_AUTO);//自动曝光、白平衡、对焦
            } catch (CameraAccessException e) {
                Log(Log.getStackTraceString(e));
            }
        }
        previewRequestBuilder.set(CaptureRequest.STATISTICS_FACE_DETECT_MODE, getFaceDetectMode());//设置人脸检测级别
        return previewRequestBuilder;
    }

    /**
     * 获取预览Surface
     *
     * @return
     */
    private Surface getPreviewSurface() {
        if (previewSurface == null) {
            previewSurface = new Surface(cView.getSurfaceTexture());
        }
        return previewSurface;
    }

    /**
     * 处理相机画面处理完成事件，获取检测到的人脸坐标，换算并绘制方框
     *
     * @param result
     */
    private void onCameraImagePreviewed(CaptureResult result) {
        Face faces[] = result.get(CaptureResult.STATISTICS_FACES);
        showMessage(false, "人脸个数:[" + faces.length + "]");
        checkFace(faces.length);
        Canvas canvas = rView.lockCanvas();
        canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);//旧画面清理覆盖

        if (faces.length > 0) {
            for (int i = 0; i < faces.length; i++) {
                Rect fRect = faces[i].getBounds();
                Log("[R" + i + "]:[left:" + fRect.left + ",top:" + fRect.top + ",right:" + fRect.right + ",bottom:" + fRect.bottom + "]");
//                showMessage(true, "[R" + i + "]:[left:" + fRect.left + ",top:" + fRect.top + ",right:" + fRect.right + ",bottom:" + fRect.bottom + "]");

                //人脸检测坐标基于相机成像画面尺寸以及坐标原点。此处进行比例换算
                //成像画面与方框绘制画布长宽比比例（同画面角度情况下的长宽比例（此处前后摄像头成像画面相对预览画面倒置（±90°），计算比例时长宽互换））
                float scaleWidth = canvas.getHeight() * 1.0f / cPixelSize.getWidth();
                float scaleHeight = canvas.getWidth() * 1.0f / cPixelSize.getHeight();

                //坐标缩放
                int l = (int) (fRect.left * scaleWidth);
                int t = (int) (fRect.top * scaleHeight);
                int r = (int) (fRect.right * scaleWidth);
                int b = (int) (fRect.bottom * scaleHeight);
                Log("[T" + i + "]:[left:" + l + ",top:" + t + ",right:" + r + ",bottom:" + b + "]");
//                showMessage(true, "[T" + i + "]:[left:" + l + ",top:" + t + ",right:" + r + ",bottom:" + b + "]");

                //人脸检测坐标基于相机成像画面尺寸以及坐标原点。此处进行坐标转换以及原点(0,0)换算
                //人脸检测：坐标原点为相机成像画面的左上角，left、top、bottom、right以成像画面左上下右为基准
                //画面旋转后：原点位置不一样，根据相机成像画面的旋转角度需要换算到画布的左上角，left、top、bottom、right基准也与原先不一样，
                //如相对预览画面相机成像画面角度为90°那么成像画面坐标的top，在预览画面就为left。如果再翻转，那成像画面的top就为预览画面的right，且坐标起点为右，需要换算到左边
                if (isFront) {
                    //此处前置摄像头成像画面相对于预览画面顺时针90°+翻转。left、top、bottom、right变为bottom、right、top、left，并且由于坐标原点由左上角变为右下角，X,Y方向都要进行坐标换算
                    canvas.drawRect(canvas.getWidth() - b, canvas.getHeight() - r, canvas.getWidth() - t, canvas.getHeight() - l, getPaint());
                } else {
                    //此处后置摄像头成像画面相对于预览画面顺时针270°，left、top、bottom、right变为bottom、left、top、right，并且由于坐标原点由左上角变为左下角，Y方向需要进行坐标换算
                    canvas.drawRect(canvas.getWidth() - b, l, canvas.getWidth() - t, r, getPaint());
                }
            }
        }
        rView.unlockCanvasAndPost(canvas);
    }

    /**
     * 初始化画笔
     */
    private Paint getPaint() {
        if (pb == null) {
            pb = new Paint();
            pb.setColor(Color.WHITE);
            pb.setStrokeWidth(4);
            pb.setStyle(Paint.Style.STROKE);//使绘制的矩形中空
        }
        return pb;
    }


    /*---------------------------------预览相关---------------------------------*/


    /*---------------------------------拍照相关---------------------------------*/

    /**
     * 初始化拍照相关
     */
    private Surface getCaptureSurface() {
        if (cImageReader == null) {
            cImageReader = ImageReader.newInstance(getCaptureSize().getWidth(), getCaptureSize().getHeight(), ImageFormat.JPEG, 2);
            cImageReader.setOnImageAvailableListener(new ImageReader.OnImageAvailableListener() {
                @Override
                public void onImageAvailable(ImageReader reader) {
                    onCaptureFinished(reader);
                }
            }, getCHandler());
            captureSurface = cImageReader.getSurface();
        }
        return captureSurface;
    }

    public void SetCaptureSize(Size captureSize) {
        this.captureSize = captureSize;
    }

    /**
     * 获取拍照尺寸
     *
     * @return
     */
    private Size getCaptureSize() {
        if (captureSize != null) {
            return captureSize;
        } else {
            return cPixelSize;
        }
    }

    /**
     * 执行拍照
     */
    private void executeCapture() {
        try {
            Log.i(this.getClass().getName(), "发出请求");
            cSession.capture(getCaptureRequest(), getCaptureCallback(), getCHandler());
        } catch (CameraAccessException e) {
            Log(Log.getStackTraceString(e));
        }
    }

    private CaptureRequest getCaptureRequest() {
        captureRequest = getCaptureRequestBuilder().build();
        return captureRequest;
    }

    private CaptureRequest.Builder getCaptureRequestBuilder() {
        if (captureRequestBuilder == null) {
            try {
                captureRequestBuilder = cSession.getDevice().createCaptureRequest(CameraDevice.TEMPLATE_STILL_CAPTURE);
                captureRequestBuilder.set(CaptureRequest.CONTROL_MODE, CameraMetadata.CONTROL_MODE_AUTO);
                captureRequestBuilder.addTarget(getCaptureSurface());
                //TODO 拍照静音尝试
//TODO 1 照片旋转
                int rotation = getActivity().getWindowManager().getDefaultDisplay().getRotation();
                int rotationTo = getOrientation(rotation);
                captureRequestBuilder.set(CaptureRequest.JPEG_ORIENTATION, rotationTo);
            } catch (CameraAccessException e) {
                Log(Log.getStackTraceString(e));
            }
        }
        return captureRequestBuilder;
    }

    private CameraCaptureSession.CaptureCallback getCaptureCallback() {
        if (captureCallback == null) {
            captureCallback = new CameraCaptureSession.CaptureCallback() {
                public void onCaptureCompleted(@NonNull CameraCaptureSession session, @NonNull CaptureRequest request, @NonNull TotalCaptureResult result) {
                    FaceFragment.this.onCameraImagePreviewed(result);
                }
            };
        }
        return captureCallback;
    }

//https://github.com/googlesamples/android-Camera2Basic

    /**
     * Retrieves the JPEG orientation from the specified screen rotation.
     *
     * @param rotation The screen rotation.
     * @return The JPEG orientation (one of 0, 90, 270, and 360)
     */
    private int getOrientation(int rotation) {
        // Sensor orientation is 90 for most devices, or 270 for some devices (eg. Nexus 5X)
        // We have to take that into account and rotate JPEG properly.
        // For devices with orientation of 90, we simply return our mapping from ORIENTATIONS.
        // For devices with orientation of 270, we need to rotate the JPEG 180 degrees.
        return (ORIENTATIONS.get(rotation) + cOrientation + 270) % 360;
    }

    /**
     * 处理相机拍照完成的数据
     *
     * @param reader
     */
    private void onCaptureFinished(ImageReader reader) {
        Image image = reader.acquireLatestImage();
        ByteBuffer buffer = image.getPlanes()[0].getBuffer();
        byte[] data = new byte[buffer.remaining()];
        buffer.get(data);
        image.close();
        buffer.clear();

        if (bitmap != null) {
            bitmap.recycle();
            bitmap = null;
        }
        bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
        data = null;

        if (bitmap != null) {
            //TODO 2 前置摄像头翻转照片
            if (isFront) {
                Matrix matrix = new Matrix();
                matrix.postScale(-1, 1);
                Bitmap imgToShow = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, false);
                bitmap.recycle();
                showImage(imgToShow);
            } else {
                showImage(bitmap);
            }
        }
        Runtime.getRuntime().gc();
        goNext();
    }

    @Override
    public void onDestroy() {
        closeCamera();
        super.onDestroy();
    }


    private void showImage(final Bitmap image) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                imageView.setVisibility(View.VISIBLE);
                imageView.setImageBitmap(image);
            }
        });
    }
    /*---------------------------------拍照相关---------------------------------*/

    private void showMessage(final boolean add, final String message) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (add) {
                    textView.setText(textView.getText() + "\n" + message);
                } else {
                    textView.setText(message);
                }
            }
        });
    }

    CustomDialog dialog;

    @Override
    public void onTick(long millisUntilFinished) {
        time.setText(millisUntilFinished + "");
    }

    @Override
    public void onSuccess() {
        showDialog();
        closeCamera();
    }

    void showDialog() {
        if (dialog == null) {
            dialog = Create.createDialog(getContext(), R.drawable.ic_face, getString(R.string.dialog_no_face), new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                    backTopFragment();
                }
            }, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                    openCamera(false);
                    CountDownPresent.getInstance().setStartTime(30);
                }
            }, new CustomDialog.DialogOver() {
                @Override
                public void over() {
                    backTopFragment();
                }
            });
            dialog.show();
        } else {
            dialog.show();
        }
    }


}
