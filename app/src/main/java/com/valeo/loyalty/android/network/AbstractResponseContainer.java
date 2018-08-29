package com.valeo.loyalty.android.network;


import com.valeo.loyalty.android.network.exception.ApiException;
import com.valeo.loyalty.android.network.exception.DataRequestException;

/**
 * Base container class for network responses.
 * @param <T>   type of contained data
 * @param <R>   type of result data
 */
abstract class AbstractResponseContainer<T, R> {

	private final T data;
	private final DataRequestException externalException;

	/**
	 * Creates an instance.
	 * @param data                  incoming raw data
	 * @param externalException     the associated exception, if any
	 */
	AbstractResponseContainer(T data, DataRequestException externalException) {
		this.data = data;
		this.externalException = externalException;
	}

	/**
	 * Extracts result from raw data.
	 * @param   data  raw data
	 * @return  extracted result
	 * @throws  ApiException    if the result cannot be extracted
	 */
	protected abstract R extractResult(T data) throws ApiException;

	/**
	 * Gets the data contained in the response.
	 * @return  response data.
	 * @throws DataRequestException     if response is invalid or an exception happened while
	 * it was obtained.
	 */
	public R getData() throws DataRequestException {
		throwExceptionIfNeeded();
		return extractResult(data);
	}

	private void throwExceptionIfNeeded() throws DataRequestException {
		if (externalException != null) {
			throw externalException;
		}
	}
}
