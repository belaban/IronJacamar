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

package org.jboss.jca.test.deployers.spec.rars.testcases;

import org.jboss.jca.test.deployers.spec.ArquillianJCATestUtils;
import org.jboss.jca.test.deployers.spec.rars.multiple.MultipleAdminObject1;
import org.jboss.jca.test.deployers.spec.rars.multiple.MultipleAdminObject2;
import org.jboss.jca.test.deployers.spec.rars.multiple.MultipleConnectionFactory1;
import org.jboss.jca.test.deployers.spec.rars.multiple.MultipleConnectionFactory2;

import javax.annotation.Resource;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.spec.ResourceAdapterArchive;

import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertNotNull;

/**
 * Test cases for deploying resource adapter archives (.RAR) with multiple
 * mananged connection factories and admin objects
 *
 * @author <a href="mailto:jesper.pedersen@jboss.org">Jesper Pedersen</a>
 */
@RunWith(Arquillian.class)
public class MultipleFullTestCase
{

   //-------------------------------------------------------------------------------------||
   //---------------------- GIVEN --------------------------------------------------------||
   //-------------------------------------------------------------------------------------||
   /**
    * Define the deployment
    * @return The deployment archive
    * @throws Exception in case of errors
    */
   @Deployment
   public static ResourceAdapterArchive createDeployment() throws Exception
   {
      String archiveName = "multiple-full.rar";
      String packageName = "org.jboss.jca.test.deployers.spec.rars.multiple";
      ResourceAdapterArchive raa = ArquillianJCATestUtils.buidShrinkwrapRa(archiveName, packageName);
      raa.addAsManifestResource(archiveName + "/META-INF/ra.xml", "ra.xml");
      raa.addAsManifestResource(archiveName + "/META-INF/ironjacamar.xml", "ironjacamar.xml");

      return raa;
   }

   //-------------------------------------------------------------------------------------||
   //---------------------- WHEN  --------------------------------------------------------||
   //-------------------------------------------------------------------------------------||
   //
   @Resource(mappedName = "java:/eis/MultipleConnectionFactory1")
   private MultipleConnectionFactory1 connectionFactory1;

   @Resource(mappedName = "java:/eis/MultipleConnectionFactory2")
   private MultipleConnectionFactory2 connectionFactory2;

   @Resource(mappedName = "java:/eis/MultipleAdminObject1")
   private MultipleAdminObject1 adminObject1;

   @Resource(mappedName = "java:/eis/MultipleAdminObject2")
   private MultipleAdminObject2 adminObject2;

   //-------------------------------------------------------------------------------------||
   //---------------------- THEN  --------------------------------------------------------||
   //-------------------------------------------------------------------------------------||

   /**
    * Basic
    * @exception Throwable Thrown if case of an error
    */
   @Test
   public void testBasic() throws Throwable
   {
      assertNotNull(connectionFactory1);
      assertNotNull(connectionFactory2);
      assertNotNull(adminObject1);
      assertNotNull(adminObject2);
   }
}
