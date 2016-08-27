package nl.javalon.groufty.resource;

/**
 * Resource helper class. 
 * Providing tools for checking the existence of resources and triggering the appropriate exceptions.
 * @author Melcher
 *
 */
public class RestResourceHelper {

	private static final String DEFAULT_404_MESSAGE = "The requested resource could not be found.";
	private static final String DEFAULT_400_MESSAGE = "The request was malformed";

	/**
	 * Throws a {@link ResourceNotFoundException} if the provided resource is null with a default message.
	 * @param resource the resource to test
	 * @param <T> the resource
	 * @return the resource, if not null.
	 */
	public static <T> T checkFound(T resource) {
		return checkFound(resource, DEFAULT_404_MESSAGE);
	}

	/**
	 * Throws a {@link ResourceNotFoundException} if the provided resource is null, with the provided message
	 * @param resource the resource to test
	 * @param message the message to throw on null
	 * @param <T> the resource type
	 * @return the resource, if not null
	 */
	public static <T> T checkFound(T resource, String message) {
        if (resource == null) {
            throw new ResourceNotFoundException(message);
        }
        return resource;
    }
	
	public static <T> void checkNotNull(T resource) {
        if (resource == null) {
            throw new BadRequestException(DEFAULT_400_MESSAGE);
        }
	}
}
