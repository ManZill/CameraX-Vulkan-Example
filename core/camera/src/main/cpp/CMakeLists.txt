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

target_compile_options(
        vulkan-camera
        PRIVATE
        $<$<CONFIG:Debug>:-g>
        "-fmacro-prefix-map=${CMAKE_CURRENT_SOURCE_DIR}/="
)

find_library(log-lib log)
find_library(android-lib android)
find_library(vulkan-lib vulkan)

target_link_libraries(
        vulkan-camera
        ${log-lib}
        ${android-lib}
        ${vulkan-lib}
)
