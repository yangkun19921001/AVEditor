//
// Created by 阳坤 on 2020-08-13.
//

#include "ITransfer.h"

void ITransfer::update(AVData data) {

    //暂停就不传输了
    if (isPause()){
        return;
    }
    //将数据发送给子类
    onData(data);
}






