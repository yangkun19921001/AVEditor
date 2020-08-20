//
// Created by 阳坤 on 2020-08-20.
//

#include "IMuxer.h"


static void ReleaseCallback(AVData *data) {
    if (data)
        data->drop();
}

double IMuxer::r2d(AVRational r) {
    return r.num == 0 || r.den == 0 ? 0. : (double) r.num / (double) r.den;
}


void IMuxer::main() {
    while (!isExit) {
        if (isPause()) {
            sleep();
            continue;
        }
        AVData avData;
        streamLists.pop(avData);
        dequeue(avData);
    }
}

int IMuxer::enqueue(AVData avData) {
    streamLists.push(avData);
    return 0;
}

void IMuxer::clear() {
    streamLists.clearQueue();
    streamLists.setFlag(0);
}

int IMuxer::start() {
    streamLists.setFlag(1);
    int ret = IThread::start();
    streamLists.setReleaseCallback(ReleaseCallback);
    return ret;
}


