
class Priority:

    pri=[[-1,1,1,1,-1],
         [-1,-1,1,1,-1],
         [-1,-1,10,10,-1],
         [1,1,1,1,0],
         [-1,-1,10,10,-1]]

    def __init__(self,input):
        self.file_in=open(input,"r")
        self.list=[]
        self.top=-1

    def priority_func(self):
        char=self.file_in.read(1)
        while char!='\r' and char!='\n':
            if (self.Vt(char) != 0 ):
                if(self.top==-1):
                    self.top=0
                    print('I' + char)
                    self.list.append(char)
                else:
                   if(self.judge(char)==-1):
                       print("RE")
                       return
            else:
                print('RE')
                return
            char = self.file_in.read(1)
        while(self.top!=0):
            if (self.judge('#') == -1):
                print('RE')
                return
        if(self.list[0]=='i'):
            print("R\n")
        elif(self.list[0]=='E'):
            pass
        else:
            print('RE')
        self.file_in.close()

    def priority_judge(self,a,b):
        if(b=="#"):
            if(a=='(' ):
                return 10
            else:
                return -1
        else:
            return self.pri[self.Vt(a)-1][self.Vt(b)-1]

    def judge(self,char):
        left=self.top
        while (self.Vt(self.list[left]) == 0): #如果是非终结符，往左继续找
            left-=1
            if(left<0): #如果栈内没有终结符，就入栈
                if (char == '#'):
                    return
                self.top += 1
                print('I' + char)
                self.list.append(char)
                return 1  # 入栈继续下一个
        ch=self.list[left]
        judge=self.priority_judge(ch,char)
        if(judge==10):
            return -1 #解析失败
        elif(judge==0 or judge==1):
            if (char == '#'):
                return
            self.top+=1
            print('I'+char)
            self.list.append(char)
            return 1 #入栈继续下一个
        else:
            if(ch=='+' or ch=='*'):
                sta=self.Statute(left-1)
                if(sta!=0):
                    del self.list[left-1:self.top+1]
                    self.list.append(sta)
                    self.top=left-2
                    print('R')
                    return self.judge(char) #规约成功，继续规约
                else:
                    # sta=self.Statute(self.top,self.top)
                    # if(sta!=0):
                    #     del self.list[self.top:self.top + 1]
                    #     self.list.append(sta)
                    #     print('R')
                    #     return self.judge(self.top, char)  # 规约成功，继续规约
                    # else:
                    return -1 #规约失败
            elif(ch==')'):
                sta = self.Statute(left - 2)
                if (sta != 0):
                    del self.list[left - 2:self.top + 1]
                    self.list.append(sta)
                    self.top = left - 2
                    print('R')
                    return self.judge(char)  # 规约成功，继续规约
                else:
                    # sta=self.Statute(left - 1,left - 1)
                    # if(sta!=0):
                    #     self.list[left-1]=sta
                    #     print('R')
                    #     return self.judge(self.top, char)  # 规约成功，继续规约
                    # else:
                    return -1 #规约失败
            elif(ch=='i'):
                sta = self.Statute(left)
                if (sta != 0):
                    del self.list[left:self.top + 1]
                    self.list.append(sta)
                    print('R')
                    return self.judge(char)  # 规约成功，继续规约
                else:
                    return -1  # 规约失败
            else:
                return -1


    def Vt(self,char):
        if char=='+':
            return 1
        elif char=='*':
            return 2
        elif char=='i':
            return 3
        elif char=='(':
            return 4
        elif char==')':
            return 5
        else:
            return 0

    # def Vn(self,char):
    #     if char=='E':
    #         return 1
    #     elif char=='T':
    #         return 2
    #     elif char=='F':
    #         return 3
    #     else:
    #         return 0

    def Statute(self,left):
        if(left<0 or self.top<0):
            return 0 #没匹配上

        str=""
        for i in range(left,self.top+1):
            str=str+self.list[i]
        if(str=="E+E" or str=="E*E" or str=="(E)" or str=='i'):
            return 'E'
        else:
            return 0 #没有匹配上



