//
// Created by 阳坤 on 2020-08-21.
//


#include "SoundTouchUtils.h"

void
SoundTouchUtils::initSpeedController(int channels, int sampleingRate, double tempo, double pitchSemi) {
    soundTouch = new SoundTouch();
    soundTouch->setSampleRate(sampleingRate);
    soundTouch->setChannels(channels);
    soundTouch->setPitch(pitchSemi);
    soundTouch->setTempo(tempo);
    isExit = false;
}


int SoundTouchUtils::putData(uint8_t *input, int size) {
    if (size > 0 && input) {
        soundTouch->putSamples(reinterpret_cast<const SAMPLETYPE *>(input),
                               size / AUDIO_SAMPLE_FORMAT_16BIT / soundTouch->numChannels());
    }
    return size;
}


void SoundTouchUtils::close() {
    isExit = true;
    if (!soundTouch)
        return;
    soundTouch->flush();
    soundTouch->clear();
    soundTouch = 0;
}

/**
 * 动态设置速率
 * @param speed
 */
void SoundTouchUtils::setSpeed(double speed) {
    if (soundTouch && speed >= 0.0 && speed <= 3.0)
        soundTouch->setTempo(speed);
}

int SoundTouchUtils::getData(short **out, int size) {
    int num = 0;
    num = soundTouch->receiveSamples(*out,
                                     size / AUDIO_SAMPLE_FORMAT_16BIT / soundTouch->numChannels());
    return num * 2 * soundTouch->numChannels();
}




