package com.github.marschall.pathclassloader;

import java.io.IOException;
import java.net.URL;
import java.net.URLStreamHandler;
import java.nio.file.FileSystem;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;

//TODO SecureClassLoader
public final class PathClassLoader extends ClassLoader {

	private static final URLStreamHandler HANDLER = new PathURLStreamHandler();

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
	
	@Override
	protected Class<?> findClass(String name) throws ClassNotFoundException {
		// TODO path injection
		Path classPath = this.path.resolve(name.replace('.', '/').concat(".class"));
		if (Files.exists(classPath)) {
			try {
				byte[] byteCode = Files.readAllBytes(classPath);
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
		// TODO path injection
		Path resolved = this.path.resolve(name);
		if (Files.exists(resolved)) {
			try {
				return toURL(resolved);
			} catch (IOException e) {
				throw new RuntimeException("could not open " + resolved, e);
			}
		} else {
			return null;
		}
	}
	
	@Override
	protected Enumeration<URL> findResources(String name) throws IOException {
		FileSystem fileSystem = this.path.getFileSystem();
		// TODO correct?
		final PathMatcher matcher = fileSystem.getPathMatcher("glob:" + name);
		final List<URL> resources = new ArrayList<>();
		
		Files.walkFileTree(path, new SimpleFileVisitor<Path>() {
			@Override
			public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
				if (matcher.matches(file)) {
					resources.add(toURL(file));
				}
				return super.visitFile(file, attrs);
			}
		});
		return Collections.enumeration(resources);
	}

	private URL toURL(Path path) throws IOException {
		return new URL(null, path.toUri().toString(), HANDLER);
	} 

}
