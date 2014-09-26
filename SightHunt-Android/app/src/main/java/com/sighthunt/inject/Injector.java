package com.sighthunt.inject;

public final class Injector {

	private static Injections sProductionInjections = new Injections();
	// This will always be null when there are no test injections, to speed up
	// getting production instances
	private static Injections sTestInjections;

	private Injector() {
	}

	public static synchronized <T extends Injectable> T get(Class<T> type) {
		return getLocal(Injector.class, type);
	}

	public static synchronized <T extends Injectable> T getLocal(Class<?> forClass, Class<T> type) {
		if (sTestInjections != null) {
			T testInstance = sTestInjections.getAllowNull(forClass, type);
			if (testInstance != null) {
				return testInstance;
			}
		}
		return sProductionInjections.get(forClass, type);
	}

	public static synchronized <T extends Injectable> T getAllowNull(Class<T> type) {
		return getLocalAllowNull(Injector.class, type);
	}

	public static synchronized <T extends Injectable> T getLocalAllowNull(Class<?> forClass, Class<T> type) {
		if (sTestInjections != null) {
			T testInstance = sTestInjections.getAllowNull(forClass, type);
			if (testInstance != null) {
				return testInstance;
			}
		}
		return sProductionInjections.getAllowNull(forClass, type);
	}

	public static synchronized <T extends Injectable> void inject(Class<T> type, T object) {
		injectLocal(Injector.class, type, object);
	}

	public static synchronized <T extends Injectable> void injectLocal(Class<?> forClass, Class<T> type, T instance) {
		sProductionInjections.inject(forClass, type, instance);
	}
}