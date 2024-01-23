[reference/lzss.c](lzss-c/reference/lzss.c) 是网上找的一套lzss算法实现，原地址为 [https://oku.edu.mie-u.ac.jp/~okumura/compression/lzss.c](https://oku.edu.mie-u.ac.jp/~okumura/compression/lzss.c)

但无法直接用于自己的场景：

1. [reference/lzss.c](lzss-c/reference/lzss.c) 是对文件压缩和解压操作，而我的需求是直接压缩和解压字节数组
2. [reference/lzss.c](lzss-c/reference/lzss.c) 用到了全局变量，无法同一时间调用多次

基于以上两点，改造成了适合自己的 [myimplement/lzss.c](lzss-c/myimplement/lzss.c)

1. 支持基于十六进制字符串直接压缩和解压
2. 补充了单元测试，clone代码之后可直接查看算法效果

   2.1 [test/reference_lzss_test.c](lzss-c/test/reference_lzss_test.c)

   2.2 [test/myimplement_lzss_test.c](lzss-c/test/myimplement_lzss_test.c)

   2.3 [test/byte_tool_test.c](lzss-c/test/byte_tool_test.c)

另外，最近补充了 [Java 实现的 LZSS 压缩算法](lzss-java)
