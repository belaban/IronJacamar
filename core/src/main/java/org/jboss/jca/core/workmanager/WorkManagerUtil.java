/*
 * JBoss, Home of Professional Open Source.
 * Copyright 2012, Red Hat Middleware LLC, and individual contributors
 * as indicated by the @author tags. See the copyright.txt file in the
 * distribution for a full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */

package org.jboss.jca.core.workmanager;

import java.util.Iterator;
import java.util.List;

import javax.resource.spi.work.HintsContext;
import javax.resource.spi.work.Work;
import javax.resource.spi.work.WorkContext;
import javax.resource.spi.work.WorkContextProvider;

/**
 *
 * A WorkManagerUtil.
 *
 * @author <a href="stefano.maestri@jboss.com">Stefano Maestri</a>
 *
 */
public class WorkManagerUtil
{

   /**
    *
    * Utility method to decide if a work will have to run under long running thread pool
    *
    * @param work the work
    * @return true if long running thread pool is required
    */
   public static boolean isLongRunning(Work work)
   {
      boolean isLongRunning = false;
      if (work instanceof WorkContextProvider)
      {
         WorkContextProvider wcProvider = (WorkContextProvider) work;
         List<WorkContext> contexts = wcProvider.getWorkContexts();

         if (contexts != null && contexts.size() > 0)
         {
            boolean found = false;
            Iterator<WorkContext> it = contexts.iterator();
            while (!found && it.hasNext())
            {
               WorkContext wc = it.next();
               if (wc instanceof HintsContext)
               {
                  HintsContext hc = (HintsContext) wc;
                  if (hc.getHints().containsKey(HintsContext.LONGRUNNING_HINT))
                  {
                     isLongRunning = true;
                     found = true;
                  }
               }
            }
         }
      }
      return isLongRunning;
   }
}
