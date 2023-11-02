# 向工程添加多个特定的头文件搜索路径
include_directories(${PROJECT_SOURCE_DIR}/myimplement)
include_directories(${PROJECT_SOURCE_DIR}/reference)


# reference lzss 测试程序
add_executable(reference_lzss_test reference_lzss_test.c)
target_link_libraries(reference_lzss_test reference_lib)


# reference lzss 测试程序
add_executable(myimplement_lzss_test myimplement_lzss_test.c)
target_link_libraries(myimplement_lzss_test myimplement_lib)
