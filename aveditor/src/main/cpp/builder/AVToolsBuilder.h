//
// Created by 阳坤 on 2020-08-19.
//

#ifndef IKAVEDIT_AVTOOLSBUILDER_H
#define IKAVEDIT_AVTOOLSBUILDER_H


#include "IToolsBuilder.h"

class AVToolsBuilder : public IToolsBuilder {
public:
    static AVToolsBuilder *getInstance() {
        static AVToolsBuilder tools;
        return &tools;
    }

public:
    /**
  * 创建播放模块
  * @return
  */
    virtual IPlayerProxy *getPlayEngine(unsigned char index = 0);

    /**
  * 创建速率模块
  * @return
  */
    virtual  SoundTouchUtils *getSoundTouchEngine();

};


#endif //IKAVEDIT_AVTOOLSBUILDER_H
