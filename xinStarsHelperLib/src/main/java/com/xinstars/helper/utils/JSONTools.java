package com.xinstars.helper.utils;

import java.util.ArrayList;
import java.util.Iterator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.Bundle;

public class JSONTools
{
	public static Bundle toBundle(final JSONObject jsonObj) throws JSONException
	{
		Preconditions.checkNotNull(jsonObj);

		final Bundle bundle = new Bundle();
		@SuppressWarnings("unchecked")
		final Iterator<String> iterator = jsonObj.keys();
		
		while (iterator.hasNext())
		{
			final String key = iterator.next();
			if (jsonObj.isNull(key))
			{
				bundle.putString(key, "null");
				continue;
			}

			final Object value = jsonObj.get(key);
			if (value instanceof JSONObject)
				bundle.putBundle(key, toBundle((JSONObject) value));
			else if (value instanceof JSONArray)
				bundle.putParcelableArrayList(key, toBundleArray((JSONArray) value));
			else if (value instanceof Boolean)
				bundle.putBoolean(key, (Boolean) value);
			else if (value instanceof String)
				bundle.putString(key, (String) value);
			else if (value instanceof Integer)
				bundle.putInt(key, (Integer) value);
			else if (value instanceof Long)
				bundle.putLong(key, (Long) value);
			else if (value instanceof Double) 
				bundle.putDouble(key, (Double) value);
		}
		return bundle;
	}

	public static ArrayList<Bundle> toBundleArray(final JSONArray jsonArray) throws JSONException
	{
		Preconditions.checkNotNull(jsonArray);

		final ArrayList<Bundle> bundles = new ArrayList<Bundle>();
		for (int i = 0; i < jsonArray.length(); i++)
		{
			bundles.add(toBundle(jsonArray.optJSONObject(i)));
		}
		return bundles;
	}

}
