# PushAPK-Binder
一个集成了小米push，使用binder通讯将消息传递给linux进程的apk

## Android进程间通信-Binder

## 简介

本文项目JAVA层和C层通过Binder完整且稳定通信的方法。

首先介绍了Binder通信的基本原理，然后通过需求切入，提供了两种通信方案（即java层注册service和c层注册service）然后根据实际情况，实现方案。并记录了实现过程中出现的问题和解决思路。

即service端产生Binder服务。通过serviceManager注册至系统内核. client端通过serviceManager获取Binder服务（实际是代理对象）完成通信。

文章地址：[https://cuizehui.github.io/2018/07/07/responsive-android/](Android进程间通信-Binder)
