package com.github.marschall.pathclassloader;

import static java.nio.file.StandardOpenOption.CREATE_NEW;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;
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
      try (OutputStream output = Files.newOutputStream(parent.resolve(fileName), CREATE_NEW)) {
        copy(sourceClassFile, output);
      }

      Path root = this.fileSystem.getPath("/");
      ClassLoader classLoader = new PathClassLoader(root);
      Class<?> loadedClass = Class.forName(clazz.getName(), true, classLoader);
      assertTrue(Callable.class.isAssignableFrom(loadedClass));
      Object instance = loadedClass.getConstructor().newInstance();
      assertEquals("Hello World", ((Callable<?>) instance).call());

    }
  }
  
  @Test
  public void findResources() throws IOException {
    Class<?> clazz = HelloWorld.class;
    try (InputStream sourceClassFile = clazz.getClassLoader().getResourceAsStream("subfolder/file.txt")) {
      assertNotNull(sourceClassFile);
      
      Path target = this.fileSystem.getPath("/subfolder/file.txt");
      Files.createDirectories(target.getParent());
      
      try (OutputStream output = Files.newOutputStream(target, CREATE_NEW)) {
        copy(sourceClassFile, output);
      }
      
      Path root = this.fileSystem.getPath("/");
      ClassLoader classLoader = new PathClassLoader(root);
      List<URL> resources = Collections.list(classLoader.getResources("/subfolder/*"));
      
      assertThat(resources, hasSize(1));
      
      URL resoure = resources.get(0);
      try (InputStream input = resoure.openStream()) {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        copy(input, output);
        byte[] data = output.toByteArray();
        byte[] expected = new byte[]{'c', 'o', 'n', 't', 'e', 'n', 't'};
        assertArrayEquals(expected, data);
      }
    }  
  }
  
  private static void copy(InputStream input, OutputStream output) throws IOException {
    byte[] buffer = new byte[4096];
    int read;
    while ((read = input.read(buffer)) != -1) {
      output.write(buffer, 0, read);
    }
  }

}
