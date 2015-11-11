// Service provider framework sketch

// Noninstantiable class for service registration and access - Pages 8-9
package org.effectivejava.examples.chapter02.item01;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
/**
 * 灵活的静态工厂方法构成了服务提供者框架(Service Provider Framework)的基础
 * 服务访问API一般允许但是不要求客户端指定某种选择提供者的条件。如果没有这样的规定，API就会返回默认实现的一个实例。
 * @author dissidia986
 *
 */
public class Services {
	/**
	 * 考虑用静态工厂方法代替构造器
	 */
	private Services() {
	} // Prevents instantiation (Item 4)

	// Maps service names to services
	private static final Map<String, Provider> providers = new ConcurrentHashMap<String, Provider>();
	public static final String DEFAULT_PROVIDER_NAME = "<def>";

	// Provider registration API
	public static void registerDefaultProvider(Provider p) {
		registerProvider(DEFAULT_PROVIDER_NAME, p);
	}

	public static void registerProvider(String name, Provider p) {
		providers.put(name, p);
	}

	// Service access API
	public static Service newInstance() {
		return newInstance(DEFAULT_PROVIDER_NAME);
	}

	public static Service newInstance(String name) {
		Provider p = providers.get(name);
		if (p == null)
			throw new IllegalArgumentException(
					"No provider registered with name: " + name);
		return p.newService();
	}
}
