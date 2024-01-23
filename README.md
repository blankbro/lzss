## LZSS 算法实现

#### [参考项目](lzss-c/reference/lzss.c) 

所有实现的参考项目, 源代码来自 [https://oku.edu.mie-u.ac.jp/~okumura/compression/lzss.c](https://oku.edu.mie-u.ac.jp/~okumura/compression/lzss.c)

- 实现了文件的压缩和解压

#### [参考项目-单元测试](lzss-c/test/reference_lzss_test.c)

为参考项目添加的单元测试

#### [我的C语言实现](lzss-c/myimplement/lzss.c)

基于参考项目稍微做了一些改造：

- 直接对字节数组进行压缩和解压，而不是文件
- 允许方法同一时间多次调用：移除共享变量，新增 EncodeBuffer、DecodeBuffer 等结构

#### [我的C语言实现-单元测试](lzss-c/test/myimplement_lzss_test.c)

- 新增十六进制字符串和字节数组相互转换

#### [我的Java实现](lzss-java/src/main/java/io/github/blankbro/lzss/Lzss.java)

基于C语言实现改造成了Java实现

- 添加了非常详细的注释
- 重命名变量名，见名知义
