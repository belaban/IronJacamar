/*
 * JBoss, Home of Professional Open Source.
 * Copyright 2009, Red Hat Middleware LLC, and individual contributors
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
package org.jboss.jca.test.core.spec.chapter10.common;

import java.util.concurrent.CountDownLatch;

import javax.resource.spi.work.Work;
import javax.resource.spi.work.WorkException;
import javax.resource.spi.work.WorkManager;

/**
 * NestCharWork.

 * @author <a href="mailto:jeff.zhang@jboss.org">Jeff Zhang</a>
 * @version $Revision: $
 */
public class NestCharWork implements Work
{
   /**
    * shared string buffer
    */
   private static StringBuffer buf = new StringBuffer();
   /**
    * Latch when enter run method
    */
   private CountDownLatch start;
   /**
    * Latch when leave run method
    */
   private CountDownLatch done;
   /**
    * current thread id
    */
   private String name;
   private WorkManager workManager = null;
   private Work nestWork = null;
   private boolean nestDo = false;
  
   /**
    * Constructor.
    * @param name this class name
    * @param start Latch when enter run method
    * @param done Latch when leave run method
    */
   public NestCharWork(String name, CountDownLatch start, CountDownLatch done) 
   {
      this.name = name;
      this.start = start;
      this.done = done;
   }

   /**
    * release method
    */
   public void release()
   {
 
   }

   /**
    * run method
    */
   public void run()
   {
      try
      {
         if (nestWork != null && workManager != null)
         {
            if (nestDo)
            {
               workManager.doWork(nestWork);
            }
         }
         buf.append(name);
         start.await();
      } 
      catch (InterruptedException e)
      {
         e.printStackTrace();
      } 
      catch (WorkException e)
      {
         e.printStackTrace();
      }
      done.countDown(); 
   }
   
   /**
    * empty string buffer
    */
   public void emptyBuffer()
   {
      buf = new StringBuffer();
   }
   
   /**
    * @return String get string buffer
    */
   public String getBuffer()
   {
      return buf.toString();
   }
   
   /**
    * @param wm workManager
    */
   public void setWorkManager(WorkManager wm)
   {
      workManager = wm;
   }
   
   /**
    * @param work work
    */
   public void setWorkManager(Work work)
   {
      nestWork = work;
   }
   
   /**
    * @param exec if nest execute doWork
    */
   public void setNestDo(boolean exec)
   {
      nestDo = exec;
   }
}
