# ABus

此总线主要包含两种通信方式——同步通信和异步通信

异步通信的主要过程为:
调用subscribe方法订阅事件，调用post方法发送事件

同步通信的主要过程为:
调用get/set方法获取/设置数据，调用registerServer方法实现获取/设置数据的方法