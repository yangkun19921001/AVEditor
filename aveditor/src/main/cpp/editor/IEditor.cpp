//
// Created by 阳坤 on 2020-08-31.
//

#include "IEditor.h"


int IEditor::open(const char*url,deque<MediaEntity *> lists) {
    this->outPath = new char[strlen(url) + 1];
    strcpy(this->outPath, url);
    return 1;
}


int IEditor::start() {
    return IThread::start();
}





