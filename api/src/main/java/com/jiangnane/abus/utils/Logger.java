package com.jiangnane.abus.utils;

import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.os.Process;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * 日志输出类
 * 如果注册Handler可将所有日志内容抛出
 *
 * @author hanwei
 */
public class Logger {
    private static final String TAG = "Logger";

    private static boolean sIsDebuggable = true;
    private static Handler sHandler = null;
    private static int OUT_SIGNAL = -1;


    public static void setDebuggable(boolean isDebuggable) {
        sIsDebuggable = isDebuggable;
    }

    public static boolean isDebuggable() {
        return sIsDebuggable;
    }

    /**
     * 注册接收日志的handler
     *
     * @param handler
     * @param outSignal Message的what
     */
    public static void register(Handler handler, int outSignal) {
        sHandler = handler;
        OUT_SIGNAL = outSignal;
    }

    /**
     * 反注册
     *
     * @param handler
     */
    public static void unRegister(Handler handler) {
        if (handler == sHandler) {
            sHandler = null;
            OUT_SIGNAL = -1;
        }
    }

    private static void logOut(int level, String tag, String msg) {
        if (sHandler == null) {
            return;
        }

        int color = Color.WHITE;
        String levelFlog = null;

        switch (level) {
            case Log.VERBOSE:
                color = 0xFFBBBBBB;
                levelFlog = "V";
                break;
            case Log.DEBUG:
                color = 0xFF6BB5E2;
                levelFlog = "D";
                break;
            case Log.INFO:
                color = 0xFF58B419;
                levelFlog = "I";
                break;
            case Log.WARN:
                color = 0xFFDC9051;
                levelFlog = "W";
                break;
            case Log.ERROR:
                color = 0xFFFF6B68;
                levelFlog = "E";
                break;
            default:
                levelFlog = "";
                break;
        }

        StringBuilder sb = new StringBuilder();
        sb.append(levelFlog);
        sb.append("\t:");
        sb.append(tag);
        sb.append("->");
        sb.append(msg);
        sb.append("\n");

        SpannableString spanString = new SpannableString(sb.toString());
        ForegroundColorSpan span = new ForegroundColorSpan(color);
        spanString.setSpan(span, 0, sb.length(),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        Message msgObj = new Message();
        msgObj.what = OUT_SIGNAL;
        msgObj.obj = spanString;
        msgObj.arg1 = level;

        sHandler.sendMessage(msgObj);
    }

    public static void v(String msg) {
        v(null, msg, null);
    }

    public static void v(String tag, String msg) {
        v(tag, msg, null);
    }

    /**
     * Send a VERBOSE log message and log the exception.
     */
    public static void v(String tag, String msg, Throwable tr) {

        if (msg == null) {
            msg = "\'Log Message is Null.\'";
        }

        if (tag == null) {
            tag = TAG;
        }

        if (sIsDebuggable) {
            if (tr == null) {
                Log.v(tag, msg);
            } else {
                Log.v(tag, msg, tr);
            }
        }

        logOut(Log.VERBOSE, tag, msg);
    }

    public static void d(String msg) {
        d(null, msg, null);
    }

    public static void d(String tag, String msg) {
        d(tag, msg, null);
    }

    /**
     * Send a DEBUG log message and log the exception.
     */
    public static void d(String tag, String msg, Throwable tr) {

        if (msg == null) {
            msg = "\'Log Message is Null.\'";
        }

        if (tag == null) {
            tag = TAG;
        }

        if (sIsDebuggable) {
            if (tr == null) {
                Log.d(tag, msg);
            } else {
                Log.d(tag, msg, tr);
            }
        }

        logOut(Log.DEBUG, tag, msg);
    }

    public static void i(String msg) {
        i(null, msg, null);
    }

    public static void i(String tag, String msg) {
        i(tag, msg, null);
    }

    /**
     * Send a INFO log message and log the exception.
     */
    public static void i(String tag, String msg, Throwable tr) {

        if (msg == null) {
            msg = "\'Log Message is Null.\'";
        }

        msg = Process.myPid() + ":" + msg;

        if (tag == null) {
            tag = TAG;
        }

        if (sIsDebuggable) {
            if (tr == null) {
                Log.i(tag, msg);
            } else {
                Log.i(tag, msg, tr);
            }
        }

        logOut(Log.INFO, tag, msg);
    }

    public static void w(String msg) {
        w(null, msg, null);
    }

    public static void w(String tag, String msg) {
        w(tag, msg, null);
    }

    /**
     * Send a WARN log message and log the exception.
     */
    public static void w(String tag, String msg, Throwable tr) {
        if (msg == null) {
            msg = "\'Log Message is Null.\'";
        }

        if (tag == null) {
            tag = TAG;
        }

        if (sIsDebuggable) {
            if (tr == null) {
                Log.w(tag, msg);
            } else {
                Log.w(tag, msg, tr);
            }
        }

        logOut(Log.WARN, tag, msg);
    }

    public static void e(String msg) {
        e(null, msg, null);
    }

    public static void e(String tag, String msg) {
        e(tag, msg, null);
    }

    /**
     * Send a ERROR log message and log the exception.
     */
    public static void e(String tag, String msg, Throwable tr) {

        if (msg == null) {
            msg = "\'Log Message is Null.\'";
        }

        if (tag == null) {
            tag = TAG;
        }

        if (sIsDebuggable) {
            if (tr == null) {
                Log.e(tag, msg);
            } else {
                Log.e(tag, msg, tr);
            }
        }

        logOut(Log.ERROR, tag, msg);
    }

    public static void wtf(String msg) {
        wtf(null, msg, null);
    }

    public static void wtf(String tag, String msg) {
        wtf(tag, msg, null);
    }

    /**
     * What a Terrible Failure: Report an exception that should never happen.
     * This method will print log whatever the debug mode is.
     */
    public static void wtf(String tag, String msg, Throwable tr) {
        if (msg == null) {
            msg = "\'Log Message is Null.\'";
        }

        if (tag == null) {
            tag = TAG;
        }

        if (tr == null) {
            Log.wtf(tag, msg);
        } else {
            Log.wtf(tag, msg, tr);
        }

        logOut(Log.ERROR, tag, msg);
    }

    //规定每段显示的长度
    private static int LOG_MAX_LENGTH = 2000;

    public static void longI(String TAG, String msg) {
        int strLength = msg.length();
        int start = 0;
        int end = LOG_MAX_LENGTH;
        for (int i = 0; i < 100; i++) {
            //剩下的文本还是大于规定长度则继续重复截取并输出
            if (strLength > end) {
                Logger.i(TAG + i, msg.substring(start, end));
                start = end;
                end = end + LOG_MAX_LENGTH;
            } else {
                Logger.i(TAG, msg.substring(start, strLength));
                break;
            }
        }
    }

    public static void printJson(String msg) {
        d(null, json(msg));
    }


    /**
     * 按照JSON格式打印日志
     */
    public static void printJson(String tag, String msg) {
        d(tag, json(msg));
    }

    private static String json(String json) {

        String message = json;

        if (TextUtils.isEmpty(json)) {
            message = "Empty/Null json content";
        } else {
            try {

                if (json.startsWith("{")) {
                    JSONObject e1 = new JSONObject(json);
                    message = e1.toString(4);
                }

                if (json.startsWith("[")) {
                    JSONArray e = new JSONArray(json);
                    message = e.toString(4);
                }
            } catch (JSONException var4) {
                message = var4.getCause().getMessage() + "\n" + json;
            }

        }

        return message;
    }


    public static void printStack() {
        if(true) {
            return;
        }

        String stackStr = "";
        StackTraceElement[] stacks = new Throwable().getStackTrace();
        if(stacks != null && stacks.length > 1) {
            stackStr = new Throwable().getStackTrace()[1].toString();
        }

        Logger.i("ThreadName: " + Thread.currentThread().getName() + " ; Stack: " + stackStr);
    }
}
