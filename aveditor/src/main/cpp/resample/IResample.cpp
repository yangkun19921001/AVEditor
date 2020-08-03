//
// Created by 阳坤 on 2020-05-22.
//

#include "IResample.h"

/**
 * 通知给子类模块
 * @param data
 */
void IResample::update(AVData data) {
    AVData d = this->resample(data);
    if (d.size > 0) {
        this->send(d);
    }
}
