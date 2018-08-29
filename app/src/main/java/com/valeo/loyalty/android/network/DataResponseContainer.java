package com.valeo.loyalty.android.network;

import com.valeo.loyalty.android.network.exception.ApiException;
import com.valeo.loyalty.android.network.exception.DataRequestException;

/**
 * Base container class for network responses.
 * @param <T>   type of contained data
 */
public class DataResponseContainer<T> extends AbstractResponseContainer<T, T> {

	private DataResponseContainer(T response, DataRequestException externalException) {
		super(response, externalException);
	}

	@Override
	protected T extractResult(T response) throws ApiException {
		return response;
	}

	static <T> DataResponseContainer<T> wrap(Throwable exception) {
		return new DataResponseContainer<>(null, new DataRequestException(exception));
	}

	static <T> DataResponseContainer<T> wrap(ApiException exception) {
		return new DataResponseContainer<>(null, exception);
	}

	static <T> DataResponseContainer<T> wrap(T response) {
		DataRequestException exception = (response != null)
			? null : new ApiException("Invalid server response", "");

		return new DataResponseContainer<>(response, exception);
	}
}
