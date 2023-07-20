package eu.toldi.infinityforlemmy;

import android.content.SharedPreferences;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import eu.toldi.infinityforlemmy.account.Account;
import eu.toldi.infinityforlemmy.apis.RedditAPI;
import eu.toldi.infinityforlemmy.utils.APIUtils;
import eu.toldi.infinityforlemmy.utils.SharedPreferencesUtils;
import okhttp3.Authenticator;
import okhttp3.Headers;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.Route;
import retrofit2.Call;
import retrofit2.Retrofit;

class AccessTokenAuthenticator implements Authenticator {
    private Retrofit mRetrofit;
    private RedditDataRoomDatabase mRedditDataRoomDatabase;
    private SharedPreferences mCurrentAccountSharedPreferences;

    AccessTokenAuthenticator(Retrofit retrofit, RedditDataRoomDatabase accountRoomDatabase, SharedPreferences currentAccountSharedPreferences) {
        mRetrofit = retrofit;
        mRedditDataRoomDatabase = accountRoomDatabase;
        mCurrentAccountSharedPreferences = currentAccountSharedPreferences;
    }

    @Nullable
    @Override
    public Request authenticate(Route route, @NonNull Response response) {
        if (response.code() == 401) {
            String accessTokenHeader = response.request().header(APIUtils.AUTHORIZATION_KEY);
            if (accessTokenHeader == null) {
                return null;
            }

            String accessToken = accessTokenHeader.substring(APIUtils.AUTHORIZATION_BASE.length());
            synchronized (this) {
                Account account = mRedditDataRoomDatabase.accountDao().getCurrentAccount();
                if (account == null) {
                    return null;
                }
                String accessTokenFromDatabase = account.getAccessToken();

                return response.request().newBuilder().headers(Headers.of(APIUtils.getOAuthHeader(accessTokenFromDatabase))).build();
            }
        }
        return null;
    }
}
