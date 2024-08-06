package org.jdiameter.impl.ha.data;

import org.jdiameter.client.api.IContainer;

public interface CachedSessionDatasource
{
    IContainer getContainer();

    <T> T getFieldValue(String sessionId, String fieldName);

    void setFieldValue(String sessionId, String fieldName, Object value);

    void removeSession(String sessionId);

    void setExpiryTime(String sessionId, long expiryTime);
}
