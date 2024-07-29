package io.quarkiverse.diameter;


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
    String value() default DiameterConfig.DEFAULT_CONFIG_NAME;
}
