package com.pedroalmir.testPriorization.util.file;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.io.DirectoryWalker;

@SuppressWarnings("rawtypes")
public class MyDirectoryWalker extends DirectoryWalker {
        
        private String extension;
        private int depth;
        
        public MyDirectoryWalker(String extension, int depth) {
                super();
                this.setExtension(extension);
                this.setDepth(depth);
        }

        @SuppressWarnings("unchecked")
		public List<File> getExtensionFiles(File dir) throws IOException {
                List<File> results = new ArrayList<File>();
                walk(dir, results);
                return results;
        }

        
        @SuppressWarnings({ "unchecked" })
        protected void handleFile(File file, int depth, Collection results) throws IOException {
                if (file.getCanonicalPath().endsWith(this.getExtension())) {
                        if (depth <= this.getDepth()) {
                                results.add(file);
                        }
                }
        }

        protected boolean handleDirectory(File directory, int depth, Collection results) {
                return true;

        }

        public String getExtension() {
                return extension;
        }

        public void setExtension(String extension) {
                this.extension = extension;
        }

        public int getDepth() {
                return depth;
        }

        public void setDepth(int depth) {
                this.depth = depth;
        }
}