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

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.TimeUnit;

public interface EntityMetaData<T>
{
	/**
	 * Get the entity type
	 *
	 * @return the {@link EntityType} value
	 */
	EntityType getEntityType();

	/**
	 * The entity name
	 *
	 * @return the Entity name
	 */
	String getName();

	/**
	 * The Java Class associated with the entity
	 *
	 * @return The entity class
	 */
	Class<T> getEntityClass();

	/**
	 * Create a new instance of an entity. This is a helper method where "new Entity()" is not an option
	 *
	 * @return The new instance
	 */
	@Nonnull
	T getNewEntity();

	/**
	 * The table linked to the entity
	 *
	 * @return The table name
	 */
	String getTable();

	/**
	 * Retrieve an EntityField for a given field name.
	 *
	 * @param fieldName The field name
	 * @return The EntityField for a field.
	 * @throws UnknownFieldException If the fields does not exist
	 */
	@Nonnull
	EntityField getEntityField(String fieldName);

	/**
	 * Check if the given field name is an entity field
	 *
	 * @param fieldName The field name to check
	 * @return True if a field
	 */
	boolean isEntityField(String fieldName);

	/**
	 * Retrieve an EntityField for a given column name
	 *
	 * @param column The column name
	 * @return The EntityField for a field or null if not found
	 */
	@Nullable
	EntityField getEntityFieldByColumn(String column);

	/**
	 * Retrieve an EntityField for a given field number.
	 *
	 * @param fieldNr The field name
	 * @return The EntityField for a field.
	 * @throws UnknownFieldException If the fields does not exist
	 */
	@Nonnull
	EntityField getEntityFieldByNr(int fieldNr);

	/**
	 * Return the list of all the entity fields in the entity.
	 *
	 * @return The list of fields
	 */
	Collection<EntityField> getEntityFields();

	/**
	 * Return all the listeners for the entity
	 *
	 * @return List of all the listeners
	 */
	EntityLifecycle getLifecycleListeners();

	/**
	 * Return the class used for the primary key
	 *
	 * @return The class of the primary key
	 */
	@Nullable
	@SuppressWarnings("java:S1452")
	//generic wildcard is required
	EntityMetaData<?> getPrimaryKeyMetaData();

	/**
	 * Return a list of all the defined id fields
	 *
	 * @return List of id fields
	 */
	@Nonnull
	List<EntityField> getIdFields();

	/**
	 * True if the entity has more than one ID field
	 *
	 * @return true if there are more than one if field in the entity
	 */
	boolean hasMultipleIdFields();

	/**
	 * Return the first (only) id field. This method can only be used if there is only one id field.
	 *
	 * @return The id field
	 * @throws IllegalArgumentException if the entity have multiple id fields
	 */
	EntityField getIdField();

	/**
	 * Check if the entity can be cached
	 *
	 * @return true if cacheable
	 */
	boolean isCacheable();

	/**
	 * Retrieves the idle time of the entity. The units are dependent the value in {@link #getCacheTimeUnit}
	 *
	 * @return The idle time in.
	 */
	long getIdleTime();

	/**
	 * Retrieves the time unit used for caching.
	 *
	 * @return The {@link TimeUnit} used for caching.
	 */
	TimeUnit getCacheTimeUnit();

	/**
	 * Checks if the entity has a version field.
	 *
	 * @return True if the entity has a version field, false otherwise.
	 */
	boolean hasVersionField();

	/**
	 * Return the metadata for the version field.
	 *
	 * @throws IllegalArgumentException if there is not version field defined
	 */
	EntityField getVersionField();

	/**
	 * Deprecated since version 3.0.0 and marked for removal.
	 * Returns the columns associated with the entity.
	 *
	 * @return The columns as a comma-delimited string.
	 * @deprecated This method is deprecated and will be removed in a future version.
	 */
	@Deprecated(since = "3.0.0", forRemoval = true)
	String getColumns();

	/**
	 * Retrieve the legacy state of the entity. An entity is seen as a legacy entity if there is no @Entity annotation.
	 *
	 * @return True if a legacy entity
	 * @deprecated This method is deprecated and will be removed in a future version.
	 */
	@Deprecated(since = "3.0.0", forRemoval = true)
	boolean isLegacyEntity();

}//EntityMetaData
