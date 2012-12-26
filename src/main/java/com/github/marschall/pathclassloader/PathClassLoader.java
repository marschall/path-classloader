package com.github.marschall.pathclassloader;

import java.nio.file.Path;
import java.security.SecureClassLoader;

public final class PathClassLoader extends SecureClassLoader {

  private final Path path;

  static {
    if (!registerAsParallelCapable()) {
      throw new AssertionError("could not register class as parallel capable");
    }
  }

  public PathClassLoader(Path path) {
    this(path, null);
  }

  public PathClassLoader(Path path, ClassLoader parent) {
    super(parent);
    this.path = path;
  }

}
