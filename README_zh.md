##### Translate to: [English](README.md)

## 关于 PinkFox

一种无需额外开通端口就能让开发人员在Web端对预发、甚至是生产环境的Java代码进行调试的轻量级debug工具。它不仅支持基础的debug能力如step over、step into、step out、resume、flush等；还支持自主实现的高级能力， 比如Hotswap(热替换)等。
 
    - 支持基础的debug能力，如step over（下一步）、step into（进入方法）、step out（退出方法）、resume（释放请求）、flush（冲刷）、stop（停止）等能力。
    - 无需拉取代码，直接就能在web端获取源码，支持maven路径树展示。
    - 无需额外开通端口，通过浏览器即可访问和使用。
    - 避免了本地与服务器代码不一致问题，而且无需依赖IDE环境即可调试。
    - 编译环节做到预发、线上环境完全隔离，在maven compile阶段加入-DpinkFox=true即可生效Debug能力。
    - 支持>、<、>=、<=、==等表达式。
    - 支持目标类文件搜索触达。
    - 支持