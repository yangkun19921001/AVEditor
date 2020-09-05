//
// Created by 阳坤 on 2020-08-19.
//


#include "AVToolsBuilder.h"

IPlayerProxy *AVToolsBuilder::getPlayEngine(unsigned char index) {
    return IPlayerProxy::getInstance();
}



IEditor *AVToolsBuilder::getEditorEngine() {
    static AVEditor avEditor;
    return &avEditor;
}

SoundTouchUtils *AVToolsBuilder::getSoundTouchEngine(unsigned char index) {
    return SoundTouchUtils::getInstance(index);
}
