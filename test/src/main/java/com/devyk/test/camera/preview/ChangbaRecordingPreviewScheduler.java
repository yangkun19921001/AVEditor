package com.devyk.test.camera.preview;

import com.phuket.tour.camera.preview.ChangbaRecordingPreviewView.ChangbaRecordingPreviewViewCallback;
import com.phuket.tour.camera.preview.ChangbaVideoCamera.ChangbaVideoCameraCallback;

import android.hardware.Camera.CameraInfo;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceHolder;

public class ChangbaRecordingPreviewScheduler
		implements ChangbaVideoCameraCallback, ChangbaRecordingPreviewViewCallback {
	private static final String TAG = "ChangbaRecordingPreviewScheduler";
	private ChangbaRecordingPreviewView mPreviewView;
	private ChangbaVideoCamera mCamera;

	public ChangbaRecordingPreviewScheduler(ChangbaRecordingPreviewView previewView, ChangbaVideoCamera camera) {
		isStopped = false;
		this.mPreviewView = previewView;
		this.mCamera = camera;
		this.mPreviewView.setCallback(this);
		this.mCamera.setCallback(this);
	}
	
	public int getNumberOfCameras() {
		if (null != mCamera) {
			return mCamera.getNumberOfCameras();
		}
		return -1;
	}

	/** 切换摄像头, 底层会在返回来调用configCamera, 之后在启动预览 **/
	public native void switchCameraFacing();
	
	private boolean isFirst = true;
	private boolean isSurfaceExsist = false;
	private boolean isStopped = false;
	private int defaultCameraFacingId = CameraInfo.CAMERA_FACING_FRONT;
	@Override
	public void createSurface(Surface surface, int width, int height){
		startPreview(surface, width, height, defaultCameraFacingId);
	}
	private void startPreview(Surface surface, int width, int height, final int cameraFacingId){
		if (isFirst) {
			prepareEGLContext(surface, width, height, cameraFacingId);
			isFirst = false;
		} else {
			createWindowSurface(surface);
		}
		isSurfaceExsist = true;
	}
	public void startPreview(final int cameraFacingId){
		try {
			if(null != mPreviewView){
				SurfaceHolder holder = mPreviewView.getHolder();
				if(null != holder){
					Surface surface = holder.getSurface();
					if(null != surface){
						startPreview(surface, mPreviewView.getWidth(), mPreviewView.getHeight(), cameraFacingId);
					}
				}
			}
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}
	public native void prepareEGLContext(Surface surface, int width, int height, int cameraFacingId);
	public native void createWindowSurface(Surface surface);

	@Override
	public native void resetRenderSize(int width, int height);
	
	@Override
	public void destroySurface(){
		if(isStopped){
			this.stopPreview();
		} else{
			this.destroyWindowSurface();
		}
		isSurfaceExsist = false;
	}

	public void stop() {
		isStopped = true;
		if(!isSurfaceExsist){
			this.stopPreview();
		}
	}
	private void stopPreview(){
		this.destroyEGLContext();
		isFirst = true;
		isSurfaceExsist = false;
		isStopped = false;
	}
	public native void destroyWindowSurface();
	public native void destroyEGLContext();

	/**
	 * 当Camera捕捉到了新的一帧图像的时候会调用这个方法,因为更新纹理必须要在EGLThread中,
	 * 所以配合下updateTexImageFromNative使用
	 **/
	@Override
	public native void notifyFrameAvailable();
	
	public void onPermissionDismiss(String tip){
		Log.i("problem", "onPermissionDismiss : " + tip);
	}

	private CameraConfigInfo mConfigInfo;
	/** 当底层创建好EGLContext之后，回调回来配置Camera，返回Camera的配置信息，然后在EGLThread线程中回调回来继续做Camera未完的配置以及Preview **/
	public CameraConfigInfo configCameraFromNative(int cameraFacingId){
		defaultCameraFacingId = cameraFacingId;
		mConfigInfo = mCamera.configCameraFromNative(cameraFacingId);
		return mConfigInfo;
	}
	/** 当底层EGLThread创建初纹理之后,设置给Camera **/
	public void startPreviewFromNative(int textureId) {
		mCamera.setCameraPreviewTexture(textureId);
	}
	/** 当底层EGLThread更新纹理的时候调用这个方法 **/
	public void updateTexImageFromNative() {
		mCamera.updateTexImage();
	}
	/** 释放掉当前的Camera **/
	public void releaseCameraFromNative(){
		mCamera.releaseCamera();
	}
	
}
