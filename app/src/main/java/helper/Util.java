package helper;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.media.AudioAttributes;
import android.media.MediaScannerConnection;
import android.media.RingtoneManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.CallLog;
import android.provider.ContactsContract;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.StringRes;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

//import com.android.volley.Response;
//import com.android.volley.VolleyError;
import com.example.myfitnesstracker.R;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.internal.LinkedTreeMap;
import com.google.gson.reflect.TypeToken;


import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.text.DateFormat;
import java.text.DateFormatSymbols;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.TimeZone;
import java.util.UUID;
import java.util.regex.Pattern;


import models.ApplicationStatusModel;
import models.CallLogModel;
import models.ContactModel;
import models.DeviceModel;
import models.GPSTracker;
import models.LocationModel;

import static android.graphics.Paint.ANTI_ALIAS_FLAG;


public class Util {


    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    static String Code;
    static SharedPreferences sharedPreferences;
    static SharedPreferences.Editor editor;
    static boolean from_DashBoard;

    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    public static void verifyStoragePermissions(Activity activity) {
        // Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }
    }

    public static void showAlertDialogAndExitApp(final Activity activity, String message) {

        AlertDialog alertDialog = new AlertDialog.Builder(activity).create();
        alertDialog.setTitle("Alert");
        alertDialog.setCancelable(false);
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.setMessage(message);
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        Intent intent = new Intent(Intent.ACTION_MAIN);
                        intent.addCategory(Intent.CATEGORY_HOME);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        activity.startActivity(intent);
                        activity.finish();
                    }
                });

        alertDialog.show();
    }

    public static float convertPixelsToDp(float px, Context context) {
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        float dp = px / ((float) metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT);
        return dp;
    }

    public static float convertDpToPixels(Context context, float dp) {
        return dp * context.getResources().getDisplayMetrics().density;
    }

    public static int dpToInt(Context context, int dp) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dp * scale + 0.5f);
    }

    public static boolean isNetworkConnected(Context mContext) {

        return true;
    }


    public static void addLog(Context c,String type, String tag, String message) {
        if ((boolean)getBuildConfigValue(c, "DEBUG")) {
            switch (type) {
                case CommonConstants.DEBUG:
                    Log.d(tag, message);
                    break;
                case CommonConstants.ERROR:
                    Log.e(tag, message);
                    break;
            }
        }
    }




    /**
     * Gets a field from the project's BuildConfig. This is useful when, for example, flavors
     * are used at the project level to set custom fields.
     * @param context       Used to find the correct file
     * @param fieldName     The name of the field-to-access
     * @return              The value of the field, or {@code null} if the field is not found.
     */
    public static Object getBuildConfigValue(Context context, String fieldName) {
        try {
            Class<?> clazz = Class.forName(context.getPackageName() + ".BuildConfig");
            Field field = clazz.getField(fieldName);
            return field.get(null);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }


    public static String authTokenEkyc(Context context) {

        return "Basic" + " " + Util.encrypt("49esm821eqh078d0reprbqrljo" + ":" + Util.encrypt("1clcdv4iioe14lv4b1qi0dham1bg0539msbvndtom7jk821m7ns1"));
    }


    public static String getCurrentDate() {
        Date c = Calendar.getInstance().getTime();

        SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy", Locale.US);


        return df.format(c);
    }
    public static String getCurrentDate_format() {
        Date c = Calendar.getInstance().getTime();

        SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy", Locale.US);


        return df.format(c);
    }





    public static String fromStringToDate(String format, String time) {
        String inputPattern = format;
        String outputPattern = "dd/MM/yyyy";
        SimpleDateFormat inputFormat = new SimpleDateFormat(inputPattern);
        SimpleDateFormat outputFormat = new SimpleDateFormat(outputPattern);

        Date date = null;
        String str = null;

        try {
            date = inputFormat.parse(time);
            str = outputFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return str;
    }


    public static void showToast(String message, Context mContext) {
        Toast.makeText(mContext, message, Toast.LENGTH_SHORT).show();

    }

//    public static String getAppVersion() {
//        return String.valueOf(BuildConfig.VERSION_CODE);
//    }

    public static String getApplicationName(Context context) {
        ApplicationInfo applicationInfo = context.getApplicationInfo();
        int stringId = applicationInfo.labelRes;
        return stringId == 0 ? applicationInfo.nonLocalizedLabel.toString() : context.getString(stringId);
    }

    public static String getDeviceName() {
        String manufacturer = Build.MANUFACTURER;
        String model = Build.MODEL;
        if (model.startsWith(manufacturer)) {
            return capitalize(model);
        }
        return capitalize(manufacturer) + " " + model;
    }

    private static String capitalize(String str) {
        if (TextUtils.isEmpty(str)) {
            return str;
        }
        char[] arr = str.toCharArray();
        boolean capitalizeNext = true;

        StringBuilder phrase = new StringBuilder();
        for (char c : arr) {
            if (capitalizeNext && Character.isLetter(c)) {
                phrase.append(Character.toUpperCase(c));
                capitalizeNext = false;
                continue;
            } else if (Character.isWhitespace(c)) {
                capitalizeNext = true;
            }
            phrase.append(c);
        }

        return phrase.toString();

    }


    public static void launchMarket(Context context) {
        try {

            Uri uri = Uri.parse("https://play.google.com/store/apps/details?id=" + context.getPackageName() + "");
            Intent goMarket = new Intent(Intent.ACTION_VIEW, uri);
            goMarket.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(goMarket);


        } catch (ActivityNotFoundException e) {
            Uri uri = Uri.parse("market://details?id=" + context.getPackageName() + "");
            Intent goMarket = new Intent(Intent.ACTION_VIEW, uri);
            goMarket.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(goMarket);
        }
    }

    public static boolean isValidEmail(CharSequence target) {
        return (!TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches());
    }

    public static boolean isValidEmailNew(CharSequence email) {

        Pattern EMAIL_PATTERN = Pattern.compile(
                "[a-zA-Z0-9\\+\\.\\_\\%\\-\\+]{1,256}" +
                        "\\@" +
                        "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}" +
                        "(" +
                        "\\." +
                        "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25}" +
                        ")+"
        );
        return email != null && EMAIL_PATTERN.matcher(email).matches();
    }

    public static boolean isValidMobile(String phone) {

        boolean check = false;

        if (Pattern.matches("^(?!(\\d)\\1{9})(?!0123456789|1234567890|0987654321|9876543210)\\d{10}$", phone)) {
            // if(phone.length() != 10) {
            check = phone.length() >= 10 && phone.length() <= 13;
        } else {
            check = false;
        }
        return check;

    }

    public static String getOSName() {
        Field[] fields = Build.VERSION_CODES.class.getFields();
        String osName = fields[Build.VERSION.SDK_INT + 1].getName();

        return osName;
    }

    @SuppressLint("MissingPermission")
    public static String getUUID(Activity activity) {
        final int REQUEST_READ_PHONE_STATE = 101;
        String uuid = "";
        TelephonyManager tManager = (TelephonyManager) activity.getSystemService(Context.TELEPHONY_SERVICE);
        int permissionCheck = ContextCompat.checkSelfPermission(activity, Manifest.permission.READ_PHONE_STATE);

        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.READ_PHONE_STATE}, REQUEST_READ_PHONE_STATE);
        } else {
            //TODO
            uuid = tManager.getDeviceId();

        }


        return uuid;
    }

    public static boolean isNetworkAvailable(Context context) {
        boolean connected = false;
        try {
            ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            if (connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                    connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
                //we are connected to a network
                connected = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


        return connected;
    }

    public static void closeKeyBoard(Activity context) {

        try {

            InputMethodManager inputManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
            if (inputManager != null && inputManager.isAcceptingText()) {
                inputManager.hideSoftInputFromWindow(context.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static void openKeyBoard(Activity context, EditText editText) {

        try {

            InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static void closeKeyBoard2(Activity context, EditText editText) {

        try {

            InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }



    /**
     * Get a file path from a Uri. This will get the the path for Storage Access
     * Framework Documents, as well as the _data field for the MediaStore and
     * other file-based ContentProviders.
     *
     * @param context The mContext.
     * @param uri     The Uri to query.
     * @author paulburke
     */
    public static String getPath(final Context context, final Uri uri) {

        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

        // DocumentProvider
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
                // ExternalStorageProvider
                if (isExternalStorageDocument(uri)) {
                    final String docId = DocumentsContract.getDocumentId(uri);
                    final String[] split = docId.split(":");
                    final String type = split[0];

                    if ("primary".equalsIgnoreCase(type)) {
                        return Environment.getExternalStorageDirectory() + "/" + split[1];
                    }

                    // TODO handle non-primary volumes
                }
                // DownloadsProvider
                else if (isDownloadsDocument(uri)) {

                    final String id = DocumentsContract.getDocumentId(uri);
                    final Uri contentUri = ContentUris.withAppendedId(
                            Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

                    String path = uri.getPath();
                    // Bitmap bmImg = BitmapFactory.decodeFile(Environment.getExternalStorageDirectory() + path);
                    return Environment.getExternalStorageDirectory() + path;
                }
                // MediaProvider
                else if (isMediaDocument(uri)) {
                    final String docId = DocumentsContract.getDocumentId(uri);
                    final String[] split = docId.split(":");
                    final String type = split[0];

                    Uri contentUri = null;
                    if ("image".equals(type)) {
                        contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                    } else if ("video".equals(type)) {
                        contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                    } else if ("audio".equals(type)) {
                        contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                    }

                    final String selection = "_id=?";
                    final String[] selectionArgs = new String[]{
                            split[1]
                    };

                    return getDataColumn(context, contentUri, selection, selectionArgs);
                }
            }
            // MediaStore (and general)
            else if ("content".equalsIgnoreCase(uri.getScheme())) {

                // Return the remote address
                if (isGooglePhotosUri(uri))
                    return uri.getLastPathSegment();

                return getDataColumn(context, uri, null, null);
            }
            // File
            else if ("file".equalsIgnoreCase(uri.getScheme())) {
                return uri.getPath();
            }
        }

        return null;
    }

    /**
     * Get the value of the data column for this Uri. This is useful for
     * MediaStore Uris, and other file-based ContentProviders.
     *
     * @param context       The mContext.
     * @param uri           The Uri to query.
     * @param selection     (Optional) Filter used in the query.
     * @param selectionArgs (Optional) Selection arguments used in the query.
     * @return The value of the _data column, which is typically a file path.
     */
    public static String getDataColumn(Context context, Uri uri, String selection,
                                       String[] selectionArgs) {

        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {
                column
        };

        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
                    null);
            if (cursor != null && cursor.moveToFirst()) {
                final int index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(index);
            }
        } catch (NullPointerException e) {
            e.fillInStackTrace();
            return null;
        } finally {
            if (cursor != null) {
                cursor.close();
            }

        }
        return null;
    }


    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is Google Photos.
     */
    public static boolean isGooglePhotosUri(Uri uri) {
        return "com.google.android.apps.photos.content".equals(uri.getAuthority());
    }



    public static String toTitleCase(String str) {

        if (str == null) {
            return null;
        }

        boolean space = true;
        StringBuilder builder = new StringBuilder(str);
        final int len = builder.length();

        for (int i = 0; i < len; ++i) {
            char c = builder.charAt(i);
            if (space) {
                if (!Character.isWhitespace(c)) {
                    // Convert to title case and switch out of whitespace mode.
                    builder.setCharAt(i, Character.toTitleCase(c));
                    space = false;
                }
            } else if (Character.isWhitespace(c)) {
                space = true;
            } else {
                builder.setCharAt(i, Character.toLowerCase(c));
            }
        }

        return builder.toString();
    }

    public static String trimCommaOfString(String string) {
//        String returnString;
        if (string.contains(",")) {
            return string.replace(",", "");
        } else {
            return string;
        }

    }

    public static String formatLoanAmount(long number) {
        DecimalFormat formatter = new DecimalFormat("##,##,###");
        String yourFormattedString = formatter.format(number);

        return yourFormattedString;
    }

    public static String formatAmount(float number) {
        String yourFormattedString;
        if (number != 0) {
            DecimalFormat formatter = new DecimalFormat("##,##,###.00");
            yourFormattedString = formatter.format(number);
        } else {
            yourFormattedString = "0";
        }


        return yourFormattedString;
    }

    public static int setProgressValue(float totalAmount, float totalAmountPaid) {

        int progress = 0;

        progress = (int) ((Math.abs(totalAmountPaid) / Math.abs(totalAmount)) * 100);


        return progress < 0 ? 0 : progress;

    }

    public static String convertToDate(ArrayList<Integer> data) {
        if (data != null) {
            String month = getMonth(data.get(1));
            return data.get(2) + " " + month + " " + data.get(0);
        }
        return "";

    }

    public static String applicationStatus(boolean isActive) {
        if (isActive) {
            return "Ongoing";
        } else {
            return "Closed";
        }
    }

    public static String setCode(String productCode, int exp_id) {

        String code = "";

        switch (productCode) {
            case "TLF":
            case "TLV":
            case "POS":
            case "MIG":
            case "TLFOLD":
            case "DRP":
            case "TLVFN":
            case "MFLI":
            case "FFL":
                code = "common";
                break;
            case "APOS":
                code = "AutoPOS";
                break;
            case "GO":
            case "OD":
                code = "DLOD";
                break;
            default:
                code = "common";
                break;

        }


        if (exp_id == 17 && code.equalsIgnoreCase("common")) {
            code = "AutoPOS";
        }

        return code;
    }

    public static String setLoanName(String productCode) {
        String code = "";
        switch (productCode) {
            case "TLF":
            case "TLV":
            case "MIG":
            case "TLFOLD":
            case "DRP":
            case "TLVFN":
            case "MFLI":
            case "FFL":
                code = "Term Loan";
                break;
            case "APOS":
            case "POS":
                code = "Merchant Cash Advance";
                break;
            case "GO":
            case "OD":
                code = "Overdraft";
                break;
            default:
                code = "Term Loan";
                break;

        }


        return code;
    }


    public static String calculateNextODDrop() {

        String ODDropDate = "";
        Calendar cal = Calendar.getInstance();

        if (cal.get(Calendar.MONTH) == Calendar.DECEMBER) {
            cal.set(cal.get(Calendar.YEAR) + 1, 1, 1);
        } else if (cal.get(Calendar.MONTH) == Calendar.NOVEMBER) {
            cal.set(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH) + 1, 1);
        } else {
            cal.set(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH) + 2, 1);
        }

        int date = cal.get(Calendar.DATE);
        String month = getMonth(cal.get(Calendar.MONTH));
        int year = cal.get(Calendar.YEAR);

        ODDropDate = date + " " + month + " " + year;

        return ODDropDate;

    }

    public static String getMonth(int month) {
        return new DateFormatSymbols().getMonths()[month - 1].substring(0, 3);
    }

    public static String changeDate(String date) {
        String tripdate = "";
        SimpleDateFormat input = new SimpleDateFormat("MMM dd,yyyy");
        SimpleDateFormat output = new SimpleDateFormat("dd MMM yyyy");
        try {
            Date oneWayTripDate = input.parse(date);                 // parse input
            tripdate = output.format(oneWayTripDate);
        } catch (ParseException e) {
            tripdate = date;
            e.printStackTrace();
        }

        return tripdate;
    }

    public static String trimMonth(String date) {
        String tripdate = "";
        if (date != null) {
            SimpleDateFormat input = new SimpleDateFormat("dd MMM yyyy");
            SimpleDateFormat output = new SimpleDateFormat("dd MMM yyyy");
            try {
                Date oneWayTripDate = input.parse(date);                 // parse input
                tripdate = output.format(oneWayTripDate);
            } catch (ParseException e) {
                tripdate = date;
                e.printStackTrace();
            }
        } else {
            tripdate = "-";
        }

        return tripdate;
    }

    public static ArrayList<CallLogModel> fetchCallLogs(Activity context) {
        ArrayList<CallLogModel> list = new ArrayList<>();


        String permission = Manifest.permission.READ_CALL_LOG;
        int res = context.checkCallingOrSelfPermission(permission);
        try {

            if (res == PackageManager.PERMISSION_GRANTED) {

                String[] projection = new String[]{
                        CallLog.Calls.CACHED_NAME,
                        CallLog.Calls.NUMBER,
                        CallLog.Calls.TYPE,
                        CallLog.Calls.DATE,
                        CallLog.Calls.DURATION,
                };

                @SuppressLint("MissingPermission") Cursor cursor = context.getContentResolver().query(CallLog.Calls.CONTENT_URI, projection, null, null, null);
                while (cursor.moveToNext()) {
                    CallLogModel callLogModel = new CallLogModel();

                    String name = cursor.getString(0);
                    String number = cursor.getString(1);
                    String type = cursor.getString(2); // https://developer.android.com/reference/android/provider/CallLog.Calls.html#TYPE
                    String time = cursor.getString(3);// epoch time - https://developer.android.com/reference/java/text/DateFormat.html#parse(java.lang.String
                    String duration = cursor.getString(4);


                    callLogModel.setName(name);
                    callLogModel.setNumber(number);
                    callLogModel.setType(type);
                    callLogModel.setDuration(duration);
                    callLogModel.setCallTime(new SimpleDateFormat("dd/MM/yyyy hh:mm a").format(new Date(Long.parseLong(time))));

                    list.add(callLogModel);
                }
                cursor.close();


            }


        } catch (Exception e) {
            e.printStackTrace();
        }


        return list;

    }

    public static ArrayList<ContactModel> fetchContacts(Activity context) {
        ArrayList<ContactModel> list = new ArrayList<>();

        ContactModel contactModel = new ContactModel();
        String permission = Manifest.permission.READ_CONTACTS;
        int res = context.checkCallingOrSelfPermission(permission);
        try {
            if (res == PackageManager.PERMISSION_GRANTED) {
                Uri uri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
                String[] projection = new String[]
                        {
                                ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                                ContactsContract.CommonDataKinds.Phone.NUMBER};

                Cursor people = context.getContentResolver().query(uri, projection, null, null, null);

                int indexName =
                        people.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME);
                int indexNumber =
                        people.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);

                if (people.moveToFirst()) {
                    do {
                        String name = people.getString(indexName);
                        String number = people.getString(indexNumber);
                        // Do work...
                        contactModel.setNumber(number);
                        contactModel.setName(name);

                        list.add(contactModel);

                    } while (people.moveToNext());
                }


                people.close();

            }


        } catch (Exception e) {
            e.printStackTrace();
        }


        return list;

    }

    public static DeviceModel fetchDeviceDetails() {

        JsonObject deviceData = new JsonObject();

        DeviceModel deviceModel = new DeviceModel();

        String osversion = System.getProperty("os.version"); // OS version
        String apilevel = Build.VERSION.SDK;     // API Level
        String device = Build.DEVICE;          // Device
        String model = Build.MODEL;           // Model
        String product = Build.PRODUCT;
        String brand = Build.BRAND;
        String id = Build.ID;

        deviceModel.setOs_version(osversion);
        deviceModel.setApi_level(apilevel);
        deviceModel.setDevice(device);
        deviceModel.setModel_name(model);
        deviceModel.setProduct(product);
        deviceModel.setProduct(brand);
        deviceModel.setId(id);

        return deviceModel;

    }

    public static void turnGPSOn(Activity activity) {
        String provider = Settings.Secure.getString(activity.getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);

        if (!provider.contains("gps")) { //if gps is disabled
            final Intent poke = new Intent();
            poke.setClassName("com.android.settings", "com.android.settings.widget.SettingsAppWidgetProvider");
            poke.addCategory(Intent.CATEGORY_ALTERNATIVE);
            poke.setData(Uri.parse("3"));
            activity.sendBroadcast(poke);
        }
    }


    private void turnGPSOff(Activity activity) {
        String provider = Settings.Secure.getString(activity.getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);

        if (provider.contains("gps")) { //if gps is enabled
            final Intent poke = new Intent();
            poke.setClassName("com.android.settings", "com.android.settings.widget.SettingsAppWidgetProvider");
            poke.addCategory(Intent.CATEGORY_ALTERNATIVE);
            poke.setData(Uri.parse("3"));
            activity.sendBroadcast(poke);
        }
    }


    public static LocationModel getLocation(Activity context) {

        LocationModel locationModel = new LocationModel();
        GPSTracker gpsTracker = new GPSTracker(context);

        JsonObject location = new JsonObject();

        if (gpsTracker.getIsGPSTrackingEnabled()) {

            String stringLatitude = String.valueOf(gpsTracker.latitude);
            locationModel.setLatitude(stringLatitude);

            String stringLongitude = String.valueOf(gpsTracker.longitude);
            locationModel.setLongitude(stringLongitude);

            String country = gpsTracker.getCountryName(context);
            locationModel.setCountry(country);

            String city = gpsTracker.getLocality(context);
            locationModel.setCity(city);

            String postalCode = gpsTracker.getPostalCode(context);
            locationModel.setPostalCode(postalCode);

            String addressLine = gpsTracker.getAddressLine(context);
            locationModel.setAddressLine(addressLine);


        } else {
            // can't get location
            // GPS or Network is not enabled
            // Ask user to enable GPS/network in settings
            gpsTracker.showSettingsAlert();
        }

        return locationModel;

    }

    public static int getAge(String dobString){

        Date date = null;
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
        try {
            date = sdf.parse(dobString);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        if(date == null) return 0;

        Calendar dob = Calendar.getInstance();
        Calendar today = Calendar.getInstance();

        dob.setTime(date);

        int year = dob.get(Calendar.YEAR);
        int month = dob.get(Calendar.MONTH);
        int day = dob.get(Calendar.DAY_OF_MONTH);

        dob.set(year, month, day);

        int age = today.get(Calendar.YEAR) - dob.get(Calendar.YEAR);

        if (today.get(Calendar.DAY_OF_YEAR) < dob.get(Calendar.DAY_OF_YEAR)){
            age--;
        }



        return age;
    }


    // url = file path or whatever suitable URL you want.
    public static String getMimeType(String url) {
        String type = null;
        String extension = MimeTypeMap.getFileExtensionFromUrl(url);
        if (extension != null) {
            type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
        }
        return type;
    }

    public static void askUserPermissions(Activity context) {
        int PERMISSION_ALL = 1;

        if (!hasPermissions(context, getPermissions())) {
            ActivityCompat.requestPermissions(context, getPermissions(), PERMISSION_ALL);
        } else {

        }
    }

    public static String[] getPermissions() {
        String[] PERMISSIONS = {
                Manifest.permission.CAMERA,
                Manifest.permission.READ_CONTACTS,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_SMS
//                android.Manifest.permission.READ_CALL_LOG,
        };

        return PERMISSIONS;
    }

    public static boolean hasPermissions(Context context, String... permissions) {
        if (context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {

                    return false;
                }
            }
        }

        return true;
    }

    public static void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            // pre-condition
            return;
        }

        int totalHeight = 0;
        int desiredWidth = View.MeasureSpec.makeMeasureSpec(listView.getWidth(), View.MeasureSpec.AT_MOST);
        for (int i = 0; i < listAdapter.getCount(); i++) {
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);
            totalHeight += listItem.getMeasuredHeight();
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
        listView.requestLayout();
    }

    public static boolean setListViewHeightBasedOnItems(ListView listView) {

        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter != null) {

            int numberOfItems = listAdapter.getCount();

            // Get total height of all items.
            int totalItemsHeight = 0;
            for (int itemPos = 0; itemPos < numberOfItems; itemPos++) {
                View item = listAdapter.getView(itemPos, null, listView);
                float px = 500 * (listView.getResources().getDisplayMetrics().density);
                item.measure(View.MeasureSpec.makeMeasureSpec((int) px, View.MeasureSpec.AT_MOST), View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
                totalItemsHeight += item.getMeasuredHeight();
            }

            // Get total height of all item dividers.
            int totalDividersHeight = listView.getDividerHeight() *
                    (numberOfItems - 1);
            // Get padding
            int totalPadding = listView.getPaddingTop() + listView.getPaddingBottom();

            // Set list height.
            ViewGroup.LayoutParams params = listView.getLayoutParams();
            params.height = totalItemsHeight + totalDividersHeight + totalPadding;
            listView.setLayoutParams(params);
            listView.requestLayout();
            return true;

        } else {
            return false;
        }


    }

    public static boolean isKeyboardShown(View rootView) {
        /* 128dp = 32dp * 4, minimum button height 32dp and generic 4 rows soft keyboard */
        final int SOFT_KEYBOARD_HEIGHT_DP_THRESHOLD = 128;

        Rect r = new Rect();
        rootView.getWindowVisibleDisplayFrame(r);
        DisplayMetrics dm = rootView.getResources().getDisplayMetrics();
        /* heightDiff = rootView height - status bar height (r.top) - visible frame height (r.bottom - r.top) */
        int heightDiff = rootView.getBottom() - r.bottom;
        /* Threshold size: dp to pixels, multiply with display density */
        boolean isKeyboardShown = heightDiff > SOFT_KEYBOARD_HEIGHT_DP_THRESHOLD * dm.density;


        return isKeyboardShown;
    }

    public static void setMargins(View view, int left, int top, int right, int bottom) {
        if (view.getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
            ViewGroup.MarginLayoutParams p = (ViewGroup.MarginLayoutParams) view.getLayoutParams();
            p.setMargins(left, top, right, bottom);
            view.requestLayout();
        }
    }

    public static void scrollToView(final ScrollView scrollViewParent, final View view) {
        // Get deepChild Offset
        Point childOffset = new Point();
        getDeepChildOffset(scrollViewParent, view.getParent(), view, childOffset);
        // Scroll to child.
        scrollViewParent.smoothScrollTo(0, childOffset.y);
    }

    private static void getDeepChildOffset(final ViewGroup mainParent, final ViewParent parent, final View child, final Point accumulatedOffset) {
        ViewGroup parentGroup = (ViewGroup) parent;
        accumulatedOffset.x += child.getLeft();
        accumulatedOffset.y += child.getTop();
        if (parentGroup.equals(mainParent)) {
            return;
        }
        getDeepChildOffset(mainParent, parentGroup.getParent(), parentGroup, accumulatedOffset);
    }

    public static Drawable getSymbol(Context context, String symbol, float textSize, int color) {
        Paint paint = new Paint(ANTI_ALIAS_FLAG);
        paint.setTextSize(textSize);
        paint.setColor(color);
        paint.setTextAlign(Paint.Align.LEFT);


        float baseline = -paint.ascent(); // ascent() is negative
        int width = (int) (paint.measureText(symbol) + 0.5f); // round
        int height = (int) (baseline + paint.descent() + 0.5f);
        Bitmap image = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(image);
        canvas.drawText(symbol, 0, baseline, paint);
        return new BitmapDrawable(context.getResources(), image);
    }

//    private static JSONObject convertToJson(JSONObject queryParams) {
//        JSONObject convertedObject = new JSONObject();
//        try {
//            convertedObject = new JSONObject(new Gson().toJson(queryParams));
//            return (JSONObject) convertedObject.get("nameValuePairs");
//
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//
//        return convertedObject;
//
//    }

    public static boolean getDiff(Context context, String auth, Date startDate, Date endDate) {
        boolean isRequired = false;
        String expireTime = "0", startTime = "0";


        try {

            long different = endDate.getTime() - startDate.getTime();


            System.out.println("startDate : " + startDate);
            System.out.println("endDate : " + endDate);
            System.out.println("different : " + different);

            long secondsInMilli = 1000;
            long minutesInMilli = secondsInMilli * 60;
            long hoursInMilli = minutesInMilli * 60;
            long daysInMilli = hoursInMilli * 24;

            long elapsedDays = different / daysInMilli;
            different = different % daysInMilli;

            long elapsedHours = different / hoursInMilli;
            different = different % hoursInMilli;

            long elapsedMinutes = different / minutesInMilli;
            different = different % minutesInMilli;

            long elapsedSeconds = different / secondsInMilli;

            SharedPreferences sharedPreferences = context.getSharedPreferences("TokenDetails", Context.MODE_PRIVATE);

            if (auth.equals(CommonConstants.UNIFIED_AUTHORIZATION)) {


                expireTime = sharedPreferences.getString(CommonConstants.UNIFIED_EXPIRETIME, "");

            } else {

                if (auth.equals(CommonConstants.AUTHORIZATION)) {
                    expireTime = sharedPreferences.getString(CommonConstants.EXPIRETIME, "");
                }

            }


            long min = Long.parseLong(expireTime);
            long millis = min / 3600;
            // Testing
            // long MAX_DURATION = 5;

            long MAX_DURATION = millis;


            //testing
            // if (elapsedDays > 0 || elapsedMinutes >= MAX_DURATION){

            if (elapsedDays > 0 || elapsedHours >= MAX_DURATION) {

                isRequired = true;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return isRequired;

    }

    public static long getDiffTime(String presentTime, String startTime) {

        long diffTime = 0;

        try {
            //SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");

            SimpleDateFormat format = new SimpleDateFormat("dd:MM;yyyy hh:mm:ss");
            Date date1 = format.parse(presentTime);
            Date date2 = format.parse(startTime);
            long difference = date1.getTime() - date2.getTime();

            diffTime = difference;
            System.out.println(difference / 1000);
        } catch (Exception e) {
            e.printStackTrace();
        }


        return diffTime;

    }

    public static String getCurrentTime(String auth) {

        String localTime = "";

        try {

            if (auth.equals(CommonConstants.AUTHORIZATION)) {

                Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("GMT+5:30"));
                Date currentLocalTime = cal.getTime();
                DateFormat date = new SimpleDateFormat("dd/MM/yyyy kk:mm:ss");
                date.setTimeZone(TimeZone.getTimeZone("GMT+5:30"));


                localTime = date.format(currentLocalTime);
            } else {

                if (auth.equals(CommonConstants.UNIFIED_AUTHORIZATION)) {

                    Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("GMT+5:30"));
                    Date currentLocalTime = cal.getTime();
                    DateFormat date = new SimpleDateFormat("dd/MM/yyyy kk:mm:ss");
                    date.setTimeZone(TimeZone.getTimeZone("GMT+5:30"));

                    localTime = date.format(currentLocalTime);

                }

            }


        } catch (Exception e) {

            e.printStackTrace();
        }

        return localTime;


    }

    public static String subDateMonths() {

        Date date = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd");
        String currentDate = formatter.format(date);
        System.out.println("Date Format with yyyy/MM/dd : " + currentDate);

        Calendar cal = Calendar.getInstance();
        cal.setTimeZone(TimeZone.getTimeZone("GMT"));
        cal.add(Calendar.MONTH, -4);
        Date result = cal.getTime();


       /* String currentDate = formatter.format(strDate);
        String eightMonth = formatter.format(result);
*/

        String mnths = currentDate + "," + formatter.format(result);

        return mnths;

    }

    public static Bitmap drawableToBitmap(Drawable drawable) {

        if (drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable) drawable).getBitmap();
        }

        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);

        return bitmap;
    }

    public static String encrypt(final String plainMessage) {
        byte[] data = new byte[0];
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            data = plainMessage.getBytes(StandardCharsets.UTF_8);
        }

        String base64 = Base64.encodeToString(data, Base64.NO_WRAP);

        return base64.trim();

//        return "";

    }

    public static void saveStatus(String lead_code, boolean isEligibilityChecked, boolean isStatmentUploaded, boolean isAddressUpdated, boolean isKYCUploaded, int dropState, int bankTab, int docTab) {

        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();

        ApplicationStatusModel model = new ApplicationStatusModel(isEligibilityChecked, isStatmentUploaded, isAddressUpdated, isKYCUploaded, dropState, bankTab, docTab);
        Map<String, Object> data = new HashMap<>();
        data.put("data", model);

        Code = lead_code;
        DatabaseReference databaseReference = firebaseDatabase.getReference(lead_code);
        databaseReference
                .child("messages")
                .child(UUID.randomUUID().toString())
                .setValue(data);

        databaseReference.setValue(data, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                if (databaseError != null) {
                    System.out.println("Data could not be saved " + databaseError.getMessage());
                } else {
                    System.out.println("Data saved successfully.");
                }
            }
        });

    }

    public static void updateStatus(String lead_code, boolean isEligibilityChecked, boolean isStatmentUploaded, boolean isAddressUpdated, boolean isKYCUploaded, int dropState, int bankTab, int docTab) {

        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();

        DatabaseReference databaseReference = firebaseDatabase.getReference(lead_code);

        DatabaseReference ref = databaseReference.child("data");

        Map<String, Object> hopperUpdates = new HashMap<>();
        hopperUpdates.put("isEligibilityCHecked", isEligibilityChecked);
        hopperUpdates.put("isDocumentUploaded", isStatmentUploaded);
        hopperUpdates.put("isAddressUpdated", isAddressUpdated);
        hopperUpdates.put("isKYCUploaded", isKYCUploaded);
        hopperUpdates.put("dropState", dropState);
        hopperUpdates.put("bankTab", bankTab);
        hopperUpdates.put("docTab", docTab);

        ref.updateChildren(hopperUpdates);
    }

    /**
     * Extract the file name in a URL
     * /storage/emulated/legacy/Download/sample.pptx = sample.pptx
     *
     * @param url String of a URL
     * @return the file name of URL with suffix
     */
    public static String extractFileNameWithSuffix(String url) {
        return url.substring(url.lastIndexOf("/") + 1);
    }

    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    public static String sizeOfFile(long size) {

        if (size < 1024) {
            return size + " KB";
        } else if (size < 1048576) {
            return String.format("%.2f", (size / 1024.0)) + " KB";
        } else if (size < 1073741824) {
            return String.format("%.2f", (size / 1048576.0)) + " MB";
        } else if (size > 1073741824) {
            return String.format("%.2f", (size / 1073741824.0)) + " GB";
        }


        return size + " KB";

    }

    public static String dateOfCreation(String filePath) {
        File file = new File(filePath);
        Date lastModDate = new Date(file.lastModified());
        SimpleDateFormat df = new SimpleDateFormat("dd/MM/yy");

        return df.format(lastModDate);
    }

    public static int getScreenWidth(Context ctx) {
        WindowManager wm = (WindowManager) ctx.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics dm = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(dm);
        return dm.widthPixels;
    }

    public static String extractPathWithoutSeparator(String url) {
        return url.substring(0, url.lastIndexOf("/"));
    }



    public static boolean isFeildNull(Object obj) throws IllegalAccessException {
        for (Field f : obj.getClass().getDeclaredFields()) {
            f.setAccessible(true);
            if (f.getType().toString().equals("class java.lang.String")) {
                if (!(f.getName().equalsIgnoreCase("business_pan_no") || f.getName().equalsIgnoreCase("gst_no") || f.getName().equalsIgnoreCase("business_flat_no") || f.getName().equalsIgnoreCase("residence_flat_no"))) {
                    if (TextUtils.isEmpty((String) f.get(obj))) {
                        return true;
                    }
                }
            } else {
                if (!f.getName().equalsIgnoreCase("is_rco") && f.get(obj) == null) return true;
            }
        }
        return false;
    }

    public static boolean partnerCompletion(Object obj) throws IllegalAccessException {
        for (Field f : obj.getClass().getDeclaredFields()) {
            f.setAccessible(true);
            if (!f.getName().equalsIgnoreCase("isPartnerViewCollapsed") && !f.getName().equalsIgnoreCase("partner_name") && !f.getName().equalsIgnoreCase("noOftimeClicked") && !f.getName().equalsIgnoreCase("isDetailsCompleted") && !f.getName().equalsIgnoreCase("code") && !f.getName().equalsIgnoreCase("address_2") && !f.getName().equalsIgnoreCase("isPanError") && !f.getName().equalsIgnoreCase("type") && !f.getName().equalsIgnoreCase("is_deleted")) {
                if (TextUtils.isEmpty((String) f.get(obj)) || f.get(obj) == null) {
                    return true;
                }
            }

        }
        return false;
    }


//    public static void createAndSaveFileFromBase64Url(String url, Activity context, String applicationID, String prefix) {
//
//
//        File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
//        //String filetype = url.substring(url.indexOf("/") + 1, url.indexOf(";"));
//        String filename = prefix + applicationID + System.currentTimeMillis() + "." + "pdf";
//
//        int notificationId = (int) (System.currentTimeMillis() / 10000);
//
//
//        File file = new File(path, filename);
//        try {
//            if (!path.exists())
//                path.mkdirs();
//            if (!file.exists())
//                file.createNewFile();
//
//            //String base64EncodedString = url.substring(url.indexOf(",") + 1);
//            byte[] decodedBytes = Base64.decode(url, Base64.DEFAULT);
//            OutputStream os = new FileOutputStream(file);
//            os.write(decodedBytes);
//            os.close();
//
//            //Tell the media scanner about the new file so that it is immediately available to the user.
//            MediaScannerConnection.scanFile(getApplicationContext(),
//                    new String[]{file.toString()}, null,
//                    new MediaScannerConnection.OnScanCompletedListener() {
//                        public void onScanCompleted(String path, Uri uri) {
//
//                        }
//                    });
//            MimeTypeMap mime = MimeTypeMap.getSingleton();
//
//
//            //Set notification after download complete and add "click to view" action to that
//            String mimetype = url.substring(url.indexOf(":") + 1, url.indexOf("/"));
//            String ext = file.getName().substring(file.getName().indexOf(".") + 1);
//            String type = mime.getMimeTypeFromExtension(ext);
//
//            Intent intent = new Intent();
//            intent.setAction(Intent.ACTION_VIEW);
//            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
//            Uri ur;
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
//
//                ur = FileProvider.getUriForFile(context, context.getApplicationContext().getPackageName() + ".provider", file);
//
//                List<ResolveInfo> resInfoList = context.getPackageManager().queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
//                for (ResolveInfo resolveInfo : resInfoList) {
//                    String packageName = resolveInfo.activityInfo.packageName;
//                    context.grantUriPermission(packageName, ur, Intent.FLAG_GRANT_READ_URI_PERMISSION);
//                }
//
//                // intent.setDataAndType(FileProvider.getUriForFile(context, context.getApplicationContext().getPackageName() + ".provider", file), type);
//
//            } else {
//                intent.setDataAndType(Uri.fromFile(file), type);
//            }
//            PendingIntent pIntent = PendingIntent.getActivity(getApplicationContext(), 0, intent, 0);
//
//
//            NotificationManager notificationManager =
//                    (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
//
//            String channelId = "my_channel_id";
//            CharSequence channelName = "My Channel";
//            int importance = NotificationManager.IMPORTANCE_DEFAULT;
//            NotificationChannel notificationChannel = null;
//            Notification notification = null;
//            AudioAttributes att = new AudioAttributes.Builder()
//                    .setUsage(AudioAttributes.USAGE_NOTIFICATION)
//                    .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
//                    .build();
//            Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
//
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//
//                notificationChannel = new NotificationChannel(channelId, channelName, importance);
//                notificationChannel.enableLights(true);
//                notificationChannel.setLightColor(Color.RED);
//                notificationChannel.enableVibration(true);
//                notificationChannel.setVibrationPattern(new long[]{1000, 2000});
//                notificationChannel.setSound(uri, att);
//                notificationManager.createNotificationChannel(notificationChannel);
//
//                notification = new Notification.Builder(context)
//                        .setSmallIcon(R.drawable.ic_logo_blue)
//                        .setContentText("Download completed.")
//                        .setContentTitle(filename)
//                        .setContentIntent(pIntent)
//                        .setChannelId(channelId)
//                        .build();
//            } else {
//                notification = new Notification.Builder(context)
//                        .setSmallIcon(R.drawable.ic_logo_blue)
//                        .setContentText("Download completed.")
//                        .setContentTitle(filename)
//                        .setContentIntent(pIntent)
//                        .build();
//
//            }
//
//
//            notification.flags |= Notification.FLAG_AUTO_CANCEL;
//
//            notificationManager.notify(notificationId, notification);
//        } catch (IOException e) {
//            addLog(CommonConstants.DEBUG, "ExternalStorage", "Error writing " + file);
//            Toast.makeText(getApplicationContext(), "failed", Toast.LENGTH_LONG).show();
//        }
//
//        //return file.toString();
//    }

//    public static void getLeadData(Context cntx, Response.Listener listener) {
//        sharedPreferences = cntx.getSharedPreferences("UserDetails", Context.MODE_PRIVATE);
//        if (!TextUtils.isEmpty(sharedPreferences.getString(CommonConstants.LEAD_CODE, "")) && !TextUtils.isEmpty(sharedPreferences.getString(CommonConstants.LOAN_CODE, ""))) {
//            new GetAllLoanApplicationDetails(cntx, listener, true).unifiedLeadLoanApplications();
//        }
//    }
//
//    public static void getPreDisbursalData(Context cntx, Response.Listener listener, Boolean enableLoader) {
//        sharedPreferences = cntx.getSharedPreferences("UserDetails", Context.MODE_PRIVATE);
//        // new GetPreDisbursalData(cntx, listener,"5d4d541ad1f00",enableLoader).makeHttpCall();
//        if (!TextUtils.isEmpty(sharedPreferences.getString(CommonConstants.LOAN_CODE, ""))) {
//            new GetPreDisbursalData(cntx, listener, sharedPreferences.getString(CommonConstants.LOAN_CODE, ""), enableLoader).makeHttpCall();
//            //new GetPreDisbursalData(cntx, listener,"5d11ffd65cxdd",enableLoader).makeHttpCall();
//        }
//    }
//
//    public static void deleteFormData() {
//
//        LoanApplicationForm1.destroy();
//        LoanApplicationForm2.destroy();
//        LoanApplicationForm3.destroy();
//        LoanApplicationForm4.destroy();
//        PreDisbursalProcesses.destroy();
//
//    }

//    public static ProgressDialog showProgressDialog(Activity activity) {
//
//        if (!activity.isFinishing()) {
//            ProgressDialog mProgressDialog = ProgressDialog.show(activity, null, null);
//            mProgressDialog.setIndeterminate(true);
//            mProgressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
//            mProgressDialog.setCancelable(true);
//            mProgressDialog.setCanceledOnTouchOutside(false);
//            mProgressDialog.setContentView(R.layout.progress_dialog);
//            try {
//                Uri uri = Uri.parse("android.resource://com.flexiloan/drawable/flexiloans_img_loader.gif");
//                Glide.with(activity).asGif().load(uri).load(R.drawable.flexiloans_img_loader).into((ImageView) mProgressDialog.findViewById(R.id.custom_loading_imageView));
//            } catch (Exception e) {
//                e.fillInStackTrace();
//            }
//            return mProgressDialog;
//        }
//        return null;
//
//    }

    public static void hideProgressDialog(Activity activity, ProgressDialog progressDialog) {

        if (!activity.isFinishing()) {
            if (progressDialog != null && progressDialog.isShowing()) {
                progressDialog.dismiss();
                //progressDialog.findViewById(R.id.custom_loading_imageView).setVisibility(View.GONE);
            }
        }

    }

    public static boolean isDeviceRooted(Activity activity) {
            if (new DeviceUtils().isDeviceRooted(activity)) {
                showAlertDialogAndExitApp(activity, ("Device is rooted"));
                return true;
            } else {
                return false;
            }
    }

    /**
     * This fucntion will check if a string is empty and return empty string or else string with comma if not empty then.
     *
     * @param strToCheck
     * @return
     */

    public static String getStringWithComma(String strToCheck) {
        return TextUtils.isEmpty(strToCheck) ? "" : strToCheck + ", ";
    }

    public static String getNotAvailableString(String strToCheck) {
        return TextUtils.isEmpty(strToCheck) || strToCheck.equalsIgnoreCase("null") ? "N/A" : strToCheck;
    }

    public static class EnglishNumberToWords {

        final String[] tensNames = {"", " ten", " twenty", " thirty", " forty",
                " fifty", " sixty", " seventy", " eighty", " ninety"};

        final String[] numNames = {"", " one", " two", " three", " four", " five",
                " six", " seven", " eight", " nine", " ten", " eleven", " twelve", " thirteen",
                " fourteen", " fifteen", " sixteen", " seventeen", " eighteen", " nineteen"};

        private String convertLessThanOneThousand(int number) {
            String soFar;

            if (number % 100 < 20) {
                soFar = numNames[number % 100];
                number /= 100;
            } else {
                soFar = numNames[number % 10];
                number /= 10;

                soFar = tensNames[number % 10] + soFar;
                number /= 10;
            }
            if (number == 0)
                return soFar;
            return numNames[number] + " hundred" + soFar;
        }


//        function inWords (num) {
//            if ((num = num.toString()).length > 9) return 'overflow';
//            n = ('000000000' + num).substr(-9).match(/^(\d{2})(\d{2})(\d{2})(\d{1})(\d{2})$/);
//            if (!n) return; var str = '';
//            str += (n[1] != 0) ? (a[Number(n[1])] || b[n[1][0]] + ' ' + a[n[1][1]]) + 'crore ' : '';
//            str += (n[2] != 0) ? (a[Number(n[2])] || b[n[2][0]] + ' ' + a[n[2][1]]) + 'lakh ' : '';
//            str += (n[3] != 0) ? (a[Number(n[3])] || b[n[3][0]] + ' ' + a[n[3][1]]) + 'thousand ' : '';
//            str += (n[4] != 0) ? (a[Number(n[4])] || b[n[4][0]] + ' ' + a[n[4][1]]) + 'hundred ' : '';
//            str += (n[5] != 0) ? ((str != '') ? 'and ' : '') + (a[Number(n[5])] || b[n[5][0]] + ' ' + a[n[5][1]]) + 'only ' : '';
//            return str;
//        }

        void inWords(long num) {
            String snumber = Long.toString(num);
            if (snumber.length() > 9) {

            } else {

            }
        }

        public String convert(long number) {
            // 0 to 999 999 999 999
            if (number == 0) {
                return "zero";
            }

            String snumber = Long.toString(number);

            // pad with "0"
            String mask = "000000000000";
            DecimalFormat df = new DecimalFormat(mask);
            snumber = df.format(number);

            // XXXnnnnnnnnn
            int billions = Integer.parseInt(snumber.substring(0, 3));
            // nnnXXXnnnnnn
            int millions = Integer.parseInt(snumber.substring(3, 6));
            // nnnnnnXXXnnn
            int hundredThousands = Integer.parseInt(snumber.substring(7, 9));
            // nnnnnnnnnXXX
            int thousands = Integer.parseInt(snumber.substring(9, 12));
            // nnnnnnnXXnnn
            int lakhs = Integer.parseInt(snumber.substring(5, 7));

            int crore = Integer.parseInt(snumber.substring(3, 5));

            String tradCrore;
            switch (crore) {
                case 0:
                    tradCrore = "";
                    break;
                case 1:
                    tradCrore = convertLessThanOneThousand(crore) + " crore ";
                    break;
                default:
                    tradCrore = convertLessThanOneThousand(crore) + " crore ";

            }
            String result = tradCrore;

            String tradLakhs;
            switch (lakhs) {
                case 0:
                    tradLakhs = "";
                    break;
                case 1:
                    tradLakhs = convertLessThanOneThousand(lakhs) + " lakh ";
                    break;
                default:
                    tradLakhs = convertLessThanOneThousand(lakhs) + " lakh ";

            }

            result = result + tradLakhs;


//            String tradBillions;
//            switch (billions) {
//                case 0:
//                    tradBillions = "";
//                    break;
//                case 1:
//                    tradBillions = convertLessThanOneThousand(billions) + " billion ";
//                    break;
//                default:
//                    tradBillions = convertLessThanOneThousand(billions) + " billion ";
//            }
//            String result = tradBillions;
//
//            String tradMillions;
//            switch (millions) {
//                case 0:
//                    tradMillions = "";
//                    break;
//                case 1:
//                    tradMillions = convertLessThanOneThousand(millions) + " million ";
//                    break;
//                default:
//                    tradMillions = convertLessThanOneThousand(millions) + " million ";
//            }
//            result = result + tradMillions;

            String tradHundredThousands;
            switch (hundredThousands) {
                case 0:
                    tradHundredThousands = "";
                    break;
                case 1:
                    tradHundredThousands = "one thousand ";
                    break;
                default:
                    tradHundredThousands = convertLessThanOneThousand(hundredThousands) + " thousand ";
            }
            result = result + tradHundredThousands;

            String tradThousand;
            tradThousand = convertLessThanOneThousand(thousands);
            result = result + tradThousand;

            // remove extra spaces!
            return result.replaceAll("^\\s+", "").replaceAll("\\b\\s{2,}\\b", " ");
        }

    }

//    public static class NumberTextWatcherForThousand implements TextWatcher {
//
//        EditText editText;
//        Button bt_proceed;
//        Context context;
//        TextView textView, textView2;
//        int val = 0;
//        Float min_val, max_val;
//        EnglishNumberToWords englishNumberToWords = new EnglishNumberToWords();
//        long convertVal = 0;
//        boolean fromMyloans = false;
//        TextView mtextView3 = null;
//        TextInputLayout tl_amount;
//
//
//        public NumberTextWatcherForThousand(TextInputLayout tl_amount, EditText editText, Button bt_proceed, Context context, TextView textView, TextView textView2, float min_val, float max_val) {
//            this.editText = editText;
//            this.bt_proceed = bt_proceed;
//            this.context = context;
//            this.textView = textView;
//            this.textView2 = textView2;
//            this.min_val = min_val;
//            this.max_val = max_val;
//            this.tl_amount = tl_amount;
//            val = (int) context.getResources().getDimension(R.dimen.ten_dp);
//
//        }
//
//        public NumberTextWatcherForThousand(EditText editText, Button bt_proceed, Context context, TextView textView, TextView textView2, TextView textView3, float min_val, float max_val, boolean fromMyloans) {
//            this.editText = editText;
//            this.bt_proceed = bt_proceed;
//            this.context = context;
//            this.textView = textView;
//            this.textView2 = textView2;
//            this.min_val = min_val;
//            this.max_val = max_val;
//            this.mtextView3 = textView3;
//            this.fromMyloans = fromMyloans;
//        }
//
//
//        @Override
//        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//
//        }
//
//        @Override
//        public void onTextChanged(CharSequence s, int start, int before, int count) {
//
//            String enteredVal = editText.getText().toString();
//            if (fromMyloans) mtextView3.setVisibility(View.GONE);
//
//            if (TextUtils.isEmpty(enteredVal)) {
//                bt_proceed.setBackground(context.getResources().getDrawable(R.drawable.btn_partner_confirm_dialogue_unselected));
//                bt_proceed.setEnabled(false);
//                textView.setVisibility(View.GONE);
//                textView.setText(toTitleCase(""));
//            } else {
//
//                if (enteredVal.startsWith(".")) {
//                    enteredVal = "0.";
////                    Selection.setSelection(textView2,2);
//                }
//
//                Double selectedVal = Double.valueOf(Util.trimCommaOfString(enteredVal));
//
//                Double upperVal = Double.valueOf(String.valueOf(max_val));
//
//                Double lowerVal = Double.valueOf(String.valueOf(min_val));
//
//                int result = Double.compare(selectedVal, upperVal);
//                int result2 = Double.compare(selectedVal, lowerVal);
//                if (selectedVal.equals(lowerVal) && selectedVal != 0.0) {
//                    result2 = 1;
//                } else if (selectedVal.equals(upperVal)) {
//                    result = -1;
//                }
//
//                convertVal = selectedVal.longValue();
//                if (result == -1 && result2 == 1) {
//                    if (fromMyloans) {
//                        editText.setBackground(context.getResources().getDrawable(R.drawable.curved_4_corners_stroke_green_bg_white));
//                        //textView2.setBackground(context.getResources().getDrawable(R.drawable.curved_4_corners_stroke_green_bg_white));
//                    } else {
//                        textView2.setVisibility(View.GONE);
//                        tl_amount.setBackground(context.getResources().getDrawable(R.drawable.amount_border));
//                    }
//                    bt_proceed.setBackground(context.getResources().getDrawable(R.drawable.btn_partner_confirm_dialogue_yes));
//                    bt_proceed.setEnabled(true);
//                    textView.setVisibility(View.VISIBLE);
//                    textView.setTextColor(context.getResources().getColor(R.color.link_color));
//                    textView.setText("Rupees" + " " + toTitleCase(englishNumberToWords.convert(convertVal)));
//
//                } else if (result == 1) {
//
//                    if (fromMyloans) {
//                        bt_proceed.setEnabled(false);
//                        textView.setVisibility(View.GONE);
//                        bt_proceed.setBackground(context.getResources().getDrawable(R.drawable.btn_partner_confirm_dialogue_unselected));
//
//                        textView2.setBackground(context.getResources().getDrawable(R.drawable.curved_4_corners_stroke_bg_white_red));
//                        mtextView3.setVisibility(View.VISIBLE);
//                    } else {
//                        //tl_amount.setBackground(context.getResources().getDrawable(R.drawable.alert_amount_border));
//                        bt_proceed.setBackground(context.getResources().getDrawable(R.drawable.btn_partner_confirm_dialogue_unselected));
//                    }
//
//                    textView.setVisibility(View.GONE);
//                    textView.setText(toTitleCase(""));
//
//                } else if (result2 == -1) {
//
//                    bt_proceed.setBackground(context.getResources().getDrawable(R.drawable.btn_partner_confirm_dialogue_unselected));
//                    textView.setVisibility(View.GONE);
//                    textView.setText(toTitleCase(""));
//
//
//                } else {
//
//                    bt_proceed.setBackground(context.getResources().getDrawable(R.drawable.btn_partner_confirm_dialogue_unselected));
//                    textView.setVisibility(View.GONE);
//                    textView.setText(toTitleCase(""));
//
//                }
//
//            }
//        }
//
//        @Override
//        public void afterTextChanged(Editable s) {
//            try {
//                editText.removeTextChangedListener(this);
//
//                String value = editText.getText().toString();
//
//
//                if (value != null && !value.equals("")) {
//
//                    if (value.startsWith(".")) {
//                        editText.setText("0.");
//                        editText.setSelection(editText.getText().length());
//                    }
//                    if (value.startsWith("0") && !value.startsWith("0.")) {
//                        editText.setText("");
//
//                    }
//                    String str = editText.getText().toString().replaceAll(",", "");
//                    if (!value.equals(""))
//                        editText.setText(formatLoanAmount(Long.parseLong(str)));
//                    editText.setSelection(editText.getText().toString().length());
//                }
//                editText.addTextChangedListener(this);
//                return;
//            } catch (Exception ex) {
//                ex.printStackTrace();
//                editText.addTextChangedListener(this);
//            }
//
//        }
//
//        public static String formatLoanAmount(long number) {
//            DecimalFormat formatter = new DecimalFormat("##,##,###");
//            String yourFormattedString = formatter.format(number);
//
//            return yourFormattedString;
//        }
//
//        public String getDecimalFormattedString(String value) {
//            StringTokenizer lst = new StringTokenizer(value, ".");
//            String str1 = value;
//            String str2 = "";
//            if (lst.countTokens() > 1) {
//                str1 = lst.nextToken();
//                str2 = lst.nextToken();
//            }
//            String str3 = "";
//            int i = 0;
//            int j = -1 + str1.length();
//            if (str1.charAt(-1 + str1.length()) == '.') {
//                j--;
//                str3 = ".";
//            }
//            for (int k = j; ; k--) {
//                if (k < 0) {
//                    if (str2.length() > 0)
//                        str3 = str3 + "." + str2;
//                    return str3;
//                }
//                if (i == 3) {
//                    str3 = "," + str3;
//                    i = 0;
//                }
//                str3 = str1.charAt(k) + str3;
//                i++;
//            }
//
//        }
//
//
//    }

    public static class StringMatcher {
        private final static char KOREAN_UNICODE_START = '가';
        private final static char KOREAN_UNICODE_END = '힣';
        private final static char KOREAN_UNIT = '까' - '가';
        private final static char[] KOREAN_INITIAL = {'ㄱ', 'ㄲ', 'ㄴ', 'ㄷ', 'ㄸ', 'ㄹ', 'ㅁ', 'ㅂ', 'ㅃ', 'ㅅ'
                , 'ㅆ', 'ㅇ', 'ㅈ', 'ㅉ', 'ㅊ', 'ㅋ', 'ㅌ', 'ㅍ', 'ㅎ'};

        public static boolean match(String value, String keyword) {
            if (value == null || keyword == null)
                return false;
            if (keyword.length() > value.length())
                return false;

            int i = 0, j = 0;
            do {
                if (isKorean(value.charAt(i)) && isInitialSound(keyword.charAt(j))) {
                    if (keyword.charAt(j) == getInitialSound(value.charAt(i))) {
                        i++;
                        j++;
                    } else if (j > 0)
                        break;
                    else
                        i++;
                } else {
                    if (keyword.charAt(j) == value.charAt(i)) {
                        i++;
                        j++;
                    } else if (j > 0)
                        break;
                    else
                        i++;
                }
            } while (i < value.length() && j < keyword.length());

            return j == keyword.length();
        }

        private static boolean isKorean(char c) {
            return c >= KOREAN_UNICODE_START && c <= KOREAN_UNICODE_END;
        }

        private static boolean isInitialSound(char c) {
            for (char i : KOREAN_INITIAL) {
                if (c == i)
                    return true;
            }
            return false;
        }

        private static char getInitialSound(char c) {

            if (!isKorean(c)) {
                return c;
            }

            return KOREAN_INITIAL[(c - KOREAN_UNICODE_START) / KOREAN_UNIT];
        }
    }

//    static class CreateLead implements Response.Listener<CommonLeadModel>, Response.ErrorListener {
//
//        @Override
//        public void onErrorResponse(VolleyError error) {
//
//            try {
//
//                // why this code
//                if (error != null && error.networkResponse != null && error.networkResponse.data != null) {
//
//                    String respons = new String(error.networkResponse.data, StandardCharsets.UTF_8);
//                    String leadcode = new JSONObject(respons).get("leadCode").toString();
//
//                    Util.updateStatus(sharedPreferences.getString(CommonConstants.LEAD_CODE, ""), false, false, false, false, 0, 0, 0);
//
//                    if (from_DashBoard) {
//                        UsageTracker.trackEvent(dashBoardRef, EventHelper.App_Dashboard_Page, EventHelper.dashBoardLoad(leadcode));
//                        dashBoardRef.leadCreated();
//                    }
//                    editor.putString(CommonConstants.LEAD_CODE, leadcode);
//                    editor.apply();
//
//
//                } else {
//
//                    Dialog dialog_autoreject = new Dialog(dashBoardRef);
//                    dialog_autoreject.setContentView(R.layout.dialog_retry);
//                    dialog_autoreject.show();
//                    dialog_autoreject.setCanceledOnTouchOutside(false);
//                    Button bt_retry = dialog_autoreject.findViewById(R.id.bt_retry);
//                    TextView tv_head = dialog_autoreject.findViewById(R.id.head);
//                    tv_head.setText(dashBoardRef.getResources().getString(R.string.error));
//                    bt_retry.setOnClickListener(v -> {
//                        dialog_autoreject.dismiss();
//                        createLead(dashBoardRef, from_DashBoard);
//                    });
//
//                }
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//
//        }
//
//        @Override
//        public void onResponse(CommonLeadModel response) {
//
//            String leadcode = response.getLead().getCustomer().getCode();
//            String loan_code = response.getLead().getLoanApplication().getLoanApplicant().getLoan_code();
//
//            UsageTracker.trackEvent(dashBoardRef, EventHelper.App_Dashboard_Page, EventHelper.dashBoardLoad(leadcode));
//
//            editor.putString(CommonConstants.USER_WHATSAPP_NUMBER,response.getLead().getLoanApplication().getLoanApplicant().getWhatsapp_no());
//            editor.putString(CommonConstants.LEAD_CODE, leadcode);
//            editor.putString(CommonConstants.LOAN_CODE, loan_code);
//            editor.apply();
//
//            Util.saveStatus(sharedPreferences.getString(CommonConstants.LEAD_CODE, ""), false, false, false, false, 0, 0, 0);
//
//            if (from_DashBoard) {
//
//                // put whats up consent
//                // on Response of put consent --> create lead is to be called.
//
//                dashBoardRef.leadCreated();
//
//
//
//            } else {
//                // call is made from loanAmmountActivity
//                // make call for --> put whats up consent
//                // loanAmountRef
//
//            }
//
//        }
//    }


    public static int getCountOfDays(String expireDateString) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

        Date expireCovertedDate = null, todayWithZeroTime = null;
        try {

            expireCovertedDate = dateFormat.parse(expireDateString);

            Date today = new Date();

            todayWithZeroTime = dateFormat.parse(dateFormat.format(today));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        int cYear = 0, cMonth = 0, cDay = 0;

        Calendar cCal = Calendar.getInstance();
        cCal.setTime(todayWithZeroTime);
        cYear = cCal.get(Calendar.YEAR);
        cMonth = cCal.get(Calendar.MONTH);
        cDay = cCal.get(Calendar.DAY_OF_MONTH);


        Calendar eCal = Calendar.getInstance();
        eCal.setTime(expireCovertedDate);

        int eYear = eCal.get(Calendar.YEAR);
        int eMonth = eCal.get(Calendar.MONTH);
        int eDay = eCal.get(Calendar.DAY_OF_MONTH);

        Calendar date1 = Calendar.getInstance();
        Calendar date2 = Calendar.getInstance();

        date1.clear();
        date1.set(cYear, cMonth, cDay);
        date2.clear();
        date2.set(eYear, eMonth, eDay);

        long diff = date2.getTimeInMillis() - date1.getTimeInMillis();

        float dayCount = (float) diff / (24 * 60 * 60 * 1000);

        return (int) dayCount;
    }
    public static String addDays(String dt,int days){
        SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy");
        Calendar c = Calendar.getInstance();
        try {
            c.setTime(sdf.parse(dt));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        c.add(Calendar.DATE, days);  // number of days to add, can also use Calendar.DAY_OF_MONTH in place of Calendar.DATE
        SimpleDateFormat sdf1 = new SimpleDateFormat("dd-MMM-yyyy");
        String output = sdf1.format(c.getTime());

        return output;
    }


}