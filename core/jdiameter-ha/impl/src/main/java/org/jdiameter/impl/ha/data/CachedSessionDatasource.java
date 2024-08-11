package org.jdiameter.impl.ha.data;

import org.infinispan.client.hotrod.RemoteCache;
import org.jdiameter.client.api.IContainer;

public interface CachedSessionDatasource
{
    RemoteCache<String, String> getDataCache();

    IContainer getContainer();

    <T> T getFieldValue(String sessionId, String fieldName);

    void setFieldValue(String sessionId, String fieldName, Object value);

    void removeSession(String sessionId);
}
