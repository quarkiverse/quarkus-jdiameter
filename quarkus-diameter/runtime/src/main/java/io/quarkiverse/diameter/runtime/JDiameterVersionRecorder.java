/*
 *  Licensed to the Apache Software Foundation (ASF) under one or more
 *  contributor license agreements.  See the NOTICE file distributed with
 *  this work for additional information regarding copyright ownership.
 *  The ASF licenses this file to You under the Apache License, Version 2.0
 *  (the "License"); you may not use this file except in compliance with
 *  the License.  You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package io.quarkiverse.diameter.runtime;

import io.quarkus.runtime.annotations.Recorder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

@Recorder
public class JDiameterVersionRecorder
{
	private static final Logger LOG = LoggerFactory.getLogger(JDiameterVersionRecorder.class);
	public static final String DEFAULT_VERSION = "0.0.0-DEFAULT_VERSION";
	public static final String QUARKUS_JDIAMETER_POM_PROPERTIES = "/META-INF/maven/io.quarkiverse.jdiameter/quarkus-jdiameter/pom.properties";

	public void logVersion() {
		// Read from pom.properties with native fallback
		String version = resolveVersion();
		LOG.info("Quarkus Jdiameter extension loaded — version: {}", version);
	}

	private String resolveVersion() {
		// Try pom.properties first (JVM mode)
		try (InputStream is = getClass().getResourceAsStream(QUARKUS_JDIAMETER_POM_PROPERTIES)) {
			if (is != null) {
				Properties p = new Properties();
				p.load(is);
				return p.getProperty("version", DEFAULT_VERSION);
			}
		} catch (IOException ignored) {}

		// Fallback: MANIFEST.MF Implementation-Version
		String manifestVersion = getClass().getPackage().getImplementationVersion();
		if (manifestVersion != null) return manifestVersion;

		return DEFAULT_VERSION;
	}
}
