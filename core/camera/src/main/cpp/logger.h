//
// Created by ManZill on 10.12.2025.
//

#ifndef CAMERAX_VULKAN_EXAMPLE_LOGGER_H
#define CAMERAX_VULKAN_EXAMPLE_LOGGER_H

#include <android/log.h>

#define LOG_TAG "NativeCameraVulkan"

#define LOGD(format, ...) __android_log_print( \
    ANDROID_LOG_DEBUG,                         \
    LOG_TAG,                                   \
    "[%s][%d][%s] " format,                    \
    __FILE__, __LINE__, __func__,              \
    ##__VA_ARGS__                              \
)

#endif //CAMERAX_VULKAN_EXAMPLE_LOGGER_H
