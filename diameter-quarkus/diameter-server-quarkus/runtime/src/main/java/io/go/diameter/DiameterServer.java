package io.go.diameter;

import jakarta.enterprise.util.AnnotationLiteral;
import jakarta.inject.Qualifier;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target({TYPE, FIELD, METHOD, PARAMETER})
@Retention(RUNTIME)
@Documented
@Qualifier
public @interface DiameterServer
{
	@SuppressWarnings("ClassExplicitlyAnnotation")
	final class DiameterServerLiteral extends AnnotationLiteral<DiameterServer> implements DiameterServer
	{
		@Override
		public boolean equals(Object o)
		{
			if (this == o) return true;
			if (!(o instanceof DiameterServerLiteral)) return false;
			return super.equals(o);
		}

		@Override
		public int hashCode()
		{
			int result = super.hashCode();
			result = 31 * result;
			return result;
		}
	}
}
