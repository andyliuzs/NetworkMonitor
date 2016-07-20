package testnet.andy.testnetworkstatus.db;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.support.v4.database.DatabaseUtilsCompat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import testnet.andy.testnetworkstatus.TrafficModel;

/**
 * Created by andyliu on 16-7-4.
 */
public class DBProvider extends ContentProvider {


    private SQLiteDatabase db;
    private DBHelper dbHelper;

    private static final int TRAFFICS = 1, TRAFFIC_ID = 2;

    private static final UriMatcher URI_MATCHER;
    private static final String UNKNOWN_URI_LOG = "Unknown URI ";

    static {
        // Create and initialize URI matcher.
        URI_MATCHER = new UriMatcher(UriMatcher.NO_MATCH);

        URI_MATCHER.addURI(DBManager.AUTHORITY, TrafficModel.TRAFFIC_MODEL_TABLE, TRAFFICS);
        URI_MATCHER.addURI(DBManager.AUTHORITY, TrafficModel.TRAFFIC_MODEL_TABLE + "/#", TRAFFIC_ID);
    }

    public static final String[] TRAFFIC_PROJECTION = TrafficModel.PROJECTION;


    @Override
    public boolean onCreate() {
        dbHelper = new DBHelper(getContext());
        return dbHelper == null ? false : true;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        // Constructs a new query builder and sets its table name
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
        String finalSortOrder = sortOrder;
        String[] finalSelectionArgs = selectionArgs;
        String finalGrouping = null;
        String finalHaving = null;
        int type = URI_MATCHER.match(uri);

        Uri regUri = uri;

        // Security check to avoid project of invalid fields or lazy projection
        List<String> possibles = getPossibleFieldsForType(type);
        if (possibles == null) {
            throw new SecurityException("You are asking wrong values " + type);
        }
        checkProjection(possibles, projection);
//        checkSelection(possibles, selection);

        Cursor c;
        long id;
        switch (type) {
            case TRAFFICS:
                qb.setTables(TrafficModel.TRAFFIC_MODEL_TABLE);
                if (sortOrder == null) {
                    finalSortOrder = TrafficModel.DATE + " ASC";
                }
                break;
            case TRAFFIC_ID:
                qb.setTables(TrafficModel.TRAFFIC_MODEL_TABLE);
                qb.appendWhere(TrafficModel._ID + "=?");
                finalSelectionArgs = DatabaseUtilsCompat.appendSelectionArgs(selectionArgs,
                        new String[]{uri.getLastPathSegment()});
                break;

            default:
                throw new IllegalArgumentException("UNKNOW " + uri);
        }

        SQLiteDatabase db = dbHelper.getReadableDatabase();

        c = qb.query(db, projection, selection, finalSelectionArgs, finalGrouping, finalHaving, finalSortOrder);

        c.setNotificationUri(getContext().getContentResolver(), regUri);
        return c;
    }


    private static List<String> getPossibleFieldsForType(int type) {
        List<String> possibles = null;
        switch (type) {
            case TRAFFICS:
            case TRAFFIC_ID:
                possibles = Arrays.asList(TRAFFIC_PROJECTION);
                break;

            default:
        }
        return possibles;
    }

    private static void checkSelection(List<String> possibles, String selection) {
        if (selection != null) {
            String cleanSelection = selection.toLowerCase();
            for (String field : possibles) {
                cleanSelection = cleanSelection.replace(field, "");
            }

            cleanSelection = cleanSelection.replaceAll("not like", "");
            cleanSelection = cleanSelection.replaceAll("like", "");

            cleanSelection = cleanSelection.replaceAll(" in \\([0-9 ,]+\\)", "");
            cleanSelection = cleanSelection.replaceAll(" and ", "");
            cleanSelection = cleanSelection.replaceAll(" or ", "");
            cleanSelection = cleanSelection.replaceAll("[0-9]+", "");
            cleanSelection = cleanSelection.replaceAll("[=? ]", "");
            cleanSelection = cleanSelection.replaceAll(",", "");
            cleanSelection = cleanSelection.replaceAll("notin", "");

            cleanSelection = cleanSelection.replaceAll("\\(", "");
            cleanSelection = cleanSelection.replaceAll("\\)", "");

            if (cleanSelection.length() > 0) {
                throw new SecurityException("You are selecting wrong thing " + cleanSelection);
            }
        }
    }

    private static void checkProjection(List<String> possibles, String[] projection) {
        if (projection != null) {
            // Ensure projection is valid
//			for (String proj : projection) {
//				proj = proj.replaceAll(" AS [a-zA-Z0-9_]+$", "");
//				if (!possibles.contains(proj)) {
//					throw new SecurityException("You are asking wrong values " + proj);
//				}
//			}
        }
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        switch (URI_MATCHER.match(uri)) {
            case TRAFFICS:
                return TrafficModel.TRAFFIC_ITEMS_TYPE;
            case TRAFFIC_ID:
                return TrafficModel.TRAFFIC_ITEM_TYPE;
            default:
                throw new IllegalArgumentException(UNKNOWN_URI_LOG + uri);
        }
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues initialValues) {
        int matched = URI_MATCHER.match(uri);
        String matchedTable = null;
        Uri baseInsertedUri = null;
        switch (matched) {
            case TRAFFICS:
            case TRAFFIC_ID:
                matchedTable = TrafficModel.TRAFFIC_MODEL_TABLE;
                baseInsertedUri = TrafficModel.ITEM_URI;
                break;
            default:
                break;
        }

        if (matchedTable == null) {
            throw new IllegalArgumentException(UNKNOWN_URI_LOG + uri);
        }

        ContentValues values;

        if (initialValues != null) {
            values = new ContentValues(initialValues);
        } else {
            values = new ContentValues();
        }

        SQLiteDatabase db = dbHelper.getWritableDatabase();

        long rowId = db.insert(matchedTable, null, values);
// If the insert succeeded, the row ID exists.
        if (rowId >= 0) {
            // TODO : for inserted account register it here

            Uri retUri = ContentUris.withAppendedId(baseInsertedUri, rowId);
            getContext().getContentResolver().notifyChange(retUri, null);
            return retUri;
        }

        throw new SQLException("Failed to insert row into " + uri);

    }

    @Override
    public int delete(Uri uri, String where, String[] whereArgs) {

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        String finalWhere;
        int count = 0;
        int matched = URI_MATCHER.match(uri);
        Uri regUri = uri;

        List<String> possibles = getPossibleFieldsForType(matched);
        checkSelection(possibles, where);

        ArrayList<Long> oldRegistrationsAccounts = null;

        switch (matched) {
            case TRAFFICS:
                count = db.delete(TrafficModel.TRAFFIC_MODEL_TABLE, where, whereArgs);
                break;
            case TRAFFIC_ID:
                finalWhere = DatabaseUtilsCompat.concatenateWhere(TrafficModel._ID + " = " + ContentUris.parseId(uri),
                        where);
                count = db.delete(TrafficModel.TRAFFIC_MODEL_TABLE, finalWhere, whereArgs);
                break;
            default:
                throw new IllegalArgumentException(UNKNOWN_URI_LOG + uri);
        }
        getContext().getContentResolver().notifyChange(regUri, null);
        return count;
    }

    @Override
    public int update(Uri uri, ContentValues values, String where, String[] whereArgs) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int count;
        String finalWhere;
        int matched = URI_MATCHER.match(uri);

        List<String> possibles = getPossibleFieldsForType(matched);
        checkSelection(possibles, where);

        switch (matched) {
            case TRAFFICS:
                count = db.update(TrafficModel.TRAFFIC_MODEL_TABLE, values, where, whereArgs);
                break;
            case TRAFFIC_ID:
                finalWhere = DatabaseUtilsCompat.concatenateWhere(TrafficModel._ID + " = " + ContentUris.parseId(uri),
                        where);
                count = db.update(TrafficModel.TRAFFIC_MODEL_TABLE, values, finalWhere, whereArgs);
                break;
            default:
                throw new IllegalArgumentException(UNKNOWN_URI_LOG + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }


}
