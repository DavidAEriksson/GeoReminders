package davidaeriksson.github.io.georeminders.misc;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class DBContentProvider extends ContentProvider {

    public static final String CONTENT_PROVIDER_AUTHORITY = "davidaeriksson.github.io.georeminders.DBContentProvider";
    public static final String CONTENT_PROVEDER_SCHEME = "davidaeriksson.github.io.georeminders.DBContentProviderScheme";

    @Override
    public boolean onCreate() {
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] strings, @Nullable String s, @Nullable String[] strings1, @Nullable String s1) {
        return null;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues contentValues) {
        return null;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String s, @Nullable String[] strings) {
        return 0;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues contentValues, @Nullable String s, @Nullable String[] strings) {
        return 0;
    }

    public static Uri getUri(String path) {
        Uri.Builder builder = new Uri.Builder();
        return builder.authority(CONTENT_PROVIDER_AUTHORITY).path(path).scheme(CONTENT_PROVEDER_SCHEME).build();
    }
}
