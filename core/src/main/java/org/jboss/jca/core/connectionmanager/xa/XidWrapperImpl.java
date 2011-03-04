/*
 * JBoss, Home of Professional Open Source.
 * Copyright 2011, Red Hat Middleware LLC, and individual contributors
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
package org.jboss.jca.core.connectionmanager.xa;

import org.jboss.jca.core.spi.connectionmanager.xa.XidWrapper;

import java.util.Arrays;

import javax.transaction.xa.Xid;

/**
 * A XidWrapper.
 * 
 * @author <a href="weston.price@jboss.com">Weston Price</a>
 * @author <a href="jesper.pedersen@jboss.org">Jesper Pedersen</a>
 */
public class XidWrapperImpl implements XidWrapper
{
   /** The serialVersionUID */
   private static final long serialVersionUID = 3223859423961011795L;

   /** The formatId */
   private int formatId;
   
   /** The globalTransactionId */
   private byte[] globalTransactionId;
   
   /** The branchQualifier */
   private byte[] branchQualifier;

   /** The jndi name */
   private String jndiName;
   
   /** Cached toString() */
   private transient String cachedToString;

   /** Cached hashCode() */
   private transient int cachedHashCode;
   
   /**
    * Creates a new XidWrapperImpl instance.
    * @param xid The Xid instances
    * @param pad Should the branch qualifier be padded
    * @param jndiName The JNDI name
    */
   public XidWrapperImpl(Xid xid, boolean pad, String jndiName)
   {
      this.branchQualifier = pad ? new byte[Xid.MAXBQUALSIZE] : new byte[xid.getBranchQualifier().length];      
      System.arraycopy(xid.getBranchQualifier(), 0, branchQualifier, 0, xid.getBranchQualifier().length);      

      this.globalTransactionId = xid.getGlobalTransactionId();
      this.formatId = xid.getFormatId();
      this.jndiName = jndiName;
   }

   /**
    * {@inheritDoc}
    */
   public byte[] getBranchQualifier()
   {
      return branchQualifier.clone();
   }

   /**
    * {@inheritDoc}
    */
   public int getFormatId()
   {
      return formatId;
   }

   /**
    * {@inheritDoc}
    */
   public byte[] getGlobalTransactionId()
   {
      return globalTransactionId.clone();
   }
   
   /**
    * {@inheritDoc}
    */
   public String getJndiName()
   {
      return jndiName;
   }
   
   /**
    * {@inheritDoc}
    */
   public boolean equals(Object object)
   {
      if (object == this)
         return true;

      if (object == null || !(object instanceof Xid))
         return false;  

      Xid other = (Xid)object;
      return
         (
            formatId == other.getFormatId() && 
            Arrays.equals(globalTransactionId, other.getGlobalTransactionId()) &&
            Arrays.equals(branchQualifier, other.getBranchQualifier())
         );
   }

   /**
    * {@inheritDoc}
    */
   public int hashCode()
   {
      if (cachedHashCode == 0)
      {
         cachedHashCode = formatId;
         for (int i = 0; i < globalTransactionId.length; ++i)
            cachedHashCode += globalTransactionId[i];
      }
      return cachedHashCode;
   }

   /**
    * {@inheritDoc}
    */
   public String toString()
   {
      if (cachedToString == null)
      {
         StringBuilder sb = new StringBuilder();
         sb.append("XidWrapperImpl@").append(Integer.toHexString(System.identityHashCode(this)));
         sb.append("[formatId=").append(getFormatId());
         sb.append(" globalTransactionId=").append(new String(getGlobalTransactionId()).trim());
         sb.append(" branchQualifier=").append(new String(getBranchQualifier()).trim());
         sb.append(" jndiName=").append(jndiName);
         sb.append("]");
         cachedToString = sb.toString();
      }

      return cachedToString;
   }
}
