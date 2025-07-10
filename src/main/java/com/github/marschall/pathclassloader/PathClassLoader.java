package com.github.marschall.pathclassloader;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * Loads classes from a certain {@link Path}.
 *
 * <p>Class loading will will happen in a parent first manner which is
 * the Java SE default may.
 */
public final class PathClassLoader extends ClassLoader {

  private final Path path;

  static {
    registerAsParallelCapable();
  }

  /**
   * Creates a new {@link PathClassLoader} with no parent class loader.
   *
   * @param path the path from which to load the classes
   */
  public PathClassLoader(Path path) {
    this(path, null);
  }


  /**
   * Creates a new {@link PathClassLoader} with a parent class loader.
   *
   * @param path the path from which to load the classes
   * @param parent the class loader from which to try loading classes
   *  first
   */
  public PathClassLoader(Path path, ClassLoader parent) {
    super(parent);
    this.path = path;
  }

  @Override
  protected Class<?> findClass(String name) throws ClassNotFoundException {
    Path clazz = this.path.resolve(name.replace('.', '/').concat(".class")).normalize();
    if (!clazz.startsWith(this.path)) {
      throw new ClassNotFoundException();
    }
    if (Files.exists(clazz)) {
      try {
        byte[] byteCode = Files.readAllBytes(clazz);
        return this.defineClass(name, byteCode, 0, byteCode.length);
      } catch (IOException e) {
        throw new ClassNotFoundException(name, e);
      }
    } else {
      throw new ClassNotFoundException(name);
    }
  }

  @Override
  protected URL findResource(String name) {
    Path resolved = this.path.resolve(name).normalize();
    if (!resolved.startsWith(this.path)) {
      return null;
    }
    if (Files.exists(resolved)) {
      try {
        return this.toURL(resolved);
      } catch (IOException e) {
        throw new RuntimeException("could not open " + resolved, e);
      }
    } else {
      return null;
    }
  }

  @Override
  protected Enumeration<URL> findResources(final String name) throws IOException {
    Path resolved = this.path.resolve(name).normalize();
    if (!resolved.startsWith(this.path)) {
      return Collections.emptyEnumeration();
    }
    if (Files.exists(resolved)) {
      try {
        return new SingletonEnumeration<>(this.toURL(resolved));
      } catch (IOException e) {
        throw new RuntimeException("could not open " + resolved, e);
      }
    } else {
      return Collections.emptyEnumeration();
    }
  }

  private URL toURL(Path path) throws IOException {
    return new URL(null, path.toUri().toString(), PathURLStreamHandler.INSTANCE);
  }

  static final class SingletonEnumeration<E> implements Enumeration<E> {

    private boolean hasMoreElements;
    private final E element;

    SingletonEnumeration(E element) {
      this.element = element;
      this.hasMoreElements = true;
    }

    @Override
    public boolean hasMoreElements() {
      return this.hasMoreElements;
    }

    @Override
    public E nextElement() {
      if (!this.hasMoreElements) {
        throw new NoSuchElementException();
      }
      this.hasMoreElements = false;
      return this.element;
    }

    public Iterator<E> asIterator() {
      if (this.hasMoreElements) {
        return Collections.singleton(this.element).iterator();
      } else {
        return Collections.emptyIterator();
      }
    }

  }

}
