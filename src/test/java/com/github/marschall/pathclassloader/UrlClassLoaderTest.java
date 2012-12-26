package com.github.marschall.pathclassloader;

import static java.nio.file.StandardOpenOption.CREATE_NEW;
import static java.nio.file.StandardOpenOption.WRITE;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.Callable;

import org.junit.Ignore;
import org.junit.Test;

@Ignore("fails")
public class UrlClassLoaderTest {

//  @Rule
//  public final FileSystemRule rule = new FileSystemRule();
  
  private FileSystem fileSytem;

  @Test
  public void createUrlClassLoader() throws Exception {
    Class<?> clazz = HelloWorld.class;
    String fileName = clazz.getSimpleName() + ".class";
    try (InputStream sourceClassFile = clazz.getResourceAsStream(fileName)) {
      assertNotNull(sourceClassFile);

      Path parent = this.fileSytem.getPath(clazz.getPackage().getName().replace('.', '/'));
      Files.createDirectories(parent);
      try (OutputStream output = Files.newOutputStream(parent.resolve(fileName), CREATE_NEW, WRITE)) {
        byte[] buffer = new byte[4096];
        int read;
        while ((read = sourceClassFile.read(buffer)) != -1) {
          output.write(buffer, 0, read);
        }
      }

      Path root = this.fileSytem.getPath("/");
      try (URLClassLoader classLoader = new URLClassLoader(new URL[]{root.toUri().toURL()})) {
        Class<?> loadedClass = Class.forName(clazz.getName(), true, classLoader);
        assertTrue(Callable.class.isAssignableFrom(loadedClass));
        Object instance = loadedClass.getConstructor().newInstance();
        assertEquals("Hello World", ((Callable<?>) instance).call());
      }

    }
  }

}

class HelloWorld implements Callable<String> {

  @Override
  public String call() throws Exception {
    return "Hello World";
  }

}
