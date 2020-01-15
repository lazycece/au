# Au
[![Maven Central](https://img.shields.io/maven-central/v/com.lazycece.au/au-core)](https://search.maven.org/search?q=au-core)
[![License](https://img.shields.io/badge/license-Apache--2.0-blue)](https://www.apache.org/licenses/LICENSE-2.0.html)
[![GitHub release](https://img.shields.io/badge/release-download-orange.svg)](https://github.com/lazycece/au/releases)

[中文](./README_zh_CN.md)

Au is a filter framework. It is an extension based on servlet-filter. It provides pre-filtering and 
post-filtering of interceptor behavior. It also provides multiple reads of requests and a wrapper for response.

## Quick Start

Complete example can view [au-example](https://github.com/lazycece/au/tree/master/au-example)

### Maven dependency

```xml
<dependency>
  <groupId>com.lazycece.au</groupId>
  <artifactId>au-core</artifactId>
  <version>1.0.0</version>
</dependency>
```

### Filter

Defining `filter`，and implement the `AuFilter`.

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

Injecting `AuFilter`，and you can also set related policies for filters.

```java
    AuManager auManager = AuManager.getInstance();
    auManager.addAuFilter(SimpleAuFilter.class)
            .excludePatterns("/a/b")
            .includePatterns("/**")
            .order(1);
    auManager.setWrapper(true);
```

### Enable Au

Injecting `AuServletFilter` into `Servlet`， as follow(`jetty`):

```java
    ServletContextHandler contextHandler = new ServletContextHandler();
    contextHandler.setContextPath("/au");
    server.setHandler(contextHandler);
    contextHandler.addServlet(ExampleServlet.class, "/*");
    contextHandler.addFilter(AuServletFilter.class, "/*", EnumSet.of(DispatcherType.REQUEST));
```

## License

[MIT](https://github.com/lazycece/au/blob/master/LICENSE)
 