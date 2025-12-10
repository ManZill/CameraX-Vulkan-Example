cmake_minimum_required(VERSION 3.22.1)

project("vulkan-camera" C)

add_library(
    vulkan-camera
        SHARED
        main.c
)

target_link_options(
        vulkan-camera
        PRIVATE
        "-Wl,-z,max-page-size=16384"
)

find_library(log-lib log)
find_library(android-lib android)

target_link_libraries(
        vulkan-camera
        ${log-lib}
        ${android-lib}
        vulkan
)
