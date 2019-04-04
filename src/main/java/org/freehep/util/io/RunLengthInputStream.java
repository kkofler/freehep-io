// Copyright 2001-2009, FreeHEP.
// Copyright 2019 DAGOPT Optimization Technologies GmbH, ALL RIGHTS RESERVED.
// License: http://freehep.github.io/freehep-psviewer/license.html
package org.freehep.util.io;

import java.io.IOException;
import java.io.InputStream;

/**
 * The RunLengthStream decodes Run Length encoding. The exact definition of Run
 * Length encoding can be found in the PostScript Language Reference (3rd ed.)
 * chapter 3.13.3. *
 * 
 * @author Mark Donszelmann
 */
public class RunLengthInputStream extends DecodingInputStream implements
		RunLength {

	private int[] buffer = new int[LENGTH];

	private int index;

	private int count;

	private InputStream in;

	/**
	 * Create a Run Length input stream
	 * 
	 * @param input
	 *            stream to read from
	 */
	public RunLengthInputStream(InputStream input) {
		super();
		in = input;
		index = 0;
		count = 0;
	}

	@Override
	public int read() throws IOException {

		if ((index >= count) || (index > 128)) {
			if (!fillBuffer()) {
				return -1;
			}
		}

		int b = buffer[index];
		index++;
		return b & 0x00FF;
	}

	private boolean fillBuffer() throws IOException {
		count = in.read();

		if (end(count)) {
			return false;
		}

		if (count < 128) {
			// buffered
			count++;
			for (int i = 0; i < count; i++) {
				buffer[i] = in.read();
				if (buffer[i] < 0) {
					return false;
				}
			}
		} else {
			// counted
			count = 257 - count;
			int b = in.read();
			if (b < 0) {
				return false;
			}

			for (int i = 0; i < count; i++) {
				buffer[i] = b;
			}
		}
		index = 0;
		return true;
	}

	private boolean end(int b) {
		if ((b < 0) || (b == EOD)) {
			return true;
		}
		return false;
	}
}
