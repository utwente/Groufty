package nl.javalon.groufty.dto;

import lombok.NonNull;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.Iterator;
import java.util.List;

/**
 * Performs pagination in Java-code. Ignores sort.
 * A SimplePage encapsulates all elements and only shows those for the provided pageable.
 * @author Lukas Miedema
 */
public class SimplePage<T> implements Page<T> {
	
	private final Pageable pageable;
	private final List<T> elements;

	/**
	 * Creates a new simple page
	 * @param pageable the page to display
	 * @param elements all elements for this resource
	 */
	public SimplePage(@NonNull Pageable pageable, @NonNull List<T> elements) {
		this.pageable = pageable;
		this.elements = elements;
	}

	/**
	 * Returns the number of total pages.
	 * @return the number of total pages
	 */
	@Override
	public int getTotalPages() {
		int pageSize = pageable.getPageSize();
		int elemSize = elements.size();
		int pageCount = elemSize / pageSize;
		if (pageCount * pageSize < elemSize) {
			pageCount++;
		}
		return pageCount;
	}

	/**
	 * Returns the total amount of elements.
	 * @return the total amount of elements
	 */
	@Override
	public long getTotalElements() {
		return elements.size();
	}

	/**
	 * Returns the number of the current {@link Page}. Is always non-negative.
	 * @return the number of the current {@link Page}.
	 */
	@Override
	public int getNumber() {
		return pageable.getPageNumber();
	}

	/**
	 * Returns the size of the {@link Page}.
	 * @return the size of the {@link Page}.
	 */
	@Override
	public int getSize() {
		return pageable.getPageSize();
	}

	/**
	 * Returns the number of elements currently on this {@link Page}.
	 * @return the number of elements currently on this {@link Page}.
	 */
	@Override
	public int getNumberOfElements() {
		int elemsRemaining = Math.max(elements.size() - pageable.getPageNumber() * pageable.getPageSize(), 0);
		return Math.min(elemsRemaining, pageable.getPageSize());
	}

	/**
	 * Returns the page content as {@link List}.
	 *
	 * @return
	 */
	@Override
	public List<T> getContent() {
		int fromIndex = Math.min(pageable.getPageNumber() * pageable.getPageSize(), elements.size());
		int toIndex = Math.min((pageable.getPageNumber() + 1) * pageable.getPageSize(), elements.size());
		return elements.subList(fromIndex, toIndex);
	}

	/**
	 * Returns whether the {@link Page} has content at all.
	 *
	 * @return
	 */
	@Override
	public boolean hasContent() {
		return getNumberOfElements() > 0;
	}

	/**
	 * Returns the sorting parameters for the {@link Page}.
	 *
	 * @return
	 */
	@Override
	public Sort getSort() {
		return null;
	}

	/**
	 * Returns whether the current {@link Page} is the first one.
	 *
	 * @return
	 */
	@Override
	public boolean isFirst() {
		return pageable.getPageNumber() == 0;
	}

	/**
	 * Returns whether the current {@link Page} is the last one.
	 *
	 * @return
	 */
	@Override
	public boolean isLast() {
		return pageable.getPageNumber() + 1 == getTotalPages();
	}

	/**
	 * Returns if there is a next {@link Page}.
	 * @return if there is a next {@link Page}.
	 */
	@Override
	public boolean hasNext() {
		return !isLast();
	}

	/**
	 * Returns if there is a previous {@link Page}.
	 * @return if there is a previous {@link Page}.
	 */
	@Override
	public boolean hasPrevious() {
		return !isFirst();
	}

	/**
	 * Returns the {@link Pageable} to request the next {@link Pageable}. Can be {@literal null} in case the current
	 * {@link Page} is already the last one. Clients should check {@link #hasNext()} before calling this method to make
	 * sure they receive a non-{@literal null} value.
	 *
	 * @return
	 */
	@Override
	public Pageable nextPageable() {
		return null;
	}

	/**
	 * Returns the {@link Pageable} to request the previous {@link Page}. Can be {@literal null} in case the current
	 * {@link Page} is already the first one. Clients should check {@link #hasPrevious()} before calling this method make
	 * sure receive a non-{@literal null} value.
	 *
	 * @return
	 */
	@Override
	public Pageable previousPageable() {
		return null;
	}

	/**
	 * Returns a new {@link Page} with the content of the current one mapped by the given {@link Converter}.
	 * @param converter must not be {@literal null}.
	 * @return a new {@link Page} with the content of the current one mapped by the given {@link Converter}.
	 * @since 1.10
	 */
	@Override
	public <S> Page<S> map(Converter<? super T, ? extends S> converter) {
		return null;
	}

	/**
	 * Returns an iterator over elements of type {@code T}.
	 * @return an Iterator.
	 */
	@Override
	public Iterator<T> iterator() {
		return getContent().iterator();
	}
}
