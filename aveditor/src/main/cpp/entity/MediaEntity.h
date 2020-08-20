//
// Created by 阳坤 on 2020-08-19.
//

#ifndef IKAVEDIT_MEDIAENTITY_H
#define IKAVEDIT_MEDIAENTITY_H


typedef struct {
    const  char *path = 0;
    int64_t startDuration;
    int64_t stopDuration;
} MediaEntity;

#endif //IKAVEDIT_MEDIAENTITY_H
