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

package io.jpalite;

import jakarta.persistence.CascadeType;
import jakarta.persistence.FetchType;

import java.util.Set;

public interface EntityField
{
	/**
	 * This method is used to get the value of the field from the entity
	 *
	 * @param entity
	 * @return The value retrieved from the entity
	 */
	Object invokeGetter(Object entity);

	/**
	 * This method is used to set the value of the field on the entity
	 *
	 * @param entity The entity to set the value on
	 * @param value  The value to set on the entity
	 */
	void invokeSetter(Object entity, Object value);

	/**
	 * Method to get the class of the entity
	 *
	 * @return The class of the entity
	 */
	Class<?> getEnityClass();

	/**
	 * Method to get the name of the field
	 *
	 * @return The name of the field
	 */
	String getName();

	/**
	 * Method to get the field number
	 *
	 * @return The field number
	 */
	int getFieldNr();

	/**
	 * Method to get the type of the field
	 *
	 * @return The type of the field
	 */
	Class<?> getType();

	/**
	 * Method to get the column name
	 *
	 * @return The column name
	 */
	String getColumn();

	/**
	 * Method to get the mapping type
	 *
	 * @return The mapping type
	 */
	MappingType getMappingType();

	/**
	 * Method to check if the field is unique
	 *
	 * @return True if the field is unique, false otherwise
	 */
	boolean isUnique();

	/**
	 * Method to check if the field is nullable
	 *
	 * @return True if the field is nullable, false otherwise
	 */
	boolean isNullable();

	/**
	 * Method to check if the field is insertable
	 *
	 * @return True if the field is insertable, false otherwise
	 */
	boolean isInsertable();

	/**
	 * Method to check if the field is updatable
	 *
	 * @return True if the field is updatable, false otherwise
	 */
	boolean isUpdatable();

	/**
	 * Method to check if the field is an identity field
	 *
	 * @return True if the field is an identity field, false otherwise
	 */
	boolean isIdField();

	/**
	 * Method to check if the field is a version field
	 *
	 * @return True if the field is a version field, false otherwise
	 */
	boolean isVersionField();

	/**
	 * Method to retrieve the cascade settings for the field
	 *
	 * @return The cascade settings
	 */
	Set<CascadeType> getCascade();

	/**
	 * Method to retrieve the fetch type for the field
	 *
	 * @return The fetch type
	 */
	FetchType getFetchType();

	/**
	 * Method to set the fetch type for the field
	 *
	 * @param fetchType The fetch type
	 */
	void setFetchType(FetchType fetchType);

	/**
	 * Method to retrieve the mapped by value. This value is only set if the mapping type is {@link MappingType#MANY_TO_ONE} or
	 * {@link MappingType#ONE_TO_ONE}
	 *
	 * @return The mapped by value. If no mapped by is set, null is returned
	 */
	String getMappedBy();

	/**
	 * Method to retrieve the column definition. This value is only set if the mapping type is {@link MappingType#BASIC}
	 *
	 * @return The column definition. If no column definition is set, null is returned
	 */
	String getColumnDefinition();

	/**
	 * Method to retrieve the table name. This value is only set if the mapping type is {@link MappingType#BASIC}
	 *
	 * @return The table name. If no table is set, null is returned
	 */
	String getTable();

	/**
	 * Method to retrieve attribute converter class. This value is only set if the mapping type is {@link MappingType#BASIC}
	 *
	 * @return The attribute converter class. If no converter is set, null is returned
	 */
	@SuppressWarnings({"java:S3740", "rawtypes"})
	// Suppress warning for generic types
	FieldConvertType getConverter();

	/**
	 * Determines whether the field is an entity field.
	 *
	 * @return true if the field is an entity field, false otherwise
	 */
	boolean isEntityField();
}
