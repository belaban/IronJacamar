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
package org.jboss.jca.as.converters;

import org.jboss.jca.common.api.metadata.common.CommonXaPool;
import org.jboss.jca.common.api.metadata.common.Extension;
import org.jboss.jca.common.api.metadata.common.FlushStrategy;
import org.jboss.jca.common.api.metadata.common.Recovery;
import org.jboss.jca.common.api.metadata.ds.DsSecurity;
import org.jboss.jca.common.api.metadata.ds.Statement;
import org.jboss.jca.common.api.metadata.ds.TimeOut;
import org.jboss.jca.common.api.metadata.ds.TransactionIsolation;
import org.jboss.jca.common.api.metadata.ds.Validation;
import org.jboss.jca.common.api.metadata.ds.Statement.TrackStatementsEnum;
import org.jboss.jca.common.metadata.common.CommonXaPoolImpl;
import org.jboss.jca.common.metadata.ds.DsSecurityImpl;
import org.jboss.jca.common.metadata.ds.StatementImpl;
import org.jboss.jca.common.metadata.ds.TimeOutImpl;
import org.jboss.jca.common.metadata.ds.ValidationImpl;
import org.jboss.jca.common.metadata.ds.XADataSourceImpl;

import java.util.HashMap;
import java.util.Map;

/**
 * A XaDataSource impl.
 * 
 * @author Jeff Zhang
 * @version $Revision: $
 */
public class LegacyXaDataSourceImp implements XaDataSource
{

   private XADataSourceImpl dsImpl = null;
   
   //private String driverClass;

   private String xaDataSourceClass;

   private final String driver;

   private final HashMap<String, String> xaDataSourceProperty;

   protected final TransactionIsolation transactionIsolation;

   protected TimeOut timeOut;

   protected DsSecurity security;

   protected Statement statement;

   protected Validation validation;

   private CommonXaPool xaPool;

   protected String urlDelimiter;

   protected String urlSelectorStrategyClassName;
   
   private String newConnectionSql;

   protected Boolean useJavaContext;

   protected String poolName;

   protected Boolean enabled;

   protected String jndiName;

   protected Boolean spy;

   protected Boolean useCcm;
   
   protected Boolean jta;
   
   Recovery recovery;
   
   Boolean isSameRmOverride;

   Boolean interleaving;

   Boolean padXid;

   Boolean wrapXaDataSource;

   Boolean noTxSeparatePool;
   
   /*
    * TransactionIsolation transactionIsolation, TimeOut timeOut, DsSecurity security,
      Statement statement, Validation validation, String urlDelimiter, String urlSelectorStrategyClassName,
      Boolean useJavaContext, String poolName, Boolean enabled, String jndiName, Boolean spy, Boolean useCcm,
      Map<String, String> xaDataSourceProperty, String xaDataSourceClass, String driver, String newConnectionSql,
      CommonXaPool xaPool, Recovery recovery
         */
   
   public LegacyXaDataSourceImp(String xaDataSourceClass, String driver,
         TransactionIsolation transactionIsolation, Map<String, String> xaDataSourceProperty)
   {
      //this.connectionUrl = connectionUrl;
      //this.driverClass = driverClass;
      this.xaDataSourceClass = xaDataSourceClass;
      this.driver = driver;
      if (xaDataSourceProperty != null)
      {
         this.xaDataSourceProperty = new HashMap<String, String>(xaDataSourceProperty.size());
         this.xaDataSourceProperty.putAll(xaDataSourceProperty);
      }
      else
      {
         this.xaDataSourceProperty = new HashMap<String, String>(0);
      }
      this.transactionIsolation = transactionIsolation;
   }
   
   public void buildXaDataSourceImpl()  throws Exception
   {
      dsImpl = new XADataSourceImpl(transactionIsolation, timeOut, security,
            statement, validation, urlDelimiter, urlSelectorStrategyClassName, 
            useJavaContext, poolName, enabled, jndiName, spy, useCcm, 
            xaDataSourceProperty, xaDataSourceClass, driver, newConnectionSql, 
            xaPool, recovery);
   }
   
   @Override
   public String toString()
   {
      String out = dsImpl.toString();
      return out;
   }
   
   public LegacyXaDataSourceImp buildTimeOut(Long blockingTimeoutMillis, Long idleTimeoutMinutes, Integer allocationRetry,
         Long allocationRetryWaitMillis, Integer xaResourceTimeout, Boolean setTxQueryTimeout, Long queryTimeout,
         Long useTryLock) throws Exception
   {
      timeOut = new TimeOutImpl(blockingTimeoutMillis, idleTimeoutMinutes, allocationRetry,allocationRetryWaitMillis, xaResourceTimeout, setTxQueryTimeout,
            queryTimeout, useTryLock);
      return this;
   }
   
   public LegacyXaDataSourceImp buildDsSecurity(String userName, String password, String securityDomain, Extension reauthPlugin)
   throws Exception
   {
      security = new DsSecurityImpl(userName, password, securityDomain, reauthPlugin);
      return this;
   }
   
   public LegacyXaDataSourceImp buildStatement(Boolean sharePreparedStatements, Long preparedStatementsCacheSize,
         TrackStatementsEnum trackStatements) throws Exception
   {
      statement = new StatementImpl(sharePreparedStatements, preparedStatementsCacheSize, trackStatements);
      return this;
   }
   
   public LegacyXaDataSourceImp buildValidation(Boolean backgroundValidation, Long backgroundValidationMillis, Boolean useFastFail,
         Extension validConnectionChecker, String checkValidConnectionSql, Boolean validateOnMatch,
         Extension staleConnectionChecker, Extension exceptionSorter) throws Exception
   {
      validation = new ValidationImpl(backgroundValidation, backgroundValidationMillis, useFastFail,
            validConnectionChecker, checkValidConnectionSql, validateOnMatch,
            staleConnectionChecker, exceptionSorter);
      return this;
   }
   
   public LegacyXaDataSourceImp buildCommonPool(Integer minPoolSize, Integer maxPoolSize, 
         Boolean prefill, Boolean useStrictMin,
         FlushStrategy flushStrategy) throws Exception
   {
      xaPool = new CommonXaPoolImpl(minPoolSize, maxPoolSize, prefill, useStrictMin, flushStrategy,
            isSameRmOverride, interleaving, padXid,
            wrapXaDataSource, noTxSeparatePool);
      return this;
   }
   
   public LegacyXaDataSourceImp buildOther(String urlDelimiter, String urlSelectorStrategyClassName, String newConnectionSql, 
         Boolean useJavaContext, String poolName, Boolean enabled, String jndiName, 
         Boolean spy, Boolean useCcm, Boolean jta)
   {
      this.urlDelimiter = urlDelimiter;
      this.urlSelectorStrategyClassName = urlSelectorStrategyClassName;
      this.newConnectionSql = newConnectionSql;
      this.useJavaContext = useJavaContext;
      this.poolName = poolName;
      this.enabled = enabled;
      this.jndiName = jndiName;
      this.spy = spy;
      this.useCcm = useCcm;
      this.jta = jta;
      return this;
   }
   

   @Override
   public String getJndiName()
   {
      return this.jndiName;
   }

   @Override
   public Boolean isUseJavaContext()
   {
      return this.useJavaContext;
   }

   @Override
   public String getUrlDelimiter()
   {
      return this.urlDelimiter;
   }

   @Override
   public String getUrlSelectorStrategyClassName()
   {
      return this.urlSelectorStrategyClassName;
   }

   @Override
   public String getUserName()
   {
      return this.security.getUserName();
   }

   @Override
   public String getPassword()
   {
      return this.security.getPassword();
   }

   @Override
   public String getSecurityDomain()
   {
      return null;
   }

   @Override
   public Integer getMinPoolSize()
   {
      return this.xaPool.getMinPoolSize();
   }

   @Override
   public Integer getMaxPoolSize()
   {
      return this.xaPool.getMaxPoolSize();
   }

   @Override
   public Long getBlockingTimeoutMillis()
   {
      return this.timeOut.getBlockingTimeoutMillis();
   }

   @Override
   public Boolean isBackgroundValidation()
   {
      return this.validation.isBackgroundValidation();
   }

   @Override
   public Long getBackgroundValidationMillis()
   {
      return this.validation.getBackgroundValidationMillis();
   }

   @Override
   public Long getIdleTimeoutMinutes()
   {
      return this.timeOut.getIdleTimeoutMinutes();
   }

   @Override
   public Integer getAllocationRetry()
   {
      return this.getAllocationRetry();
   }

   @Override
   public Long getAllocationRetryWaitMillis()
   {
      return this.getAllocationRetryWaitMillis();
   }

   @Override
   public Boolean isValidateOnMatch()
   {
      return this.validation.isValidateOnMatch();
   }

   @Override
   public String getNewConnectionSql()
   {
      return this.getNewConnectionSql();
   }

   @Override
   public String getCheckValidConnectionSql()
   {
      return this.validation.getCheckValidConnectionSql();
   }

   @Override
   public Extension getValidConnectionChecker()
   {
      return this.validation.getValidConnectionChecker();
   }

   @Override
   public Extension getExceptionSorter()
   {
      return null;
   }

   @Override
   public Extension getStaleConnectionChecker()
   {
      return null;
   }

   @Override
   public TrackStatementsEnum getTrackStatements()
   {

      return this.getTrackStatements();
   }

   @Override
   public Boolean isPrefill()
   {
      return this.isPrefill();
   }

   @Override
   public Boolean isUseFastFail()
   {
      return this.validation.isUseFastFail();
   }

   @Override
   public Long getPreparedStatementsCacheSize()
   {
      return this.getPreparedStatementsCacheSize();
   }

   @Override
   public Boolean isSharePreparedStatements()
   {
      return this.isSharePreparedStatements();
   }

   @Override
   public Boolean isSetTxQueryTimeout()
   {
      return this.timeOut.isSetTxQueryTimeout();
   }

   @Override
   public Long getQueryTimeout()
   {
      return this.timeOut.getQueryTimeout();
   }

   @Override
   public Long getUseTryLock()
   {
      return this.getUseTryLock();
   }

   @Override
   public Boolean isNoTxSeparatePools()
   {
      return this.isNoTxSeparatePools();
   }

   
   @Override
   public Boolean isTrackConnectionByTx()
   {
      return this.isTrackConnectionByTx();
   }

   @Override
   public Integer getXaResourceTimeout()
   {
      return this.getXaResourceTimeout();
   }

   @Override
   public String getXaDataSourceClass()
   {

      return this.getXaDataSourceClass();
   }

   @Override
   public Map<String, String> getXaDataSourceProperty()
   {
      return this.getXaDataSourceProperty();
   }

   @Override
   public Boolean isSameRmOverride()
   {
      return this.isSameRmOverride;
   }

   @Override
   public Boolean isInterleaving()
   {
      return this.isInterleaving();
   }

   @Override
   public Boolean isPadXid()
   {
      return this.isPadXid();
   }

   @Override
   public Boolean isWrapXaResource()
   {
      return this.isWrapXaResource();
   }

   @Override
   public Boolean isNoTxSeparatePool()
   {
      return this.isNoTxSeparatePool();
   }

}
