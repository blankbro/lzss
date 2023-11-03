[reference/lzss.c](reference/lzss.c) 是网上找的一套lzss算法实现，原地址为 [https://oku.edu.mie-u.ac.jp/~okumura/compression/lzss.c](https://oku.edu.mie-u.ac.jp/~okumura/compression/lzss.c)

但无法直接用于自己的场景，有以下缺陷：

1. [reference/lzss.c](reference/lzss.c) 是对文件压缩和解压操作，而我的需求是直接压缩和解压字节数组
2. [reference/lzss.c](reference/lzss.c) 用到了全局变量，无法同一时间调用多次

基于以上两点，改造之后的项目为 [myimplement/lzss.c](myimplement/lzss.c) 有以下优点：

1. 支持基于十六进制字符串直接压缩和解压
2. 补充了单元测试，clone代码之后可直接查看算法效果

   2.1 [test/reference_lzss_test.c](test/reference_lzss_test.c)

   2.2 [test/myimplement_lzss_test.c](test/myimplement_lzss_test.c)

   2.3 [test/byte_tool_test.c](test/byte_tool_test.c)