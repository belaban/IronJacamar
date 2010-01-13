/*
 * JBoss, Home of Professional Open Source.
 * Copyright 2008-2009, Red Hat Middleware LLC, and individual contributors
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

package org.jboss.jca.deployers.fungal;

import org.jboss.jca.fungal.deployers.Deployment;

import java.io.Closeable;
import java.io.IOException;
import java.net.URL;
import java.util.List;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.jboss.logging.Logger;
import org.jboss.util.naming.Util;

/**
 * A resource adapter deployment for JCA/SJC
 * @author <a href="mailto:jesper.pedersen@jboss.org">Jesper Pedersen</a>
 */
public class RADeployment implements Deployment
{
   private static Logger log = Logger.getLogger(RADeployer.class);

   /** The resource adapter file */
   private URL adapter;

   /** JNDI names for connection factories */
   private List<String> jndiNames;

   /** The classloader */
   private ClassLoader cl;

   /**
    * Constructor
    * @param adapter The adapter
    * @param jndiNames The JNDI names for connection factories
    * @param cl The classloader for the deployment
    */
   public RADeployment(URL adapter, List<String> jndiNames, ClassLoader cl)
   {
      this.adapter = adapter;
      this.jndiNames = jndiNames;
      this.cl = cl;
   }

   /**
    * Get the unique URL for the deployment
    * @return The URL
    */
   public URL getURL()
   {
      return adapter;
   }

   /**
    * Get the classloader
    * @return The classloader
    */
   public ClassLoader getClassLoader()
   {
      return cl;
   }

   /**
    * Destroy
    */
   public void destroy()
   {
      log.debug("Undeploying: " + adapter.toExternalForm());

      if (jndiNames != null)
      {
         Context context = null;
         try
         {
            context = new InitialContext();

            for (String jndiName : jndiNames)
            {
               try
               {
                  Util.unbind(context, jndiName);
               }
               catch (Throwable it)
               {
                  log.warn("Exception during JNDI unbind for: " + jndiName, it);
               }
            }
         }
         catch (Throwable t)
         {
            log.warn("Exception during JNDI initialization", t);
         }
         finally
         {
            if (context != null)
            {
               try
               {
                  context.close();
               }
               catch (NamingException ne)
               {
                  // Ignore
               }
            }
         }
      }


      if (cl != null && cl instanceof Closeable)
      {
         try
         {
            ((Closeable)cl).close();
         }
         catch (IOException ioe)
         {
            // Swallow
         }
      }

      log.info("Undeployed: " + adapter.toExternalForm());
   }
}
