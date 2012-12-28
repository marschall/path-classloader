Path Class Loader
=================

A class loader that can be created on a Java 7 (JSR-203) file system path. This is very similar to `URLClassLoader` except that it works on any file system. `URLClassLoader` only works when a corresponding `URLStreamHandler` is installed which [is hard to do in a non-intrusive way](http://www.unicon.net/node/776).

This class loader does not work with Spring resource scanning because the way Spring resource scanning is implemented it works only for for folders and JAR files. 
