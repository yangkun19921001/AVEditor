//
// Created by 阳坤 on 2020-08-19.
//

#ifndef IKAVEDIT_ITOOLSBUILDER_H
#define IKAVEDIT_ITOOLSBUILDER_H


#include <editor/IEditor.h>
#include "../play/IPlayerProxy.h"

class IToolsBuilder {
    /**
     * 创建编辑模块
     */
public:
    /**
      * 创建播放模块
      * @return
      */
    virtual IPlayerProxy *getPlayEngine(unsigned char index = 0) = 0;

    /**
      * 创建速率模块
      * @return
      */
    virtual SoundTouchUtils *getSoundTouchEngine(unsigned char index = 1) = 0;

    /**
      * 创建音视频编辑模块
      * @return
      */
    virtual IEditor *getEditorEngine() = 0;
};


#endif //IKAVEDIT_ITOOLSBUILDER_H
