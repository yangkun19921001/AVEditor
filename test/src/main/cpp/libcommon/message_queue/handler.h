#ifndef VIDEO_COMMON_HANDLER_H
#define VIDEO_COMMON_HANDLER_H

#include "../libcommon/CommonTools.h"
#include "./message_queue.h"

class Handler {
private:
	MessageQueue* mQueue;

public:
	Handler(MessageQueue* mQueue);
	~Handler();

	int postMessage(Message* msg);
	virtual void handleMessage(Message* msg){};
};

#endif // VIDEO_COMMON_HANDLER_H
