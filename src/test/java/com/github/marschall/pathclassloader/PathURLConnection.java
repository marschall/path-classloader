package com.github.marschall.pathclassloader;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Path;

final class PathURLConnection extends URLConnection {

	private final Path path;

	PathURLConnection(URL url, Path path) {
		super(url);
		this.path = path;
	}

	@Override
	public void connect() throws IOException {
		// TODO Auto-generated method stub

	}

}
