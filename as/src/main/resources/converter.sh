#!/bin/sh
java -classpath ./ironjacamar-as.jar:../../lib/jboss-logging.jar:../../lib/jboss-common-core.jar:../../lib/ironjacamar-spec-api.jar:../../lib/papaki-core.jar:../../lib/javassist.jar:../../lib/ironjacamar-common-impl.jar:../../lib/ironjacamar-common-impl-papaki.jar:../../lib/ironjacamar-common-api.jar:../../lib/ironjacamar-common-spi.jar org.jboss.jca.as.converters.Main $*
