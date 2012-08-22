/*
 * JBoss, Home of Professional Open Source.
 * Copyright 2008, Red Hat Middleware LLC, and individual contributors
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
package org.jboss.jca.core.workmanager.transport.remote;

import org.jboss.jca.core.CoreLogger;
import org.jboss.jca.core.api.workmanager.DistributedWorkManager;
import org.jboss.jca.core.spi.workmanager.notification.NotificationListener;
import org.jboss.jca.core.spi.workmanager.transport.Transport;
import org.jboss.jca.core.workmanager.transport.remote.ProtocolMessages.Request;
import org.jboss.jca.core.workmanager.transport.remote.jgroups.JGroupsTransport;

import java.io.Serializable;
import java.util.Map;
import java.util.concurrent.ExecutorService;

import javax.resource.spi.work.DistributableWork;
import javax.resource.spi.work.WorkException;

import org.jboss.logging.Logger;

/**
 *
 * A AbstractRemoteTransport.
 *
 * @author <a href="stefano.maestri@jboss.com">Stefano Maestri</a>
 *
 */
public abstract class AbstractRemoteTransport<T> implements Transport
{

   /** The logger */
   protected static CoreLogger log = Logger.getMessageLogger(CoreLogger.class, JGroupsTransport.class.getName());

   /**
    *
    * send a messagge using specific protocol. Method overridden in specific protocol implementation classes
    *
    * @param address the address
    * @param request the request
    * @param parameters the parameters
    * @return the returned long value. Can be null if requested operation return a void
    * @throws WorkException in case of problem with the work
    */
   protected abstract Long sendMessage(T address, Request request, Serializable... parameters)
      throws WorkException;

   /** Whether trace is enabled */
   private static boolean trace = log.isTraceEnabled();

   /** Distributed work manager instance */
   protected DistributedWorkManager dwm;

   /** The kernel executorService*/
   protected ExecutorService executorService;

   /** The work manager */
   protected Map<String, T> workManagers;

   /**
    *
    * Create a new AbstractRemoteTransport.
    *
    */
   public AbstractRemoteTransport()
   {
      super();
   }

   /**
    * Init
    */
   private void init()
   {
      if (getWorkManagers() != null)
      {
         for (Map.Entry<String, T> entry : getWorkManagers().entrySet())
         {
            String id = entry.getKey();

            if (dwm.getPolicy() instanceof NotificationListener)
            {
               NotificationListener nl = (NotificationListener) dwm.getPolicy();

               nl.join(id);
               nl.updateShortRunningFree(id, 10);
               nl.updateLongRunningFree(id, 10);
            }
            if (dwm.getSelector() instanceof NotificationListener)
            {
               NotificationListener nl = (NotificationListener) dwm.getSelector();
               nl.join(id);
               nl.updateShortRunningFree(id, 10);
               nl.updateLongRunningFree(id, 10);
            }
         }
      }
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void setDistributedWorkManager(DistributedWorkManager dwm)
   {
      this.dwm = dwm;
      init();
   }

   /**
    * get The distributed work manager
    * @return the ditributed work manager
    */
   public DistributedWorkManager getDistributedWorkManager()
   {
      return dwm;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public long ping(String id)
   {
      if (trace)
         log.tracef("PING(%s)", id);

      long start = System.currentTimeMillis();

      try
      {
         T address = workManagers.get(id);
         sendMessage(address, Request.PING);
      }
      catch (WorkException e1)
      {
         if (log.isDebugEnabled())
         {
            log.debug("Error", e1);
         }
         return Long.MAX_VALUE;
      }

      return System.currentTimeMillis() - start;

   }

   @Override
   public long getShortRunningFree(String dwm)
   {
      if (trace)
         log.tracef("GET_SHORT_RUNNING_FREE(%s)", dwm);
      try
      {
         T address = workManagers.get(dwm);
         return sendMessage(address, Request.GET_SHORTRUNNING_FREE);
      }
      catch (WorkException e1)
      {
         if (log.isDebugEnabled())
         {
            log.debug("Error", e1);
         }
         return 0L;
      }

   }

   @Override
   public long getLongRunningFree(String dwm)
   {
      if (trace)
         log.tracef("GET_SHORT_RUNNING_FREE(%s)", dwm);
      try
      {
         T address = workManagers.get(dwm);
         return sendMessage(address, Request.GET_LONGRUNNING_FREE);
      }
      catch (WorkException e1)
      {
         if (log.isDebugEnabled())
         {
            log.debug("Error", e1);
         }
         return 0L;
      }
   }

   @Override
   public void updateShortRunningFree(String id, long freeCount)
   {
      if (trace)
         log.tracef("UPDATE_SHORT_RUNNING_FREE(%s,%d) from %s", id, freeCount, dwm.getId());
      try
      {
         for (T address : workManagers.values())
         {
            sendMessage(address, Request.UPDATE_SHORTRUNNING_FREE, id, freeCount);
         }
      }
      catch (WorkException e1)
      {
         if (log.isDebugEnabled())
         {
            log.debug("Error", e1);
         }
      }
   }

   @Override
   public void updateLongRunningFree(String id, long freeCount)
   {
      if (trace)
         log.tracef("UPDATE_LONG_RUNNING_FREE(%s,%d) from %s", id, freeCount, dwm.getId());
      try
      {
         for (T address : workManagers.values())
         {
            sendMessage(address, Request.UPDATE_LONGRUNNING_FREE, id, freeCount);
         }
      }
      catch (WorkException e1)
      {
         if (log.isDebugEnabled())
         {
            log.debug("Error", e1);
         }
      }

   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void doWork(String id, DistributableWork work) throws WorkException
   {
      if (trace)
         log.tracef("DO_WORK(%s, %s)", id, work);

      T address = workManagers.get(id);

      sendMessage(address, Request.DO_WORK, work);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void scheduleWork(String id, DistributableWork work) throws WorkException
   {
      if (trace)
         log.tracef("SCHEDULE_WORK(%s, %s)", id, work);

      T address = workManagers.get(id);

      sendMessage(address, Request.SCHEDULE_WORK, work);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public long startWork(String id, DistributableWork work) throws WorkException
   {
      if (trace)
         log.tracef("START_WORK(%s, %s)", id, work);

      T address = workManagers.get(id);

      return sendMessage(address, Request.START_WORK, work);
   }

   /**
    * Get the executorService.
    *
    * @return the executorService.
    */
   public ExecutorService getExecutorService()
   {
      return executorService;
   }

   /**
    * Set the executorService.
    *
    * @param executorService The executorService to set.
    */
   public void setExecutorService(ExecutorService executorService)
   {
      this.executorService = executorService;
   }

   /**
    * Get the workManagers.
    *
    * @return the workManagers.
    */
   public Map<String, T> getWorkManagers()
   {
      return workManagers;
   }

   /**
    * Set the workManagers.
    *
    * @param workManagers The workManagers to set.
    */
   public void setWorkManagers(Map<String, T> workManagers)
   {
      this.workManagers = workManagers;
   }

}
