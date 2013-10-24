/**
 * 
 */
package com.pedroalmir.testPriorization.util.file;

import java.io.File;

import org.apache.commons.io.filefilter.AbstractFileFilter;

/**
 * @author Pedro Almir
 *
 */
public class JavaFileFilter extends AbstractFileFilter {
	
	@Override
	public boolean accept(File file) {
		if(file.isFile()){
			
		}
		return super.accept(file);
	}

}
