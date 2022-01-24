Path Class Loader [![Build Status](https://app.travis-ci.com/marschall/path-classloader.svg?branch=master)](https://app.travis-ci.com/marschall/path-classloader)
=================

A class loader that can be created on a Java 7 (JSR-203) file system path. This is very similar to `URLClassLoader` except that it works on any file system. `URLClassLoader` only works when a corresponding `URLStreamHandler` is installed which [is hard to do in a non-intrusive way](http://www.unicon.net/node/776).

This class loader does not work with Spring resource scanning because the way Spring resource scanning is implemented it works only for folders and JAR files.

Usage
-----
```java
ClassLoader classLoader = new PathClassLoader(aPath);
```

The constructor argument `aPath` is expected to be a folder. If you have the classes in a JAR create a [Zip File System](http://docs.oracle.com/javase/7/docs/technotes/guides/io/fsp/zipfilesystemprovider.html). If you run into [Bug 8004789](http://bugs.sun.com/view_bug.do?bug_id=8004789) use the [backport for JDK7](https://github.com/marschall/zipfilesystem-standalone).

You can optionally pass in a parent `ClassLoader` in this case parent-first classloading will be used (Java SE default).

Maven
-----

```xml
<dependency>
  <groupId>com.github.marschall</groupId>
  <artifactId>path-classloader</artifactId>
  <version>0.1.0</version>
</dependency>
```

