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

package org.jboss.jca.test.core.spec.chapter11.common;

import javax.resource.spi.work.TransactionContext;
import javax.resource.spi.work.WorkContextLifecycleListener;

/**
 * TransactionContextCustom.
 * @version $Rev$ $Date$
 *
 */
public class TransactionContextCustom extends TransactionContext implements WorkContextLifecycleListener
{
   /** Serial version UID */
   private static final long serialVersionUID = -3841107265829832325L;

   /**contextSetupComplete*/
   private static boolean contextSetupComplete = false;

   /**contextSetupFailedErrorCode*/
   private static String contextSetupFailedErrorCode = "";
   
   /**
    * isContextSetupComplete
    * @return isContextSetupComplete
    */
   public static boolean isContextSetupComplete()
   {
      return contextSetupComplete;
   }

   /**
    * getContextSetupFailedErrorCode
    * @return getContextSetupFailedErrorCode
    */
   public static String getContextSetupFailedErrorCode()
   {
      return contextSetupFailedErrorCode;
   }

   /**
    * Context setup complete
    */
   public void contextSetupComplete()
   {
      contextSetupComplete = true;
   }

   /**
    * Context setup failed
    * @param errorCode The error code
    */
   public void contextSetupFailed(String errorCode)
   {
      contextSetupFailedErrorCode = errorCode;
      contextSetupComplete = false;
   }
}
