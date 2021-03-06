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

package org.jboss.jca.core.workmanager.unit;

import org.jboss.jca.core.api.workmanager.DistributedWorkManager;
import org.jboss.jca.core.workmanager.rars.dwm.WorkConnection;
import org.jboss.jca.core.workmanager.rars.dwm.WorkConnectionFactory;
import org.jboss.jca.embedded.arquillian.Configuration;
import org.jboss.jca.embedded.arquillian.Inject;
import org.jboss.jca.embedded.dsl.InputStreamDescriptor;

import java.util.UUID;

import javax.annotation.Resource;
import javax.resource.spi.BootstrapContext;
import javax.resource.spi.work.DistributableWork;
import javax.resource.spi.work.Work;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.logging.Logger;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.jboss.shrinkwrap.api.spec.ResourceAdapterArchive;

import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * DistributedWorkManagerTestCase.
 *
 * Tests for the JBoss specific distributed work manager functionality.
 *
 * @author <a href="mailto:jesper.pedersen@jboss.org">Jesper Pedersen</a>
 */
@RunWith(Arquillian.class)
@Configuration(autoActivate = false)
public class DistributedWorkManagerSocketTestCase
{
   private static Logger log = Logger.getLogger(DistributedWorkManagerSocketTestCase.class);

   @Resource(mappedName = "java:/eis/WorkConnectionFactory")
   private WorkConnectionFactory wcf;

   @Inject(name = "DistributedWorkManager1")
   private DistributedWorkManager dwm1;

   @Inject(name = "DistributedBootstrapContext1")
   private BootstrapContext dbc1;

   @Inject(name = "DistributedWorkManager2")
   private DistributedWorkManager dwm2;

   @Inject(name = "DistributedBootstrapContext2")
   private BootstrapContext dbc2;

   // --------------------------------------------------------------------------------||
   // Deployments --------------------------------------------------------------------||
   // --------------------------------------------------------------------------------||

   /**
    * Define the distributed work manager deployment
    * @return The deployment archive
    */
   @Deployment(name = "DWM", order = 1)
   public static InputStreamDescriptor createDistributedWorkManagerDeployment()
   {
      ClassLoader cl = Thread.currentThread().getContextClassLoader();
      InputStreamDescriptor isd = new InputStreamDescriptor("dwm-socket.xml",
                                                            cl.getResourceAsStream("dwm-socket.xml"));
      return isd;
   }

   /**
    * Define the resource adapter deployment
    * @return The deployment archive
    */
   @Deployment(name = "RAR", order = 2)
   public static ResourceAdapterArchive createArchiveDeployment()
   {
      ResourceAdapterArchive raa =
         ShrinkWrap.create(ResourceAdapterArchive.class, "work.rar");

      JavaArchive ja = ShrinkWrap.create(JavaArchive.class, UUID.randomUUID().toString() + ".jar");
      ja.addPackage(WorkConnectionFactory.class.getPackage());

      raa.addAsLibrary(ja);
      raa.addAsManifestResource("rars/dwm/META-INF/ra.xml", "ra.xml");

      return raa;
   }

   /**
    * Define the activation deployment
    * @return The deployment archive
    */
   @Deployment(name = "ACT", order = 3)
   public static InputStreamDescriptor createActivationDeployment()
   {
      ClassLoader cl = Thread.currentThread().getContextClassLoader();
      InputStreamDescriptor isd = new InputStreamDescriptor("dwm-invm-ra.xml",
                                                            cl.getResourceAsStream("dwm-invm-ra.xml"));
      return isd;
   }

   // --------------------------------------------------------------------------------||
   // Tests --------------------------------------------------------------------------||
   // --------------------------------------------------------------------------------||

   /**
    * Test that the used distributed work managers are an instance of the
    * <code>javax.resource.spi.work.DistributableWorkManager</code> interface
    * @throws Throwable throwable exception
    */
   @Test
   public void testInstanceOf() throws Throwable
   {
      assertNotNull(dwm1);
      assertTrue(dwm1 instanceof javax.resource.spi.work.DistributableWorkManager);

      assertNotNull(dwm2);
      assertTrue(dwm2 instanceof javax.resource.spi.work.DistributableWorkManager);
   }

   /**
    * Test that the used distributed work managers are configured
    * @throws Throwable throwable exception
    */
   @Test
   public void testConfigured() throws Throwable
   {
      assertNotNull(dwm1);
      assertNotNull(dwm1.getPolicy());
      assertNotNull(dwm1.getSelector());
      assertNotNull(dwm1.getTransport());

      assertNotNull(dwm2);
      assertNotNull(dwm2.getPolicy());
      assertNotNull(dwm2.getSelector());
      assertNotNull(dwm2.getTransport());

      assertNotNull(dbc1);
      assertNotNull(dbc2);
   }

   /**
    * Test that a work instance can be executed
    * @throws Throwable throwable exception
    */
   @Test
   public void testExecuted() throws Throwable
   {
      assertNotNull(wcf);

      WorkConnection wc = wcf.getConnection();
      wc.doWork(new MyWork());
      wc.doWork(new MyDistributableWork());

      assertEquals(1, dwm1.getStatistics().getWorkSuccessful());
      assertEquals(1, dwm2.getStatistics().getWorkSuccessful());

      wc.close();
   }

   // --------------------------------------------------------------------------------||
   // Helper classes -----------------------------------------------------------------||
   // --------------------------------------------------------------------------------||
   /**
    * Work
    */
   public static class MyWork implements Work
   {
      /**
       * {@inheritDoc}
       */
      public void run()
      {
         log.info("MyWork: run");
      }

      /**
       * {@inheritDoc}
       */
      public void release()
      {
         log.info("MyWork: release");
      }
   }

   /**
    * DistributableWork
    */
   public static class MyDistributableWork implements DistributableWork
   {
      /** Serial version uid */
      private static final long serialVersionUID = 1L;

      /**
       * {@inheritDoc}
       */
      public void run()
      {
         log.info("MyDistributableWork: run");
      }

      /**
       * {@inheritDoc}
       */
      public void release()
      {
         log.info("MyDistributableWork: release");
      }
   }
}
