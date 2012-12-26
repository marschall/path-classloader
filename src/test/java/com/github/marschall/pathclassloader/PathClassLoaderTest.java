package com.github.marschall.pathclassloader;

import static java.nio.file.StandardOpenOption.CREATE_NEW;
import static java.nio.file.StandardOpenOption.WRITE;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.Callable;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.github.marschall.memoryfilesystem.MemoryFileSystemBuilder;

public class PathClassLoaderTest {
  
  private FileSystem fileSystem;
  
  @Before
  public void setUp() throws IOException {
	  this.fileSystem = MemoryFileSystemBuilder.newEmpty().build("url-path-test");
  } 
  
  @After
  public void tearDown() throws IOException {
	  this.fileSystem.close();
  } 

  @Test
  public void createUrlClassLoader() throws Exception {
    Class<?> clazz = HelloWorld.class;
    String fileName = clazz.getSimpleName() + ".class";
    try (InputStream sourceClassFile = clazz.getResourceAsStream(fileName)) {
      assertNotNull(sourceClassFile);

      Path parent = this.fileSystem.getPath(clazz.getPackage().getName().replace('.', '/'));
      Files.createDirectories(parent);
      try (OutputStream output = Files.newOutputStream(parent.resolve(fileName), CREATE_NEW, WRITE)) {
        byte[] buffer = new byte[4096];
        int read;
        while ((read = sourceClassFile.read(buffer)) != -1) {
          output.write(buffer, 0, read);
        }
      }

      Path root = this.fileSystem.getPath("/");
      ClassLoader classLoader = new PathClassLoader(root);
      Class<?> loadedClass = Class.forName(clazz.getName(), true, classLoader);
      assertTrue(Callable.class.isAssignableFrom(loadedClass));
      Object instance = loadedClass.getConstructor().newInstance();
      assertEquals("Hello World", ((Callable<?>) instance).call());

    }
  }

}

class HelloWorld implements Callable<String> {

  @Override
  public String call() throws Exception {
    return "Hello World";
  }

}
