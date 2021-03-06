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
package org.jboss.jca.common.metadata.ds;

import org.jboss.jca.common.api.metadata.ds.DataSources;
import org.jboss.jca.common.metadata.ds.v11.DsParser;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;

import org.jboss.logging.Logger;

import org.junit.Test;

/**
 * Test case for parsing XML representation based upon metadata model's toString
 *
 * @author <a href="jesper.pedersen@jboss.org">Jesper Pedersen</a>
 */
public class DsParserXMLTestCase
{
   private static Logger log = Logger.getLogger(DsParserXMLTestCase.class);

   /**
    * shouldParseXMLRepresentation
    * @throws Exception in case of error
    */
   @Test
   public void shouldParseXMLRepresentation() throws Exception
   {
      FileInputStream is = null;
      ByteArrayInputStream bais = null;

      //given
      ClassLoader cl = Thread.currentThread().getContextClassLoader();
      File xmlFile = new File(cl.getResource("ds/unit/all-ds.xml").toURI());
      try
      {
         is = new FileInputStream(xmlFile);

         DsParser parser = new DsParser();

         //when
         DataSources ds1 = parser.parse(is);

         String xmlRepresentation = ds1.toString();

         log.debug(xmlRepresentation);

         bais = new ByteArrayInputStream(xmlRepresentation.getBytes("UTF-8"));

         DataSources ds2 = parser.parse(bais);
      }
      finally
      {
         if (is != null)
            is.close();

         if (bais != null)
            bais.close();
      }
   }
}
