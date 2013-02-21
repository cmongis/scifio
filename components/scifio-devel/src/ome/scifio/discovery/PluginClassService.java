/*
 * #%L
 * OME SCIFIO package for reading and converting scientific file formats.
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

package ome.scifio.discovery;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import ome.scifio.ScifioPlugin;

import org.scijava.plugin.Plugin;
import org.scijava.plugin.PluginInfo;
import org.scijava.service.AbstractService;
import org.scijava.service.Service;

/**
 * Service for obtaining a list of plugin classes
 * 
 * @author Mark Hiner
 *
 */
@Plugin(type = Service.class)
public class PluginClassService extends AbstractService {
  
  /**
   * Convenience method for obtaining a list of Classes instead of PluginInfo objects.
   */
  public <PT extends ScifioPlugin> List<Class<? extends PT>> getPluginClasses(Class<PT> type) {
    return getPluginClasses(type, null, null);
  }
  
  /**
   * As {@link #getPluginClasses(Class)} but with key,value pair parameters for filtering.
   */
  public <PT extends ScifioPlugin> List<Class<? extends PT>> getPluginClasses(Class<PT> type, Map<String, String> andPairs, Map<String, String> orPairs) {
    List<PluginInfo<PT>> pluginInfos = getContext().getService(PluginAttributeService.class).getPluginsOfType(type, andPairs, orPairs);
        
    List<Class<? extends PT>> pluginClasses = new ArrayList<Class<? extends PT>>();
    
    for(PluginInfo<PT> pluginInfo : pluginInfos) {
      pluginClasses.add(pluginInfo.getPluginClass());
    }
    
    return pluginClasses;
  }


}