#include <jni.h>

extern "C"
JNIEXPORT jstring JNICALL
Java_com_popiin_util_NativeLib_getBaseUrl(JNIEnv *env, jobject /* this */, jboolean isDebug) {
    // Define Development and Production URLs
   // const char* devUrl = "https://popiin.com/";
     const char* devUrl = "https://dev.popiin.com/";
    const char* prodUrl = "https://popiin.com/";

    // Return the appropriate URL based on build type
    return env->NewStringUTF(isDebug ? devUrl : prodUrl);
}
