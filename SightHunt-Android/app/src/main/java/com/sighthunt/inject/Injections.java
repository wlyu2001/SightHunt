package com.sighthunt.inject;

import java.util.IdentityHashMap;
import java.util.Map;

/* default*/ class Injections {
	private Map<Class<?>, Map<Class<? extends Injectable>, Injectable>> mInstanceMap = new IdentityHashMap<Class<?>, Map<Class<? extends Injectable>, Injectable>>();

	@SuppressWarnings("unchecked")
	public <T extends Injectable> T get(Class<?> forClass, Class<T> type) {
		final Map<Class<? extends Injectable>, Injectable> injectableMap = mInstanceMap.get(forClass);
		if (injectableMap == null) {
			throw new IllegalStateException("Could not find any local injections for " + forClass.getName());
		}
		final Injectable injectable = injectableMap.get(type);
		if (injectable == null) {
			throw new IllegalStateException("Could not find any injection for " + type.getName());
		}
		return (T) injectable;
	}

	@SuppressWarnings("unchecked")
	public <T extends Injectable> T getAllowNull(Class<?> forClass, Class<T> type) {
		final Map<Class<? extends Injectable>, Injectable> injectableMap = mInstanceMap.get(forClass);
		if (injectableMap == null) {
			return null;
		}
		final Injectable injectable = injectableMap.get(type);
		return (T) injectable;
	}

	public <T extends Injectable> void inject(Class<?> forClass, Class<T> type, T instance) {
		Map<Class<? extends Injectable>, Injectable> injectableMap = mInstanceMap.get(forClass);
		if (injectableMap == null) {
			injectableMap = new IdentityHashMap<Class<? extends Injectable>, Injectable>();
			mInstanceMap.put(forClass, injectableMap);
		}
		injectableMap.put(type, instance);
	}

	public <T extends Injectable> void clear(Class<?> forClass, Class<T> type) {
		Map<Class<? extends Injectable>, Injectable> injectableMap = mInstanceMap.get(forClass);
		if (injectableMap != null) {
			injectableMap.remove(type);
		}
	}
}
