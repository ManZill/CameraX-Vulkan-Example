#include <jni.h>

#include "logger.h"



JNIEXPORT void JNICALL
Java_com_manzill_example_camxvk_core_camera_VulkanCameraEngine_setSurface(
        JNIEnv *env, jobject thiz, jobject surface, jint width, jint height
)
{
    LOGD("setSurface: width=%d, height=%d", width, height);
    // TODO: implement setSurface()
}

JNIEXPORT void JNICALL
Java_com_manzill_example_camxvk_core_camera_VulkanCameraEngine_initialize(
        JNIEnv *env, jobject thiz
)
{
    LOGD("initialize");
}
