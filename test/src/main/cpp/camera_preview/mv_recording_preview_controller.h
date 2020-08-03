#ifndef MV_RECORDING_PREVIEW_CONTROLLER_H
#define MV_RECORDING_PREVIEW_CONTROLLER_H

#include <unistd.h>
#include <pthread.h>
#include <android/native_window.h>
#include <android/native_window_jni.h>
#include <EGL/egl.h>
#include <EGL/eglext.h>

#include "../camera_preview/recording_preview_renderer.h"
#include "egl_core/egl_core.h"
#include "message_queue/message_queue.h"
#include "message_queue/handler.h"

#define CAMERA_FACING_BACK										0
#define CAMERA_FACING_FRONT										1

    enum RenderThreadMessage {
        MSG_RENDER_FRAME = 0,
        MSG_EGL_THREAD_CREATE,
		MSG_EGL_CREATE_PREVIEW_SURFACE,
        MSG_SWITCH_CAMERA_FACING,
		MSG_EGL_DESTROY_PREVIEW_SURFACE,
        MSG_EGL_THREAD_EXIT
    };

class MVRecordingPreviewHandler;

class MVRecordingPreviewController {
public:
	MVRecordingPreviewController();
    virtual ~MVRecordingPreviewController();

    /** 1:准备EGL Context与EGLThread **/
    void prepareEGLContext(ANativeWindow* window, JavaVM *g_jvm, jobject obj, int screenWidth, int screenHeight, int cameraFacingId);

    /** 2:当Camera捕捉到新的一帧图像会调用 **/
    void notifyFrameAvailable();

    /** 4:切换摄像头转向 **/
    void switchCameraFacing();

    /** 5:重置绘制区域大小 **/
    void resetRenderSize(int screenWidth, int screenHeight);

    /** 7:销毁EGLContext与EGLThread **/
    virtual void destroyEGLContext();

protected:
    /** 启动预览线程的五重要个参数 **/
    ANativeWindow* _window;
    JavaVM *g_jvm;
    jobject obj;
    int screenWidth;
    int screenHeight;

    bool isInSwitchingCamera;

	int64_t startTime;

    /** 将输入纹理传回Camera配置结束之后，返回CameraInfo **/
    int facingId;
    int degress;
    int textureWidth;
    int textureHeight;

    int cameraWidth;
    int cameraHeight;

    MVRecordingPreviewHandler* handler;
	MessageQueue* queue;
	pthread_t _threadId;
	static void* threadStartCallback(void *myself);
	void processMessage();

    /** 线程中核心的处理方法 **/
    //创建EGL资源以及调用Android创建Camera
    EGLCore* eglCore;
    EGLSurface previewSurface;
    //它负责处理:拷贝纹理(copier)、处理纹理(processor)、输出纹理(render) 核心操作
    RecordingPreviewRenderer* renderer;
    virtual void buildRenderInstance();

    virtual void processVideoFrame(float position);
    void draw();

    void configCamera();
    void startCameraPreview();
    void updateTexImage();
    void releaseCamera();
    void deleteGlobalRef();

public:
	void createWindowSurface(ANativeWindow* window);
	void destroyWindowSurface();

public:
    virtual bool initialize();
    //销毁EGL资源并且调用Andorid销毁Camera
    virtual void destroy();
    void createPreviewSurface();
    void destroyPreviewSurface();
    void switchCamera();
    void renderFrame();
};


class MVRecordingPreviewHandler: public Handler {
	private:
		MVRecordingPreviewController* previewController;

	public:
		MVRecordingPreviewHandler(MVRecordingPreviewController* previewController, MessageQueue* queue) :
				Handler(queue) {
			this->previewController = previewController;
		}
		void handleMessage(Message* msg) {
			int what = msg->getWhat();
			switch (what) {
			case MSG_EGL_THREAD_CREATE:
				previewController->initialize();
				break;
			case MSG_EGL_CREATE_PREVIEW_SURFACE:
				previewController->createPreviewSurface();
				break;
			case MSG_SWITCH_CAMERA_FACING:
				previewController->switchCamera();
				break;
			case MSG_EGL_DESTROY_PREVIEW_SURFACE:
				previewController->destroyPreviewSurface();
				break;
			case MSG_EGL_THREAD_EXIT:
				previewController->destroy();
				break;
			case MSG_RENDER_FRAME:
				previewController->renderFrame();
				break;
			}
		}
	};

#endif // MV_RECORDING_PREVIEW_CONTROLLER_H
