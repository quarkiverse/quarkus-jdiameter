package io.go.diameter;

import jakarta.enterprise.util.AnnotationLiteral;
import jakarta.inject.Qualifier;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.util.Objects;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target({TYPE, FIELD, METHOD, PARAMETER, PACKAGE})
@Retention(RUNTIME)
@Documented
@Qualifier
public @interface DiameterConfig
{
	String DEFAULT_CONFIG_NAME = "<default>";

	String value() default DEFAULT_CONFIG_NAME;

	@SuppressWarnings("ClassExplicitlyAnnotation")
	final class DiameterConfigLiteral extends AnnotationLiteral<DiameterConfig> implements DiameterConfig
	{

		private final String name;

		public DiameterConfigLiteral(String name)
		{
			this.name = name;
		}

		@Override
		public String value()
		{
			return name;
		}

		@Override
		public boolean equals(Object o)
		{
			if (this == o) return true;
			if (!(o instanceof DiameterConfigLiteral that)) return false;
			if (!super.equals(o)) return false;

			return Objects.equals(name, that.name);
		}

		@Override
		public int hashCode()
		{
			int result = super.hashCode();
			result = 31 * result + (name != null ? name.hashCode() : 0);
			return result;
		}
	}
}
