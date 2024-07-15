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
import jakarta.persistence.LockModeType;
import jakarta.persistence.PersistenceException;
import jakarta.persistence.spi.LoadState;

import java.io.Serializable;
import java.sql.ResultSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

@SuppressWarnings("java:S100")//have methods starting "_" to prevent clashing with methods defined in the entities
public interface JPAEntity extends Serializable
{
	/**
	 * Return the metadata for the entity
	 *
	 * @return The Entity Metadata
	 */
	@SuppressWarnings("java:S1452")
	//Generic wildcard is required
	EntityMetaData<?> _getMetaData();

	/**
	 * Return the entity class associated with the entity
	 *
	 * @return The Class<?>
	 */
	Class<?> _getEntityClass();

	/**
	 * Return a set of all the modified fields
	 *
	 * @return The set
	 */
	Set<String> _getModifiedFields();
	
	/**
	 * Clear both the update and snapshot modification flags.
	 */
	void _clearModified();

	/**
	 * Return the load state of the entity
	 *
	 * @return {@link LoadState}
	 */
	LoadState _loadState();

	/**
	 * Test to verify if a specified field was modified
	 *
	 * @param fieldName The field
	 * @return True if the field was modified
	 */
	boolean _isFieldModified(String fieldName);

	/**
	 * Mark a specified field as modified.
	 *
	 * @param fieldName The field to mark
	 */
	void _clearField(String fieldName);

	/**
	 * Mark a specified field as modified.
	 *
	 * @param fieldName The field to mark
	 */
	void _markField(String fieldName);

	/**
	 * Check to verify if there are any modified fields
	 *
	 * @return True any fields were modified
	 */
	boolean _isEntityModified();

	/**
	 * Get the lock mode for the entity
	 *
	 * @return The {@link LockModeType} value
	 */
	LockModeType _getLockMode();

	/**
	 * Set the entity's lock mode
	 *
	 * @param lockMode The {@link LockModeType} value assigned to the entity
	 */
	void _setLockMode(LockModeType lockMode);

	/**
	 * Get the current state of the entity
	 *
	 * @return The entity state {@link EntityState}
	 */
	EntityState _getEntityState();

	/**
	 * Change the entity's state
	 *
	 * @param newState The new state as per {@link EntityState}
	 */
	void _setEntityState(EntityState newState);

	/**
	 * Set the PersistenceContext associated with the entity if the state is <b>EntityState.ATTACHED</b>
	 *
	 * @return The PersistenceContext. If the entity state is not <b>EntityState.ATTACHED</b> the result is
	 * undetermined.
	 */
	PersistenceContext _getPersistenceContext();

	/**
	 * Set the PersistenceContext. Setting this value will change the entity state to ATTACHED. Setting the
	 * PersistenceContext to null will changed the entity state to DETACHED.
	 *
	 * @param persistenceContext The entity manager the entity is attached to
	 */
	void _setPersistenceContext(PersistenceContext persistenceContext);

	/**
	 * Get the current pending action for the entity
	 *
	 * @return The {@link PersistenceAction} value
	 */
	PersistenceAction _getPendingAction();

	/**
	 * Set the pending action
	 *
	 * @param pendingAction the {@link PersistenceAction} value to assign
	 */
	void _setPendingAction(PersistenceAction pendingAction);

	/**
	 * Get the value of a specific field converted to database format
	 *
	 * @param fieldName The field name
	 * @return The value assigned to the value
	 */
	<X> X _getDBValue(@Nonnull String fieldName);

	/**
	 * Allow the caller to update a restricted field (VERSION and NON-UPDATABLE).
	 * This purpose of this method is for internal use and only be used if you know what you are doing
	 *
	 * @param method to invoke
	 */
	void _updateRestrictedField(Consumer<JPAEntity> method);

	/**
	 * Merged the supplied entity into the this one.
	 *
	 * @param entity - the entity
	 */
	void _merge(JPAEntity entity);

	/**
	 * Return the primary key for the entity. The returned object should not be modified and must be seen as immutable.
	 *
	 * @return An instance of the primary key.
	 */
	Object _getPrimaryKey();

	/**
	 * Set the entity's id fields equal to the primary key object. This can only be done on a new entity that has a
	 * TRANSIENT state.
	 *
	 * @param primaryKey The primary key object
	 */
	void _setPrimaryKey(Object primaryKey);

	/**
	 * Check if the entity was loaded only by reference.
	 *
	 * @return True if lazy loaded
	 */
	boolean _isLazyLoaded();

	/**
	 * Check if the field in entity is lazily loaded
	 *
	 * @param fieldName name of field whose load state is to be determined
	 * @return false if the field state has not been loaded, else true
	 */
	boolean _isLazyLoaded(String fieldName);

	/**
	 * Mark an entity as being loaded lazily.
	 */
	void _markLazyLoaded();

	/**
	 * Create mark a new entity as a reference
	 *
	 * @param primaryKey The key
	 */
	void _makeReference(Object primaryKey);

	/**
	 * Force a load of all fields that are lazy loaded
	 */
	void _lazyFetchAll(boolean forceEagerLoad);

	/**
	 * Reload a specific field or a whole entity from database
	 *
	 * @param fieldName The specific field to refresh, if Null the entity is reloaded
	 */
	void _lazyFetch(String fieldName);

	/**
	 * Clone the entity into a new entity. The new entity will be  in a transient state where all the fields are set to
	 * the values found the cloned entity. Note that identity and version fields are not cloned.
	 *
	 * @return The cloned entity
	 */
	JPAEntity _clone();

	/**
	 * Copy the content of entity to the current one replacing all values and states. After the copy, entity will be
	 * detached from the context and this entity will be attached to the context. The current entity cannot be attached
	 * and entity must be attached
	 *
	 * @param entity The entity to copy from
	 */
	void _replaceWith(JPAEntity entity);

	/**
	 * Reload an entity replacing all values
	 *
	 * @param properties The query properties
	 */
	void _refreshEntity(Map<String, Object> properties);

	/**
	 * Take the given result set and read and  set all the fields in the entity from it. The dirty flags for the fields
	 * read from the result set are cleared and any unflushed change to the field is lost. The colPrefix value is used
	 * to map PSQL queries
	 *
	 * @param colPrefix the column prefix
	 * @param resultSet the result set
	 * @throws PersistenceException If there has been an error reading the fields
	 */
	void _mapResultSet(String colPrefix, ResultSet resultSet);

	/**
	 * Deserialize the entity from a byte array.
	 *
	 * @param bytes the byte array
	 */
	void _deserialize(byte[] bytes);

	/**
	 * Serialise the entity into a byte array and return the array
	 *
	 * @return the serialised object
	 */
	byte[] _serialize();

	/**
	 * Retrieve content of the JPAEntity as a JSON formatted string*
	 */
	String _toJson();

	/**
	 * Load the entity from a JSON string
	 *
	 * @param jsonStr The json string
	 */
	void _fromJson(String jsonStr);

	/**
	 * Compare the primary keys of the to entities
	 *
	 * @param entity The entity to compare with
	 * @return True if the primary keys are the same
	 */
	boolean _entityEquals(JPAEntity entity);
}//JPAEntity
