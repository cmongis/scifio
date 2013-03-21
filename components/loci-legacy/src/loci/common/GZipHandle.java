/*
 * #%L
 * Legacy layer preserving compatibility between legacy Bio-Formats and SCIFIO.
 * %%
 * Copyright (C) 2005 - 2013 Open Microscopy Environment:
 *   - Board of Regents of the University of Wisconsin-Madison
 *   - Glencoe Software, Inc.
 *   - University of Dundee
 * %%
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * 1. Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDERS OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 * 
 * The views and conclusions contained in the software and documentation are
 * those of the authors and should not be interpreted as representing official
 * policies, either expressed or implied, of any organization.
 * #L%
 */

package loci.common;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import loci.legacy.context.LegacyContext;
import loci.utils.ProtectedMethodInvoker;

/**
 * A legacy delegator class for ome.scifio.io.GZipHandle.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="http://trac.openmicroscopy.org.uk/ome/browser/bioformats.git/components/common/src/loci/common/GZipHandle.java">Trac</a>,
 * <a href="http://git.openmicroscopy.org/?p=bioformats.git;a=blob;f=components/common/src/loci/common/GZipHandle.java;hb=HEAD">Gitweb</a></dd></dl>
 *
 * @see StreamHandle
 *
 * @author Melissa Linkert melissa at glencoesoftware.com
 */
public class GZipHandle extends StreamHandle {

  // -- Fields --
  
  private ProtectedMethodInvoker pmi = new ProtectedMethodInvoker();
  
  // -- Constructor --

  /**
   * Construct a new GZipHandle for the given file.
   *
   * @throws HandleException if the given file name is not a GZip file.
   */
  public GZipHandle(String file) throws IOException {
    sHandle = new ome.scifio.io.GZipHandle(LegacyContext.get(), file);
  }

  // -- GZipHandle API methods --

  /** Returns true if the given filename is a gzip file. */
  public static boolean isGZipFile(String file) throws IOException {
    ome.scifio.io.GZipHandle handle = new ome.scifio.io.GZipHandle(LegacyContext.get());
    
    return handle.isConstructable(file);
  }

  // -- StreamHandle API methods --

  /* @see StreamHandle#resetStream() */
  protected void resetStream() throws IOException {
    Class<?>[] c = null;
    Object[] o = null;
    
    try {
      pmi.invokeProtected(sHandle, "resetStream", c, o);
    }
    catch (InvocationTargetException e) {
      pmi.unwrapException(e, IOException.class);
      throw new IllegalStateException(e);
    }
  }

}
