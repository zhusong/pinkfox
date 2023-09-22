<div align="center">

![Language](https://img.shields.io/badge/language-java-brightgreen)
![Documentation](https://img.shields.io/badge/documentation-yes-brightgreen)
![license](https://img.shields.io/badge/license-MIT-brightgreen)
![jdk](https://img.shields.io/badge/JDK-8-brightgreen)
</div>

##### Translate to: [English](README_en.md)

## 背景

工作中：

- 有时候会为了查一个问题而浪费一下午的时间。 
- 有时候会看见同事为了排查某个bug在公司彻夜未眠。
- 有时候写的代码看起来明明没问题的，但就是不work，从而抓狂愤怒。
- 有时候生产出问题，因不能快速定位问题，而导致修复时间被大大拉长，订单损失越来越大，等等原因。

为了应对以上问题，一个轻量级的web断点调试能力对于我们Java开发者来说非常有必要，因为有时候：

### 依赖的环境复杂，只能预发调试
因为一个项目，
往往依赖的环境和配置比较复杂，所以经常是没有本地环境可供调试的，而为了能进行自测、联调、测试，通常是在预发环境上进行。

### 日志的规范考验实力
而当出现需要调试或者排查问题时，以传统的日志方式诚然也能解决问题，但这比较繁琐，尤其是日志的打印规范非常考验程序员的经验和素质。
当一个项目经过多次交手后，在对老业务进行问题诊断时，称得上是两眼一抹黑，无奈也只能加日志、打包、上线、重新请求、查看日志、找出问题，最终浪费生命。

### 传统IDE配置繁琐
传统IDE集成的debug能力开启，需要配置开通端口，然后本地服务器启动，再进入调试，比较繁琐，那么为了解决这些问题， 正是pinkfox出现的主要原因。

## 关于 pinkfox
一种无需开通端口，项目正常编译部署后，直接${ip}/pinkfox.html访问即可在web端进行debug，并且界面足够简洁，依赖足够轻量。 帮助研发从需求的生命周期中从开发-->自测-->联调-->测试-->上线-->上线后排查问题的整个过程提效。

![gif.gif](docs/pinkfox.gif)

## pinkfox 特点

一种无需额外开通端口就能让开发人员在Web端对预发、甚至是生产环境的Java代码进行调试的轻量级debug工具。
 
- 良好的界面交互，点击页面代码行添加/取消断点，拥有良好的断点追踪能力。
- 支持基础的debug能力，如step over（下一步）、step into（进入方法）、step out（退出方法）、resume（释放请求）、flush（冲刷）、stop（停止）等能力。
- 无需本地启动、拉取代码，无需IDE环境，直接就在web端对源码进行断点。 
- 支持maven路径树展示。
- 支持一键禁用全部断点、清除所有断点。    
- 无需额外开通端口，通过浏览器即可访问和使用。
- 避免本地与服务器代码不一致问题。
- 编译环节做到预发、线上环境完全隔离，在maven compile阶段加入-Dpinkfox=true即可生效Debug能力。不加-Dpinkfox=true无断点能力，业务完全不受影响（同不加maven dependency）。
- 支持>、<、>=、<=、==等条件表达式断点。
- 支持目标类文件搜索触达。

  
## 开始使用（3个步骤）

### 1、添加maven依赖
```xml
<dependency>
    <groupId>co.xiaowangzi</groupId>
    <artifactId>pinkfox</artifactId>
    <version>${version}</version>
</dependency>
```
### 2、在maven compile命令中增加-Dpinkfox=true，例如：
```text
mvn clean -U install -Dmaven.test.skip=true -Dpinkfox=true
```

### 3、访问${ip}/pinkfox.html

## 注意事项
1、依赖本包后，本地起服务如果报错：
```text
com.sun.proxy.$Proxy8 cannot be cast to com.sun.tools.javac.processing.JavacProcessingEnvironment
```
解决办法：

打开，例如IDEA的settings–>Build, Execution, deployment–>Compiler，在Shared build process VM opyions中添加：
```text
-Djps.track.ap.dependencies=false
```

2、如果访问pinkfox.html被sso拦截，请在excludePath中排除/路径。

3、注意：

- 目前只支持java8，tomcat7及以上。
- 不加-Dpinkfox=true无断点能力，业务完全不受影响（同不加maven dependency）。
- 不支持对依赖的第三方Jar内进行断点调试，比如JDK源码、spring源码等。
- 支持多线程环境下的断点调试。
- 支持内部类断点。  
- 如果生产环境生效了断点能力，虽然在没有设置任何断点的情况下，IO密集型项目性能仍有5%~10%的损耗，CPU密集型性能损耗尤甚，但不影响业务流程的完整性，请自行判断是否给生产环境使用。
- 在一个类超过2000行时，调试页面性能会下降一点（依赖浏览器渲染）。
- lambda内断点不支持。

## 社区
- [微信群]
- [QQ群]

## 协议
[MIT license](./LICENSE).
