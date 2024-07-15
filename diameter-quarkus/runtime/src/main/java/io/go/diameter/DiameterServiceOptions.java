package io.go.diameter;


import org.jdiameter.api.ApplicationId;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target({TYPE})
@Retention(RUNTIME)
@Documented
public @interface DiameterServiceOptions
{
	/**
	 * Specifies the diameter configuration name
	 */
	String config() default DiameterConfig.DEFAULT_CONFIG_NAME;

	DiameterApplication type();

	ApplicationMode mode();

	/**
	 * Specifies the vendor ID for application definition.
	 */
	long vendorId() default ApplicationId.UNDEFINED_VALUE;

	/**
	 * The Authentication Application ID for application definition.
	 */
	long authApplId() default ApplicationId.UNDEFINED_VALUE;

	/**
	 * The Account Application ID for application definition.
	 */
	long acctApplId() default ApplicationId.UNDEFINED_VALUE;
}
