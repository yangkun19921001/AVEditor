//
// Created by 阳坤 on 2020-08-21.
//

#ifndef IKAVEDIT_SOUNDTOUCHENGINE_H
#define IKAVEDIT_SOUNDTOUCHENGINE_H


#include <soundtouch/include/SoundTouch.h>

using namespace soundtouch;
using namespace std;

struct SoundTouchEngine {
    SoundTouch *sTouch;
    queue<signed char> *fBufferOut;
    int channels;
    int sampleRate;
    float tempoChange;
    int pitchSemi;
    int bytesPerSample;

    SoundTouchEngine() {
        sTouch = new SoundTouch();
        fBufferOut = new queue<signed char>();
    }

    SoundTouchEngine(const SoundTouchEngine &other) {
        sTouch = new SoundTouch();
        fBufferOut = new queue<signed char>();
        this->channels = other.channels;
        this->sampleRate = other.sampleRate;
        this->tempoChange = other.tempoChange;
        this->pitchSemi = other.pitchSemi;
        this->bytesPerSample = other.bytesPerSample;
    }
};


#endif //IKAVEDIT_SOUNDTOUCHENGINE_H
