import pymouse,pykeyboard,os,sys
from pywinauto import application
from pykeyboard import PyKeyboard
from pymouse import *

import time
m = PyMouse() 
k = PyKeyboard()
print('hello world')
#鼠标操作： 
#m.click(x,y,button,n) –鼠标点击 -button:-1表示左键,2表示点击右键,n 点击次数
# m.move(x,y) –鼠标移动到坐标(x,y) 
#x_dim, y_dim = m.screen_size()  # –获得屏幕尺寸
class Pywin(object):
    '''
    pywin framwork main class
    tool_name : 程序名称，支持带路径
    windows_name : 窗口名字
    '''
    SLEEP_TIME = 1
    def __init__(self):
        '''
        初始化方法，初始化一个app
        '''
        self.app = application.Application()
    def run(self,tool_name):
        ''''
        启动应用程序
        '''
        self.app.start(tool_name)
        time.sleep(self.SLEEP_TIME)
    def connect(self,window_name):
        '''
        连接应用程序
        '''
        #app.connect_(path = r"c:\windows\system32\notepad.exe")
        #app.connect_(process = 2341)
        #app.connect_(handle = 0x010f0c)
        self.app.connect(title = window_name)
        time.sleep(self.SLEEP_TIME)
    def close(self,window_name):
        '''
        关闭应用程序
        '''
        self.app[window_name].Close()
        time.sleep(self.SLEEP_TIME)
    def max_window(self, window_name):
        '''
        最大化窗口
        '''
        self.app[window_name].Maximize()
        time.sleep(self.SLEEP_TIME)
    def menu_click(self, window_name, menulist):
        '''
        菜单点击
        '''
        self.app[window_name].MenuSelect(menulist)
        time.sleep(self.SLEEP_TIME)
    def input(self, window_name, controller, content):
        '''
        输入内容
        '''
        self.app[window_name][controller].TypeKeys(content)
        time.sleep(self.SLEEP_TIME)
    def click(self, window_name, controller):
        '''
        鼠标左键点击
        example:
        下面两个功能相同,下面支持正则表达式
        app[u'关于“记事本”'][u'确定'].Click()
        app.window_(title_re = u'关于“记事本”').window_(title_re = u'确定').Click()
        '''
        self.app[window_name][controller].Click()
        time.sleep(self.SLEEP_TIME)
    def double_click(self, window_name, controller, x = 0,y = 0):
        '''
        鼠标左键点击(双击)
        '''
        self.app[window_name][controller].DoubleClick(button = "left", pressed = "",  coords = (x, y))
        time.sleep(self.SLEEP_TIME)
    def right_click(self, window_name, controller, order):
        '''
        鼠标右键点击，下移进行菜单选择
        window_name : 窗口名
        controller：区域名
        order ： 数字，第几个命令
        '''
        self.app[window_name][controller].RightClick()
        k = PyKeyboard()
        for down in range(order):
            k.press_key(k.down_key)
            time.sleep(0.5)
        k.press_key(k.enter_key)
        time.sleep(self.SLEEP_TIME)

if __name__ ==  "__main__":
    app = Pywin()
    # 记事本例子
    tool_name = "notepad.exe"
    # 通过Spy++ 获取window_name，即标题文本
    window_name = u"无标题 - 记事本"
    menulist = u"帮助->关于记事本"
    # 通过Spy++ 获取controller，即窗口类名
    controller = "Edit"
    content = u"johnny"
    window_name_new = content + ".txt"
    # 启动程序，记事本只能开一个
    app.run(tool_name)
    app.connect(window_name)
    # app.max_window(window_name)  
    app.menu_click(window_name,menulist)
    app.click(u'关于记事本', u'确定')   #点击关于记事本窗口上的确定按钮
    app.input(window_name,controller,content)
    # Ctrl + a 全选
    app.input(window_name,controller,"^a")
    # 选择复制
    app.right_click(window_name,controller,3)
    #选择粘贴
    app.right_click(window_name,controller,4)
    k=PyKeyboard()
    k.press_key(k.enter_key)
    time.sleep(1)
    # Ctrl + v 粘贴
    app.input(window_name,controller,"^v")
    # Ctrl + s 保存
    app.input(window_name,controller,"^s")
    # 输入文件名
    app.input(u"另存为",controller,content)
    # 保存
    app.click(u"另存为","Button")
    try:
        app.click(u"确认另存为","Button")
    except:
        pass
    finally:
        app.close(window_name_new)

