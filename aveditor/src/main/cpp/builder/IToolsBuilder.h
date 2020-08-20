//
// Created by 阳坤 on 2020-08-19.
//

#ifndef IKAVEDIT_ITOOLSBUILDER_H
#define IKAVEDIT_ITOOLSBUILDER_H


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
};


#endif //IKAVEDIT_ITOOLSBUILDER_H
