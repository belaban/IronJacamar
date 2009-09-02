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

package org.jboss.jca.core.api;

import javax.transaction.RollbackException;
import javax.transaction.SystemException;


/**
 * The JBoss specific connection manager interface
 * @author <a href="mailto:gurkanerdogdu@yahoo.com">Gurkan Erdogdu</a>
 * @version $Rev$ $Date$
 */
public interface ConnectionManager extends javax.resource.spi.ConnectionManager
{
   /**
    * Document Me!
    * @param errorRollback errorRollback
    * @return time left
    * @throws RollbackException if exception occurs
    */
   public long getTimeLeftBeforeTransactionTimeout(boolean errorRollback) throws RollbackException;
   
   /**
    * Document Me!
    * @return transaction time out
    * @throws SystemException if any exceptions
    */
   public int getTransactionTimeout() throws SystemException;
   
   /**
    * Document Me!
    * @throws RollbackException rollbacked exception
    * @throws SystemException system exception
    */
   public void checkTransactionActive() throws RollbackException, SystemException;
   
}
