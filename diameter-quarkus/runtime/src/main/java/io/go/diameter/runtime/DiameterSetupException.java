package io.go.diameter.runtime;

public class DiameterSetupException extends RuntimeException
{
	public DiameterSetupException(String message)
	{
		super(message);
	}

	public DiameterSetupException(String message, Throwable cause)
	{
		super(message, cause);
	}

	public DiameterSetupException(Throwable cause)
	{
		super(cause);
	}

	public DiameterSetupException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace)
	{
		super(message, cause, enableSuppression, writableStackTrace);
	}
}
