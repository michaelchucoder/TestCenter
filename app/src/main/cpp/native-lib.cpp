#include <jni.h>
#include <string>

extern "C"
jstring
Java_com_michael_testcenter_MainActivity_stringFromJNI(
        JNIEnv *env,
        jobject /* this */) {
    std::string hello = "Hello stringFromJNI from C++";
    return env->NewStringUTF(hello.c_str());
}
