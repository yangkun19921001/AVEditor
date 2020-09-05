//
// Created by 阳坤 on 2020-08-21.
//

#ifndef IKAVEDIT_SOUNDTOUCHUTILS_H
#define IKAVEDIT_SOUNDTOUCHUTILS_H

#include <jni.h>
#include <string>
#include <stdlib.h>
#include <stdio.h>
#include <vector>
#include <queue>
#include <entity/SoundTouchEngine.h>

#include <pthread.h>

#define AUDIO_SAMPLE_FORMAT_16BIT 2


class SoundTouchUtils {

public:
    static SoundTouchUtils *getInstance(unsigned char i) {
        static SoundTouchUtils px[10];
        return &px[i];
    }

    SoundTouch *soundTouch = 0;
public:

    int finished = true;
    pthread_mutex_t mutexSpeed;
    int isExit= false;

    void initSpeedController(int channels,
                             int sampleingRate,
                             double tempo,
                             double pitchSemi);

    void close();

    void setSpeed(double speed);


    int putData(uint8_t *input,int size);
    int getData(short **out, int size);
};


#endif //IKAVEDIT_SOUNDTOUCHUTILS_H
