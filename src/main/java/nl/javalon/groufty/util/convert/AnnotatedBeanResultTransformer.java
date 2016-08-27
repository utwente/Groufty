package nl.javalon.groufty.util.convert;

import org.hibernate.QueryException;
import org.hibernate.transform.AliasToBeanConstructorResultTransformer;
import org.hibernate.transform.ResultTransformer;
import org.springframework.core.convert.ConversionService;

import java.lang.reflect.Constructor;
import java.util.List;

/**
 * Special {@link AliasToBeanConstructorResultTransformer} which finds an annotated constructor and
 * uses that. Uses a {@link ConversionService} to convert actual to expected types.
 * @author Lukas Miedema
 */
public class AnnotatedBeanResultTransformer<T extends Object> implements ResultTransformer {

	private final ConversionService conversionService;
	private final Constructor<T> constructor;

	/**
	 * Construct a transformer for the provided bean.
	 * @param clazz
	 */
	public AnnotatedBeanResultTransformer(Class<?> clazz, ConversionService conversionService) {
		this.constructor = findAnnotatedConstructor(clazz);
		this.conversionService = conversionService;
	}

	private static Constructor findAnnotatedConstructor(Class<?> clazz) {
		for (Constructor ctor: clazz.getDeclaredConstructors()) {
			if (ctor.getAnnotation(SqlToBeanConstructor.class) != null) {
				return ctor;
			}
		}
		throw new IllegalArgumentException(clazz + " has no SqlToBeanConstructor.");
	}

	/**
	 * Tuples are the elements making up each "row" of the query result.
	 * The contract here is to transform these elements into the final
	 * row.
	 *
	 * @param tuple   The result elements
	 * @param aliases The result aliases ("parallel" array to tuple)
	 * @return The transformed row.
	 */
	@Override
	public T transformTuple(Object[] tuple, String[] aliases) {
		Class<?>[] expected = constructor.getParameterTypes();
		Object[] converted = new Object[tuple.length];

		if (converted.length != expected.length) {
			throw new IllegalArgumentException(
					constructor.getDeclaringClass().getSimpleName() + " expects " + expected.length +
							" arguments but got " + converted.length + " arguments");
		}

		// Convert one by one
		for (int i = 0; i < converted.length; i++) {
			Class<?> expectedParameterClass = expected[i];
			Object parameter = tuple[i];

			if (parameter != null) {

				// Convert only if necessary
				if (expectedParameterClass != parameter.getClass()) {
					parameter = conversionService.convert(parameter, expectedParameterClass);
				}
			}
			converted[i] = parameter;
		}

		try {
			return constructor.newInstance(converted);
		} catch (Exception e) {
			throw new QueryException("Could not instantiate " + constructor.getDeclaringClass().getSimpleName(), e);
		}
	}

	/**
	 * Here we have an opportunity to perform transformation on the
	 * query result as a whole.  This might be useful to convert from
	 * one collection type to another or to remove duplicates from the
	 * result, etc.
	 *
	 * @param collection The result.
	 * @return The transformed result.
	 */
	@Override
	public List transformList(List collection) {
		return collection;
	}
}
