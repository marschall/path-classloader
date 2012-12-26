package com.github.marschall.pathclassloader;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Path;

final class PathURLConnection extends URLConnection {

	private final Path path;

	PathURLConnection(URL url, Path path) {
		super(url);
		this.path = path;
	}

	@Override
	public void connect() throws IOException {
		// nothing to do
	}
	
	@Override
	public long getContentLengthLong() {
		try {
			return Files.size(this.path);
		} catch (IOException e) {
			throw new RuntimeException("could not get size of: " + this.path, e);
		}
	}
	
	@Override
	public InputStream getInputStream() throws IOException {
		return Files.newInputStream(this.path);
	}
	
	@Override
	public OutputStream getOutputStream() throws IOException {
		return Files.newOutputStream(this.path);
	}

}
