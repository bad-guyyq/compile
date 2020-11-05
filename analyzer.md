# 算符优先分析法

#### 题目

编程实现以下文法 `G[E]` 的算符优先分析法：

```none
E -> E '+' T | T
T -> T '*' F | F
F -> '(' E ')' | 'i'      E->T->F->i

E -> E+T|T   E+F|E+i En

E -> E+E|E*E|(E)|i
```

|      |  +   |  *   |  i   |  (   |  )   |
| :--: | :--: | :--: | :--: | :--: | :--: |
|  +   | >-1  |  <1  |  <   |  <   |  >   |
|  *   |  >   |  >   |  <   |  <   |  >   |
|  i   |  >   |  >   |      | 1000 |  >   |
|  (   |  <   |  <   |  <   |  <   |  =0  |
|  )   |  >   |  >   |      |      |  >   |

python test.py "input.txt"

cd E:\python\python3.7\

E:

python -version

E:\python3.8\python.exe D:/py/Priority/test "input.txt"



























i
