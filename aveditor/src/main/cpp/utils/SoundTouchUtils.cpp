//
// Created by 阳坤 on 2020-08-21.
//


#include "SoundTouchUtils.h"

void
SoundTouchUtils::initSpeedController(unsigned char i, int channels, int sampleingRate, double tempo, double pitchSemi) {
    if (NULL == soundTouch[i])
        soundTouch[i] = new SoundTouch();
    soundTouch[i]->setSampleRate(sampleingRate);
    soundTouch[i]->setChannels(channels);
    soundTouch[i]->setPitch(pitchSemi);
    soundTouch[i]->setTempo(tempo);
    isExit = false;
}


int SoundTouchUtils::putData(unsigned char i, uint8_t *input, int size) {
    if (size > 0 && input) {
        soundTouch[i]->putSamples(reinterpret_cast<const SAMPLETYPE *>(input),
                                  size / AUDIO_SAMPLE_FORMAT_16BIT / soundTouch[i]->numChannels());
    }
    return size;
}


void SoundTouchUtils::close(unsigned char i) {
    isExit = true;
    if (!soundTouch[i])
        return;
    soundTouch[i]->flush();
    soundTouch[i]->clear();
    soundTouch[i] = 0;
}

/**
 * 动态设置速率
 * @param speed
 */
void SoundTouchUtils::setSpeed(unsigned char i, double speed) {
    if (soundTouch[i] && speed >= 0.0 && speed <= 3.0)
        soundTouch[i]->setTempo(speed);
}

int SoundTouchUtils::getData(unsigned char i, short **out, int size) {
    int num = 0;
    num = soundTouch[i]->receiveSamples(*out,
                                        size / AUDIO_SAMPLE_FORMAT_16BIT / soundTouch[i]->numChannels());
    return num * 2 * soundTouch[i]->numChannels();
}




