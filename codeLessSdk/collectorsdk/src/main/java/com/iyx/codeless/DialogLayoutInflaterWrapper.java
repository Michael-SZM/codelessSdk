package com.iyx.codeless;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.XmlResourceParser;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.util.Preconditions;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.iyx.collectorsdk.R;

import org.xmlpull.v1.XmlPullParser;

public class DialogLayoutInflaterWrapper extends LayoutInflater {

    /**
     * @param context
     * @return 如果自动打点被禁用，返回系统LayoutInflater
     */
    @SuppressLint("RestrictedApi")
    public static LayoutInflater from(@NonNull Context context) {
        Preconditions.checkNotNull(context);

        if (!AutoTrackerSwitch.getInstance().isAutoTrackEnable()) {
            return LayoutInflater.from(context);
        }

        return new DialogLayoutInflaterWrapper(LayoutInflater.from(context), context);
    }

    /**
     * @return 如果自动打点被禁用，返回系统LayoutInflater
     */
    @SuppressLint("RestrictedApi")
    public static LayoutInflater from(@NonNull View view) {
        Preconditions.checkNotNull(view);

        Context context = view.getContext();

        if (!AutoTrackerSwitch.getInstance().isAutoTrackEnable()) {
            return LayoutInflater.from(context);
        }

        return new DialogLayoutInflaterWrapper(LayoutInflater.from(context), context);
    }

    /**
     * @param inflater
     * @return 如果自动打点被禁用，返回系统LayoutInflater
     */
    @SuppressLint("RestrictedApi")
    public static LayoutInflater wrapInflater(@NonNull LayoutInflater inflater) {
        Preconditions.checkNotNull(inflater);

        if (!AutoTrackerSwitch.getInstance().isAutoTrackEnable()) {
            return inflater;
        }

        return new DialogLayoutInflaterWrapper(inflater, inflater.getContext());
    }

    /**
     * @param context
     * @param resource
     * @param root
     * @return 如果自动打点被禁用，使用系统LayoutInflater
     */
    public static View inflate(Context context, @LayoutRes int resource, ViewGroup root) {
        if (!AutoTrackerSwitch.getInstance().isAutoTrackEnable()) {
            return View.inflate(context, resource, root);
        }

        LayoutInflater factory = DialogLayoutInflaterWrapper.from(context);
        return factory.inflate(resource, root);
    }

    public static View inflate(Context context, @LayoutRes int resource, ViewGroup root, boolean attachToRoot) {
        if (!AutoTrackerSwitch.getInstance().isAutoTrackEnable()) {
            root = attachToRoot ? root : null;
            return View.inflate(context, resource, root);
        }

        LayoutInflater factory = DialogLayoutInflaterWrapper.from(context);
        return factory.inflate(resource, root, attachToRoot);
    }

    private LayoutInflater mInflater;

    protected DialogLayoutInflaterWrapper(LayoutInflater inflater, Context context) {
        super(inflater, context);
        mInflater = inflater;
    }

    @Override
    public LayoutInflater cloneInContext(Context newContext) {
        return DialogLayoutInflaterWrapper.wrapInflater(mInflater.cloneInContext(newContext));
    }

    @Override
    public View inflate(XmlPullParser parser, @Nullable ViewGroup root) {
        return mInflater.inflate(parser, root);
    }

    @Override
    public View inflate(XmlPullParser parser, @Nullable ViewGroup root, boolean attachToRoot) {
        return mInflater.inflate(parser, root, attachToRoot);
    }

    public View inflate(@LayoutRes int resource, @Nullable ViewGroup root) {
        final Resources res = getContext().getResources();

        final XmlResourceParser parser = res.getLayout(resource);
        try {
            View view = inflate(parser, root);

            boolean attachToRoot = root != null;
            if (!attachToRoot) {
                view.setTag(R.id.id_dialog_namespace_tag, ResourceHelper.getResourceEntryName(getContext(), resource));
                return view;
            }

            int childCount = root.getChildCount();
            View tagedView = root.getChildAt(childCount - 1);
            tagedView.setTag(R.id.id_dialog_namespace_tag, ResourceHelper.getResourceEntryName(getContext(), resource));
            return view;
        } finally {
            parser.close();
        }
    }


    public View inflate(@LayoutRes int resource, @Nullable ViewGroup root, boolean attachToRoot) {
        final Resources res = getContext().getResources();

        final XmlResourceParser parser = res.getLayout(resource);
        try {
            View view = inflate(parser, root, attachToRoot);

            attachToRoot = (attachToRoot && root != null);
            if (!attachToRoot) {
                view.setTag(R.id.id_dialog_namespace_tag, ResourceHelper.getResourceEntryName(getContext(), resource));
                return view;
            }

            int childCount = root.getChildCount();
            View tagedView = root.getChildAt(childCount - 1);
            tagedView.setTag(R.id.id_dialog_namespace_tag, ResourceHelper.getResourceEntryName(getContext(), resource));
            return view;
        } finally {
            parser.close();
        }
    }
}
