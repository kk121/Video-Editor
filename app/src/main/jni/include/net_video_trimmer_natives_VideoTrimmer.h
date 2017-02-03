#include <jni.h>

#ifndef _Included_net_video_trimmer_natives_VideoTrimmer
#define _Included_net_video_trimmer_natives_VideoTrimmer
#ifdef __cplusplus
extern "C" {
#endif

JNIEXPORT jint JNICALL Java_net_video_trimmer_natives_VideoTrimmer_trim
  (JNIEnv *, jclass, jstring, jstring, jint, jint);

#ifdef __cplusplus
}
#endif
#endif
