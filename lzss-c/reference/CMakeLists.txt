# 把一个目录下所有的源代码文件名，赋值给变量SOURCE_NAMES
aux_source_directory(./ SOURCE_NAMES)

# 生成静态库reference
add_library(reference_lib STATIC ${SOURCE_NAMES})