package com.rostreamer.indexer;

import java.io.File;
import java.io.FileFilter;

public class IndexerFileFilter implements FileFilter {

	@Override
	public boolean accept(File file) {
		String name = file.getName();
		return ((name.endsWith(".mp4") || file.isDirectory()) && (name.startsWith("_") == false));
	}
}
