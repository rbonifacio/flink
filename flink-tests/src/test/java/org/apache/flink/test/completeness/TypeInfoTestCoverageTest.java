/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.flink.test.completeness;

import java.lang.reflect.Modifier;
import java.util.Set;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.api.common.typeutils.TypeInformationTestBase;
import org.apache.flink.util.TestLogger;
import static org.junit.Assert.assertTrue;
import org.junit.Test;
import org.reflections.Reflections;

/**
 * Scans the class path for type information and checks if there is a test for it.
 */
public class TypeInfoTestCoverageTest extends TestLogger {

	@Test
	public void testTypeInfoTestCoverage() {
		Reflections reflections = new Reflections("org.apache.flink");

		Set<Class<? extends TypeInformation>> typeInfos = reflections.getSubTypesOf(TypeInformation.class);
		Set<Class<? extends TypeInformationTestBase>> typeInfoTests = reflections.getSubTypesOf(TypeInformationTestBase.class);

		// check if a test exists for each type information
		for (Class<? extends TypeInformation> typeInfo : typeInfos) {
			// we skip abstract classes and inner classes to skip type information defined in test classes
			if (Modifier.isAbstract(typeInfo.getModifiers()) ||
					Modifier.isPrivate(typeInfo.getModifiers()) ||
					typeInfo.getName().contains("Test$") ||
					typeInfo.getName().contains("TestBase$") ||
					typeInfo.getName().contains("ITCase$") ||
					typeInfo.getName().contains("$$anon")) {
				continue;
			}
			boolean found = false;
			for (Class<? extends TypeInformationTestBase> typeInfoTest : typeInfoTests) {
				if (typeInfoTest.getName().equals(typeInfo.getName() + "Test")) {
					found = true;
				}
			}
			assertTrue("Could not find test that corresponds to " + typeInfo.getName(), found);
		}
	}
}
