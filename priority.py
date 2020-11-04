
class Priority:

    pri=[[-1,1,1,1,-1],
         [-1,-1,1,1,-1],
         [-1,-1,10,10,-1],
         [1,1,1,1,0],
         [-1,-1,10,10,-1]]

    def __init__(self,input):
        self.file_in=open(input,"r")
        self.list=[]

    def priority_func(self):
        char=self.file_in.read(1)
        top=0
        while char!='\r' and char!='\t':
            if (self.Vt(char) != 0 ):
                if(top==0):
                    print('I' + char)
                    self.list.append(char)
                else:
                   if(self.judge(top,char)==-1):
                       return
            else:
                print('RE')
                return
            char = self.file_in.read(1)

        self.file_in.close()

    def priority_judge(self,a,b):
        return self.pri[self.Vt(a)][self.Vt(b)]

    def judge(self,top,char):
        left=top
        while (self.Vt(self.list[left]) == 0):
            left-=1
            if(left<0):
                top += 1
                print('I' + char)
                self.list.append(char)
                return 1  # 入栈继续下一个
        ch=self.list[left]
        judge=self.priority_judge(ch,char)
        if(judge==10):
            return -1 #解析失败
        elif(judge==0 or judge==1):
            top+=1
            print('I'+char)
            self.list.append(char)
            return 1 #入栈继续下一个
        else:
            if(ch=='+' or ch=='*'):
                sta=self.Statute(left-1,top)
                if(sta!=0):
                    del self.list[left-1:top+1]
                    self.list.append(sta)
                    top=left-2
                    print('R')
                    return self.judge(top, char) #规约成功，继续规约
                else:
                    sta=self.Statute(top,top)
                    if(sta!=0):
                        del self.list[top:top + 1]
                        self.list.append(sta)
                        print('R')
                        return self.judge(top, char)  # 规约成功，继续规约
                    else:
                        return -1 #规约失败
            elif(ch==')'):
                sta = self.Statute(left - 2, top)
                if (sta != 0):
                    del self.list[left - 2:top + 1]
                    self.list.append(sta)
                    top = left - 2
                    print('R')
                    return self.judge(top, char)  # 规约成功，继续规约
                else:
                    sta=self.Statute(left - 1,left - 1)
                    if(sta!=0):
                        self.list[left-1]=sta
                        print('R')
                        return self.judge(top, char)  # 规约成功，继续规约
                    else:
                        return -1 #规约失败

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

    def Vn(self,char):
        if char=='E':
            return 1
        elif char=='T':
            return 2
        elif char=='F':
            return 3
        else:
            return 0

    def Statute(self,left,top):
        if(left<0 or top<0):
            return 0 #没匹配上

        str=""
        for i in range(left,top+1):
            str=str+list[i]
        if(str=="E+T" or str=="T"):
            return 'E'
        elif(str=="T*F" or str=="F"):
            return 'T'
        elif(str=="(E)" or str=='i'):
            return 'F'
        else:
            return 0 #没有匹配上



