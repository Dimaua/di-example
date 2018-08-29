package com.valeo.loyalty.android.network;

import android.support.annotation.NonNull;

import com.valeo.loyalty.android.model.ErrorResponse;
import com.valeo.loyalty.android.network.exception.ApiException;

import java.io.IOException;
import java.lang.annotation.Annotation;

import java8.util.function.Consumer;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Converter;
import retrofit2.Response;

/**
 * Wraps response consumer as a Retrofit {@link Callback}.
 * @param <T>   response data type
 */
class RetrofitCallbackAdapter<T> implements Callback<T> {

	private final Consumer<DataResponseContainer<T>> responseHandler;

	RetrofitCallbackAdapter(Consumer<DataResponseContainer<T>> responseHandler) {
		this.responseHandler = responseHandler;
	}

	@Override
	public void onResponse(@NonNull Call<T> call,
	                       @NonNull Response<T> response) {

		if (!response.isSuccessful()) {
			handleErrorResponse(response);
		} else {
			T body = response.body();
			if (body == null) {
				postException(new ApiException("Server error", response.code()));
			} else {
				processResponse(body);
			}
		}
	}

	private void handleErrorResponse(@NonNull Response<T> response) {
		ResponseBody errorResponseBody = response.errorBody();

		if (errorResponseBody == null) {
			postException(new ApiException("Server error", response.code()));
			return;
		}

		Converter<ResponseBody, ErrorResponse> converter = ApiInitializer.RETROFIT
			.responseBodyConverter(ErrorResponse.class, new Annotation[0]);

		try {
			ErrorResponse errorResponse = converter.convert(errorResponseBody);
			postException(new ApiException(errorResponse.getMessage(), errorResponse.getError(), response.code()));
		} catch (IOException e) {
			postException(e);
		}
	}

	private void processResponse(T body) {
		postResponse(body);
	}

	private void postResponse(T response) {
		responseHandler.accept(DataResponseContainer.wrap(response));
	}

	private void postException(Throwable exception) {
		responseHandler.accept(DataResponseContainer.wrap(exception));
	}

	private void postException(ApiException exception) {
		responseHandler.accept(DataResponseContainer.wrap(exception));
	}

	@Override
	public void onFailure(@NonNull Call<T> call, @NonNull Throwable exception) {
		postException(exception);
	}
}
