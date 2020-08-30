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


int SoundTouchUtils::soundtouch(uint8_t *input, short **out, int size) {
    int num = 0;
    if (size > 0 && input && *out) {
        /**
         * 因为 ffmpeg 解码出来是 uint8 1个字节，我们需要转换为 short 2 个字节形式
         * 用uint_16 来存储PCM数据，就不需要下面的转换。
         */
        for (int i = 0; i < size / 2 + 1; i++) {
            (*out)[i] = (input[i * 2] | (input[i * 2 + 1] << 8));
        }
        soundTouch->putSamples(reinterpret_cast<const SAMPLETYPE *>(input), size / 2 / soundTouch->numChannels());
    } else {
        close();
    }
    num = soundTouch->receiveSamples(*out,
                                     size / 2 / soundTouch->numChannels());
    return num * 2 * soundTouch->numChannels();
}


void SoundTouchUtils::close() {
    isExit = true;
    if (!soundTouch)
        return;
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




