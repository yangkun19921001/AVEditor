#ifndef _MEDIA_OPENSL_ES_CONTEXT_H_
#define _MEDIA_OPENSL_ES_CONTEXT_H_

#include "opensl_es_util.h"
#include "../libcommon/CommonTools.h"

class OpenSLESContext {
private:
	SLObjectItf engineObject;
	SLEngineItf engineEngine;
	bool isInited;
	/**
	 * Creates an OpenSL ES engine.
	 */
	SLresult createEngine() {
		// OpenSL ES for Android is designed to be thread-safe,
		// so this option request will be ignored, but it will
		// make the source code portable to other platforms.
		SLEngineOption engineOptions[] = { { (SLuint32) SL_ENGINEOPTION_THREADSAFE, (SLuint32) SL_BOOLEAN_TRUE } };

		// Create the OpenSL ES engine object
		return slCreateEngine(&engineObject, ARRAY_LEN(engineOptions), engineOptions, 0, // no interfaces
				0, // no interfaces
				0); // no required
	};
	/**
	 * Realize the given object. Objects needs to be
	 * realized before using them.
	 * @param object object instance.
	 */
	SLresult RealizeObject(SLObjectItf object) {
		// Realize the engine object
		return (*object)->Realize(object, SL_BOOLEAN_FALSE); // No async, blocking call
	};
	/**
	 * Gets the engine interface from the given engine object
	 * in order to create other objects from the engine.
	 */
	SLresult GetEngineInterface() {
		// Get the engine interface
		return (*engineObject)->GetInterface(engineObject, SL_IID_ENGINE, &engineEngine);
	};

	OpenSLESContext();
	void init();
	static OpenSLESContext* instance;
public:
	static OpenSLESContext* GetInstance(); //工厂方法(用来获得实例)
	virtual ~OpenSLESContext();
	SLEngineItf getEngine() {
		return engineEngine;
	};
};
#endif	//_MEDIA_OPENSL_ES_CONTEXT_H_
