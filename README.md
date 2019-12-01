# Au
[![Maven Central](https://img.shields.io/maven-central/v/com.lazycece.au/au-core)](https://search.maven.org/search?q=au-core)
[![License](https://img.shields.io/github/license/lazycece/au)](http://opensource.org/licenses/MIT)
[![GitHub release](https://img.shields.io/badge/release-download-orange.svg)](https://github.com/lazycece/au/releases)

`Au`是一个`filter`框架，它是在`servlet-filter`的基础上进行扩展，提供拦截器行为式的前置过滤与后置过滤，同时亦提供了对`request`的多次读取，以及对`response`的包装。

## Quick Start

完整的使用案例可查看[au-example](https://github.com/lazycece/au/tree/master/au-example)

### Maven dependency

```xml
<dependency>
  <groupId>com.lazycece.au</groupId>
  <artifactId>au-core</artifactId>
  <version>1.0.0</version>
</dependency>
```

### Filter

定义`filter`，并且实现`AuFilter`:

```java
public class SimpleAuFilter implements AuFilter {

    @Override
    public String name() {
        return "simple-filter";
    }

    @Override
    public boolean preHandle() throws Exception {
        // pre handle
        return true;
    }

    @Override
    public void postHandle() throws Exception {
        // post-handle
    }
}
```

注入`AuFilter`，同时可设置过滤器的相关策略：

```
    AuManager auManager = AuManager.getInstance();
    auManager.addAuFilter(SimpleAuFilter.class)
            .excludePatterns("/a/b")
            .includePatterns("/**")
            .order(1);
    auManager.setWrapper(true);
```

### Enable Au

将`AuServletFilter`注入到`Servlet`中，下面以在`jetty`中使用为例：

```
        ServletContextHandler contextHandler = new ServletContextHandler();
        contextHandler.setContextPath("/au");
        server.setHandler(contextHandler);
        contextHandler.addServlet(ExampleServlet.class, "/*");
        contextHandler.addFilter(AuServletFilter.class, "/*", EnumSet.of(DispatcherType.REQUEST));
```

## License

[MIT](https://github.com/lazycece/au/blob/master/LICENSE)
 