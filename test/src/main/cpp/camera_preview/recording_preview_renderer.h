#ifndef RECORDING_PREVIEW_RENDERER_H
#define RECORDING_PREVIEW_RENDERER_H

#include "opengl_media/render/video_gl_surface_render.h"
#include "opengl_media/texture/gpu_texture_frame.h"
#include "opengl_media/texture_copier/gpu_texture_frame_copier.h"
#include "CommonTools.h"
#include "egl_core/gl_tools.h"

#define PREVIEW_FILTER_SEQUENCE_IN									0
#define PREVIEW_FILTER_SEQUENCE_OUT									10 * 60 * 60 * 1000000
#define PREVIEW_FILTER_POSITION										0.5

static GLfloat CAMERA_TRIANGLE_VERTICES[8] = {
		-1.0f, -1.0f,	// 0 top left
		1.0f, -1.0f,	// 1 bottom left
		-1.0f, 1.0f,  // 2 bottom right
		 1.0f, 1.0f,	// 3 top right
	};

static GLfloat CAMERA_TEXTURE_NO_ROTATION[8] = {
        0.0f, 1.0f,
        1.0f, 1.0f,
        0.0f, 0.0f,
        1.0f, 0.0f
	};

static GLfloat CAMERA_TEXTURE_ROTATED_90[8] = {
        1.0f, 1.0f,
        1.0f, 0.0f,
        0.0f, 1.0f,
        0.0f, 0.0f
	};

static GLfloat CAMERA_TEXTURE_ROTATED_180[8] = {
        1.0f, 0.0f,
        0.0f, 0.0f,
        1.0f, 1.0f,
        0.0f, 1.0f
	};

static GLfloat CAMERA_TEXTURE_ROTATED_270[8] = {
        0.0f, 0.0f,
        0.0f, 1.0f,
        1.0f, 0.0f,
        1.0f, 1.0f
	};

class RecordingPreviewRenderer {
public:
	RecordingPreviewRenderer();
    virtual ~RecordingPreviewRenderer();

    void init(int degress, bool isVFlip, int textureWidth, int textureHeight, int cameraWidth, int cameraHeight);
    void setDegress(int degress, bool isVFlip);
    void processFrame(float position);
    void drawToView(int videoWidth, int videoHeight);
    void drawToViewWithAutofit(int videoWidth, int videoHeight, int texWidth, int texHeight);
    void dealloc();

    int getCameraTexId();

    GLuint getOutputTexId(){
    		return outputTexId;
    };
	GLuint getInputTexId() {
		return inputTexId;
	};

protected:
    //在copy以及processor中的FBO
    GLuint FBO;
    //camera捕捉到的TextureFrame 但是是SamplerOES格式的
	GPUTextureFrame* cameraTexFrame;
	//利用mCopier转换为Sampler2D格式的inputTexId
    GLuint inputTexId;
    //利用mProcessor转换为处理过的outputTexId
    GLuint outputTexId;
    //用于旋转的纹理id
    GLuint rotateTexId;
	//暂停状态下的保留的那一帧Texture
    GLuint pausedTexId;
    //暂停的时候增加的FilterId 当切换为普通预览的时候需要去掉
    int mixFilterId;
    /** 1:把Camera的纹理拷贝到inputTexId **/
    GPUTextureFrameCopier* mCopier;
    /** 3:把outputTexId渲染到View上 **/
    VideoGLSurfaceRender* mRenderer;

    /** Camera Info From Android Camera **/
    int degress;
    bool isVFlip;
    GLfloat* textureCoords;
    int textureCoordsSize;
    int textureWidth;
    int textureHeight;
    int cameraWidth;
    int cameraHeight;

    void fillTextureCoords();
    float flip(float i);
    GLfloat* getVertexCoords();
};

#endif // RECORDING_PREVIEW_RENDERER_H
