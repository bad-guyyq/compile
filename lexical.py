#!/usr/bin/python
# -*- coding: UTF-8 -*-
class Lexical:

    def __init__(self,input):
        self.f_in=open(input,"r")

    def lexical_func(self):
        char=self.GetNBC()
        while char!='':
            TOKEN=char
            if char.isalpha():#字母
                char = self.f_in.read(1)
                while char.isalnum():#字母或数字
                    TOKEN+=char
                    char = self.f_in.read(1)
                self.Ungetch()
                if self.Reserve(TOKEN)!='':
                    print (self.Reserve(TOKEN))
                else:
                    print ('Ident('+TOKEN+')')
            elif char.isdigit():
                while char.isdigit():
                    TOKEN+=char
                    char = self.f_in.read(1)
                self.Ungetch()
                print ('Int('+TOKEN+')')
            elif char=='+':
                print('Plus')
            elif char=='*':
                print('Star')
            elif char==',':
                print('Comma')
            elif char=='(':
                print('LParenthesis')
            elif char==')':
                print('RParenthesis')
            elif char==':':
                char =self.f_in.read(1)
                if char=='=':
                    print('Assign')
                else:
                    self.Ungetch()
                    print('Colon')
            elif char=='\n' or char=='\t':
                pass
            else:
                print('Unknown')
                break
            char = self.GetNBC()
        self.f_in.close()

    def GetNBC(self):
        char = self.f_in.read(1)
        while char==' ':
            char = self.f_in.read(1)
        return char

    def Ungetch(self):
        self.f_in.seek(self.f_in.tell()-1,0)

    def Reserve(self,token):
        if token=='BEGIN':
            return 'Begin'
        elif token=='END':
            return 'End'
        elif token=='FOR':
            return 'For'
        elif token=='THEN':
            return 'Then'
        elif token=='ELSE':
            return 'Else'
        else:
            return ''


