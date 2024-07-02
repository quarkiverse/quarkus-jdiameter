package io.go.diameter;

import jakarta.enterprise.util.AnnotationLiteral;
import jakarta.inject.Qualifier;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.util.Objects;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target({TYPE, FIELD, METHOD, PARAMETER})
@Retention(RUNTIME)
@Documented
@Qualifier
public @interface DiameterClient
{
	String value() default "<default>";

	@SuppressWarnings("ClassExplicitlyAnnotation")
	final class DiameterClientLiteral extends AnnotationLiteral<DiameterClient> implements DiameterClient
	{

		private final String name;

		public DiameterClientLiteral(String name)
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
			if (!(o instanceof DiameterClientLiteral that)) return false;
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
