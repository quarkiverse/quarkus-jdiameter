package org.jdiameter.impl.ha.timer;

import org.infinispan.client.hotrod.annotation.ClientListener;
import org.jdiameter.api.BaseSession;
import org.jdiameter.client.api.IContainer;
import org.jdiameter.client.impl.BaseSessionImpl;
import org.jdiameter.common.api.data.ISessionDatasource;
import org.jdiameter.common.api.timer.ITimerFacility;
import org.jdiameter.common.impl.app.AppSessionImpl;
import org.jdiameter.impl.ha.data.CachedSessionDatasourceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Replicated implementation of {@link ITimerFacility}
 */
@ClientListener
public class ReplicatedTimerFacilityImpl implements ITimerFacility
{
	private static final Logger logger = LoggerFactory.getLogger(ReplicatedTimerFacilityImpl.class);
	public static final String TXTIMER_ID = "TXTIMER_ID";

	private final CachedSessionDatasourceImpl sessionDataSource;
	private final Map<String, TimerTask> timerMap = new ConcurrentHashMap<>();
	private final Timer timer = new Timer();

	public ReplicatedTimerFacilityImpl(IContainer container)
	{
		super();
		ISessionDatasource datasource = container.getAssemblerFacility().getComponentInstance(ISessionDatasource.class);
		if (datasource instanceof CachedSessionDatasourceImpl cachedSessionDatasource) {
			this.sessionDataSource = cachedSessionDatasource;
		} else {
			throw new IllegalArgumentException("ReplicatedTimerFacilityImpl expects an ISessionDatasource of type 'CachedSessionDatasource' and is not compatible with " + datasource.getClass().getName());
		}
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.jdiameter.common.api.timer.ITimerFacility#cancel(java.io.Serializable)
	 */
	@Override
	public void cancel(Serializable id)
	{
		if (id instanceof String timerId) {
			logger.debug("Cancelling timer with id {}", timerId);
			TimerTask task = timerMap.remove(timerId);
			if (task != null) {
				task.cancel();
			}//if
		}
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.jdiameter.common.api.timer.ITimerFacility#schedule(java.lang.String, java.lang.String, long)
	 */
	@Override
	public Serializable schedule(String sessionId, String timerName, long milliseconds) throws IllegalArgumentException
	{
		TimerTaskRunner runner = new TimerTaskRunner(sessionId, timerName, milliseconds);
		timerMap.put(runner.getTimerId(), runner);
		logger.debug("Scheduling timer for sessionId {} and timer id {}", sessionId, runner.getTimerId());
		timer.schedule(runner, milliseconds);

		return runner.getTimerId();
	}

	/**
	 * TimerTaskRunner is a private internal class that extends TimerTask.
	 * It is designed to handle the execution of scheduled timers associated with a session.
	 * Each instance of TimerTaskRunner is uniquely identified by a generated UUID.
	 */
	private class TimerTaskRunner extends TimerTask
	{
		private final String sessionId;
		private final String timerName;
		private final long milliseconds;
		private final String timerId = UUID.randomUUID().toString();

		public TimerTaskRunner(String sessionId, String timerName, long milliseconds)
		{
			this.sessionId    = sessionId;
			this.timerName    = timerName;
			this.milliseconds = milliseconds;
		}

		public String getTimerId()
		{
			return timerId;
		}

		@Override
		public void run()
		{
			if (timerMap.remove(timerId) != null) {
				String sessionTimerId = sessionDataSource.getFieldValue(sessionId, TXTIMER_ID);
				if (timerId.equals(sessionTimerId)) {
					BaseSession session = sessionDataSource.getSession(sessionId);
					if (session != null) {

						logger.debug("Scheduled timer for sessionId {} and timer id {} expired after {}ms", sessionId, timerId, milliseconds);
						try {
							if (!session.isAppSession()) {
								BaseSessionImpl impl = (BaseSessionImpl) session;
								impl.onTimer(timerName);
							} else {
								AppSessionImpl impl = (AppSessionImpl) session;
								impl.onTimer(timerName);
							}
						}
						catch (Exception e) {
							logger.error("Caught exception from session object!", e);
						}
					}
				} else {
					logger.debug("Timer with id {} is not associated with session {}", timerId, sessionId);
				}
			} else {
				logger.debug("Timer with id {} has been cancelled for session {}", timerId, sessionId);
			}
		}
	}
}
