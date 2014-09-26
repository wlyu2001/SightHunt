package com.sighthunt.network;

import android.content.Context;
import android.text.TextUtils;

import com.sighthunt.R;
import com.sighthunt.inject.Injectable;
import com.sighthunt.network.model.Sight;
import com.sighthunt.network.model.User;

import junit.framework.Assert;

import java.util.List;

import retrofit.Callback;
import retrofit.RequestInterceptor;
import retrofit.RestAdapter;
import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.Multipart;
import retrofit.http.POST;
import retrofit.http.PUT;
import retrofit.http.Part;
import retrofit.http.Path;
import retrofit.http.Query;
import retrofit.mime.TypedFile;

public class ApiManager implements Injectable {


	public interface SightInterface {

		// type can be new, most_voted, most_hunted
		@GET("/sight/list/{region}")
		void getSightsByRegion(@Path("region") String region, @Query("type") String type, Callback<List<Sight>> callback);

		@GET("/sight/fetch")
		void getSight(@Query("id") String id, Callback<Sight> callback);

		@Multipart
		@PUT("/sight/new")
		void createSight(@Body Sight sight, @Part("photo") TypedFile photo, Callback<String> callback);

		@POST("/sight/edit")
		void editSight(@Body Sight sight, Callback<String> callback);

		// type can be hunted, created
		@GET("/sight/list/{user}")
		void getSightsByUser(@Path("user") String id, @Query("type") String type, Callback<List<Sight>> callback);

	}

	public interface UserInterface {

		@GET("/user/fetch")
		void getUser(@Query("username") String username, Callback<User> callback);

		@GET("/user/login")
		User loginSync(@Query("username") String username, @Query("password") String password);


		@GET("/user/login")
		void loginAsync(@Query("username") String username, @Query("password") String password, Callback<User> callback);

		@POST("/user/signup")
		void signupUser(@Body User user, Callback<User> callback);

		@POST("/user/edit")
		void editUser(@Body User user, Callback<User> callback);
	}


	private static final String API_URL = "https://sight-hunt.appspot.com";

	private SightInterface mSightService;
	private UserInterface mUserService;
	private String mToken;

	public ApiManager(final Context context) {
		final String key = context.getString(R.string.api_key);

		RestAdapter restAdapter = new RestAdapter.Builder().setEndpoint(API_URL).setLogLevel(RestAdapter.LogLevel.FULL).setRequestInterceptor(new RequestInterceptor() {
			@Override
			public void intercept(RequestInterceptor.RequestFacade request) {

				// The request is validated by package name and signing key, so no other app should be able to send request
				request.addQueryParam("key", key);

				// We need to use token for auth, since user might change password, then token is invalidated and user needs to log in again
				// For creating user, the endpoint can ignore Authorization
				//Assert.assertFalse(TextUtils.isEmpty(mToken));
				request.addHeader("Authorization", mToken);
			}
		}).build();


		mSightService = restAdapter.create(SightInterface.class);
		mUserService = restAdapter.create(UserInterface.class);
	}

	public void setToken(String token) {
		mToken = token;
	}

	public SightInterface getSightService() {
		return mSightService;
	}

	public UserInterface getUserService() {
		return mUserService;
	}
}
