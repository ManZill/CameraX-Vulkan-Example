#include <jni.h>
#include <android/native_window_jni.h>
#include <android/hardware_buffer_jni.h>

#if defined(__ANDROID__) || defined(ANDROID)
#define VK_USE_PLATFORM_ANDROID_KHR
#endif

#include <vulkan/vulkan.h>
#include <malloc.h>
#include <string.h>
#include <assert.h>

#include "logger.h"

#ifndef APP_NAME
#define APP_NAME ""
#endif

#ifndef APP_VERSION_MAJOR
#define APP_VERSION_MAJOR 0
#endif

#ifndef APP_VERSION_MINOR
#define APP_VERSION_MINOR 0
#endif

#ifndef APP_VERSION_PATCH
#define APP_VERSION_PATCH 0
#endif

ANativeWindow *g_native_window = NULL;

JNIEXPORT void JNICALL
Java_com_manzill_example_camxvk_core_camera_VulkanCameraEngine_setSurface(
        JNIEnv *env, jobject thiz, jobject surface, jint width, jint height
)
{
    if (surface == NULL)
    {
        ANativeWindow_release(g_native_window);
        g_native_window = NULL;
        return;
    }

    ANativeWindow *native_window = ANativeWindow_fromSurface(env, surface);
    if (native_window != g_native_window)
    {
        if (g_native_window != NULL)
        {
            ANativeWindow_release(g_native_window);
        }
        g_native_window = native_window;
        ANativeWindow_acquire(g_native_window);
    }
}

JNIEXPORT void JNICALL
Java_com_manzill_example_camxvk_core_camera_VulkanCameraEngine_processHardwareBuffer(
        JNIEnv *env, jobject thiz, jobject hardwareBufferObj
)
{
    AHardwareBuffer *buffer = AHardwareBuffer_fromHardwareBuffer(env, hardwareBufferObj);
    if (!buffer)
    {
        LOGD("Failed to get AHardwareBuffer");
        return;
    }

    AHardwareBuffer_Desc desc = { 0 };
    AHardwareBuffer_describe(buffer, &desc);

    VkAndroidHardwareBufferPropertiesANDROID properties = { 0 };
    properties.sType = VK_STRUCTURE_TYPE_ANDROID_HARDWARE_BUFFER_PROPERTIES_ANDROID;
//    vkGetAndroidHardwareBufferPropertiesANDROID();


    // ... //

    AHardwareBuffer_unlock(buffer, NULL);
}

void create_vulkan_device()
{
    VkApplicationInfo application_info = {
            .sType = VK_STRUCTURE_TYPE_APPLICATION_INFO,
            .pNext = NULL,
            .pApplicationName = APP_NAME,
            .applicationVersion = VK_MAKE_VERSION(APP_VERSION_MAJOR, APP_VERSION_MINOR, APP_VERSION_PATCH),
            .pEngineName = NULL,
            .engineVersion = 0,
            .apiVersion = VK_API_VERSION_1_3,
    };

    const char* const validation_layers[] = {
#ifdef NDEBUG
            "VK_LAYER_KHRONOS_validation"
#endif
    };

    const char* const instance_extensions[] = {
        "VK_KHR_surface",
        "VK_KHR_android_surface"
    };


    VkInstanceCreateInfo instance_create_info = {
            .sType = VK_STRUCTURE_TYPE_INSTANCE_CREATE_INFO,
            .pNext = NULL,
            .pApplicationInfo = &application_info,
            .enabledLayerCount  = sizeof(validation_layers) / sizeof(*validation_layers),
            .ppEnabledLayerNames = validation_layers,
            .enabledExtensionCount = sizeof(instance_extensions) / sizeof(*instance_extensions),
            .ppEnabledExtensionNames = instance_extensions,
    };
    // ToDo: Make it global?
    VkInstance *instance = (VkInstance *)malloc(sizeof(VkInstance));
    vkCreateInstance(&instance_create_info, NULL, instance);

    VkAndroidSurfaceCreateInfoKHR surface_create_info = {
            .sType = VK_STRUCTURE_TYPE_ANDROID_SURFACE_CREATE_INFO_KHR,
            .pNext = NULL,
            .flags = 0,
            .window = g_native_window,
    };
    // ToDo: Make it global?
    VkSurfaceKHR *surface = (VkSurfaceKHR *)malloc(sizeof(VkSurfaceKHR));
    vkCreateAndroidSurfaceKHR(*instance, &surface_create_info, NULL, surface);

    VkPhysicalDevice *gpu = (VkPhysicalDevice *)malloc(sizeof(VkPhysicalDevice));
    {
        uint32_t gpuCount = 0;
        vkEnumeratePhysicalDevices(*instance, &gpuCount, NULL);
        VkPhysicalDevice *gpus = (VkPhysicalDevice *)malloc(sizeof(VkPhysicalDevice) * gpuCount);
        vkEnumeratePhysicalDevices(*instance, &gpuCount, gpus);
        memcpy(gpu, gpus, sizeof(VkPhysicalDevice));
        free(gpus);
    }

    VkPhysicalDeviceMemoryProperties *gpu_memory_properties = (VkPhysicalDeviceMemoryProperties *)malloc(sizeof(VkPhysicalDeviceMemoryProperties));
    vkGetPhysicalDeviceMemoryProperties(*gpu, gpu_memory_properties);

    uint32_t *queue_family_index = (uint32_t *)malloc(sizeof(uint32_t));
    {
        uint32_t queue_family_count = 0;
        vkGetPhysicalDeviceQueueFamilyProperties(*gpu, &queue_family_count, NULL);
        assert(queue_family_count != 0);
        VkQueueFamilyProperties *queue_families = (VkQueueFamilyProperties *)malloc(sizeof(VkQueueFamilyProperties) * queue_family_count);
        vkGetPhysicalDeviceQueueFamilyProperties(*gpu, &queue_family_count, queue_families);

        for (*queue_family_index = 0; *queue_family_index < queue_family_count; (*queue_family_index)++)
        {
            if (queue_families[*queue_family_index].queueFlags & VK_QUEUE_GRAPHICS_BIT)
            {
                break;
            }
        }
        assert(*queue_family_index < queue_family_count);

        free(queue_families);
    }

    float priorities[] = { 1.0f };

    VkDeviceQueueCreateInfo queue_create_info = {
            .sType = VK_STRUCTURE_TYPE_DEVICE_QUEUE_CREATE_INFO,
            .pNext = NULL,
            .flags = 0,
            .queueFamilyIndex = *queue_family_index,
            .queueCount = 1,
            .pQueuePriorities = priorities,
    };

    VkDeviceCreateInfo device_create_info = {
            .sType = VK_STRUCTURE_TYPE_DEVICE_CREATE_INFO,
            .pNext = NULL,
            .flags = 0,
            .queueCreateInfoCount = 1,
            .pQueueCreateInfos = &queue_create_info,
            .enabledLayerCount = 0,
            .ppEnabledLayerNames = NULL,
            .enabledExtensionCount = sizeof(instance_extensions) / sizeof(*instance_extensions),
            .ppEnabledExtensionNames = instance_extensions,
            .pEnabledFeatures = NULL,
    };

    VkDevice *device = (VkDevice *)malloc(sizeof(VkDevice));
    vkCreateDevice(*gpu, &device_create_info, NULL, device);
    vkGetDeviceQueue(*device, /*queue_family_index*/ 0, 0, NULL);
}



JNIEXPORT void JNICALL
Java_com_manzill_example_camxvk_core_camera_VulkanCameraEngine_initialize(
        JNIEnv *env, jobject thiz
)
{
    LOGD("initialize");
}
