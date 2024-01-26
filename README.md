## LZSS 算法

**Lempel–Ziv–Storer–Szymanski**（**LZSS**）是一个[无损数据压缩](https://zh.wikipedia.org/wiki/%E6%97%A0%E6%8D%9F%E6%95%B0%E6%8D%AE%E5%8E%8B%E7%BC%A9)[算法](https://zh.wikipedia.org/wiki/%E7%AE%97%E6%B3%95)，属于[LZ77](https://zh.wikipedia.org/wiki/LZ77%E4%B8%8ELZ78)的派生，1982年由James Storer和[Thomas Szymanski](https://zh.wikipedia.org/w/index.php?title=Thomas_Szymanski&action=edit&redlink=1)创建。LZSS发布于《Journal of the ACM》的“Data compression via textual substitution”。

LZSS是一种字典编码技术。它会尝试以符号字符串替换相同字符串为一个字典位置的引用。

### 例子

压缩前 177字节

```
I am Sam

Sam I am

That Sam-I-am!
That Sam-I-am!
I do not like
that Sam-I-am!

Do you like green eggs and ham?

I do not like them, Sam-I-am.
I do not like green eggs and ham.
```

压缩后 94字节

```
I am Sam

(5,3) (0,4)

That(4,4)-I-am!(19,16)I do not like
t(21,14)
Do you(58,5) green eggs and ham?
(49,14) them,(24,9).(112,15)(93,18).
```

### 实现原理

![LZSS算法原理](https://raw.githubusercontent.com/blankbro/draw.io/master/LZSS算法原理.drawio.png)

LZSS 算法的压缩效果与多个因素有关：

1. 原始数据重复数据越高，压缩效果可能越好，但重复的数据最好能在一个窗口内，否则起不到效果
2. 重复数据的长度越长，压缩效果越好；如果重复数据都是单字节，那基本没有压缩效果
3. 窗口越大更容易找重复数据，但是窗口占的bit也越多

### [参考项目](lzss-c/reference/lzss.c) 

所有实现的参考项目, 源代码来自 [https://oku.edu.mie-u.ac.jp/~okumura/compression/lzss.c](https://oku.edu.mie-u.ac.jp/~okumura/compression/lzss.c)

- 实现了文件的压缩和解压
- 为参考项目添加了[单元测试](lzss-c/test/reference_lzss_test.c)

### [C语言实现](lzss-c/myimplement/lzss.c)

基于参考项目稍微做了一些改造：

- 直接对字节数组进行压缩和解压，而不是文件
- 允许方法同一时间多次调用：移除共享变量，新增 EncodeBuffer、DecodeBuffer 等结构
- [单元测试](lzss-c/test/myimplement_lzss_test.c)新增十六进制字符串和字节数组相互转换

### [Java实现](lzss-java/src/main/java/io/github/blankbro/lzss/Lzss.java)

基于C语言实现，改造成了Java实现

- 添加了部分注释，帮助理解算法逻辑
- 重新为变量命名，起到了见名知义的效果
- [单元测试](lzss-java/src/test/java/io/github/blankbro/lzss/LzssTest.java)

### 参考资料

[LZSS - 维基百科，自由的百科全书 (wikipedia.org)](https://zh.wikipedia.org/wiki/LZSS)