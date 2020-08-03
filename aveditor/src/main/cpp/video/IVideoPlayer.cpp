//
// Created by 阳坤 on 2020-05-22.
//

#include "IVideoPlayer.h"

void IVideoPlayer::update(AVData data) {
    if (isPause())
        return;
    //渲染数据
    this->render(data);
}
