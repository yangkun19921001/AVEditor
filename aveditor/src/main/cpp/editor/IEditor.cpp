//
// Created by 阳坤 on 2020-08-31.
//

#include "IEditor.h"


int IEditor::open(const char*url,deque<MediaEntity *> lists) {
    this->medialists = lists;
    return 1;
}


int IEditor::start() {
    return IThread::start();
}

int IEditor::close() {
    if (medialists.size() > 0) {
        while (!medialists.empty()){
            MediaEntity *media =   medialists.front();
            LOGD("clear path:%s \n",media->path);
            delete[] media->path;
            delete media;
            medialists.pop_front();
            media->path = NULL;

        }
    }
    return 0;
}


