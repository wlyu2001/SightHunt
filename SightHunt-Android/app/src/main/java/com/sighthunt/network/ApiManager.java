package com.sighthunt.network;

import android.content.Context;

import com.sighthunt.R;
import com.sighthunt.inject.Injectable;
import com.sighthunt.network.model.Sight;
import com.sighthunt.network.model.User;
import com.sighthunt.util.AccountUtils;

import java.io.File;
import java.util.List;

import retrofit.Callback;
import retrofit.ErrorHandler;
import retrofit.RequestInterceptor;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.Multipart;
import retrofit.http.POST;
import retrofit.http.Part;
import retrofit.http.Query;
import retrofit.mime.TypedFile;
import retrofit.mime.TypedString;

public class ApiManager implements Injectable {


	public interface SightInterface {

		// type can be new, most_voted, most_hunted
		@GET("/sight/list_by_region")
		void getSightsByRegion(@Query("region") String region, @Query("last_modified") long lastModified, @Query("type") String type, @Query("offset") int start, @Query("limit") int limit, Callback<List<Sight>> callback);

		@GET("/sight/fetch")
		void getSight(@Query("id") String id, Callback<Sight> callback);

		@POST("/sight/new")
		void createSight(@Body Sight sight, Callback<Sight> callback);

		@POST("/sight/edit")
		void editSight(@Body Sight sight, Callback<String> callback);

		// type can be hunted, created
		@GET("/sight/list_by_user")
		void getSightsByUser(@Query("user") String id, @Query("last_modified") long lastModified, @Query("type") String type, @Query("offset") int start, @Query("limit") int limit, Callback<List<Sight>> callback);

		@GET("/sight/hunt")
		void huntSight(@Query("user") String username, @Query("sight") String sightKey, @Query("vote") int vote, Callback<Integer> callback);

	}

	public interface ImageInterface {
		@Multipart
		@POST("/")
		void uploadImage(@Part("sight_key") TypedString key, @Part("image") TypedFile photo, @Part("thumb") TypedFile thumb, Callback<Sight> callback);
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
	private AccountUtils mAccountUtils;

	private RequestInterceptor mIntercetper = new RequestInterceptor() {
		@Override
		public void intercept(RequestInterceptor.RequestFacade request) {

			// The request is validated by package name and signing key, so no other app should be able to send request
			request.addQueryParam("key", mKey);

			// We need to use token for auth, since user might change password, then token is invalidated and user needs to log in again
			// For creating user, the endpoint can ignore Authorization
			//Assert.assertFalse(TextUtils.isEmpty(mToken));
			request.addHeader("Username", mAccountUtils.getUsername());
			request.addHeader("Authorization", mAccountUtils.getToken());
			request.addHeader("User-Agent", "gzip");
			request.addHeader("Accept-Encoding", "gzip");
		}
	};

	private RequestInterceptor mIntercetper1 = new RequestInterceptor() {
		@Override
		public void intercept(RequestInterceptor.RequestFacade request) {

			request.addHeader("Username", mAccountUtils.getUsername());
			request.addHeader("Authorization", mAccountUtils.getToken());
			request.addHeader("User-Agent", "gzip");
			request.addHeader("Accept-Encoding", "gzip");
		}
	};

	private final String mKey;

	public ApiManager(final Context context, AccountUtils accountUtils) {
		mAccountUtils = accountUtils;
		mKey = context.getString(R.string.api_key);

		RestAdapter restAdapter = new RestAdapter.Builder()
				.setEndpoint(API_URL)
				.setLogLevel(RestAdapter.LogLevel.FULL)
				.setRequestInterceptor(mIntercetper)
				.setErrorHandler(new ErrorHandler() {
					@Override
					public Throwable handleError(RetrofitError cause) {
						Response r = cause.getResponse();
						if (r != null && r.getStatus() == 401) {
							mAccountUtils.invalidateToken();
						}
						return cause;
					}
				}).build();

		mSightService = restAdapter.create(SightInterface.class);
		mUserService = restAdapter.create(UserInterface.class);
	}

	public void uploadImage(String url, String key, String image, String thumb, final Callback<Sight> callback) {

		final TypedFile imageFile = new TypedFile("image/jpeg", new File(image));
		final TypedFile thumbFile = new TypedFile("image/jpeg", new File(thumb));
		TypedString typedString = new TypedString(key);

		RestAdapter imageAdapter = new RestAdapter.Builder()
				.setEndpoint(url)
				.setRequestInterceptor(mIntercetper1)
				.setLogLevel(RestAdapter.LogLevel.FULL)
				.build();
		imageAdapter.create(ImageInterface.class).uploadImage(typedString, imageFile, thumbFile, callback);
	}

	public SightInterface getSightService() {
		return mSightService;
	}

	public UserInterface getUserService() {
		return mUserService;
	}
}
