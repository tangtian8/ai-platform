package top.tangtian.balofhope;

import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;

/**
 * @author tangtian
 * @date 2026-01-24 12:39
 */
@Component
public class BeanCreationLogger implements BeanPostProcessor {

	@Override
	public Object postProcessAfterInitialization(Object bean, String beanName) {
		if (beanName.toLowerCase().contains("numberfrequency")) {
			System.out.println("========== Bean Created ==========");
			System.out.println("Bean Name: " + beanName);
			System.out.println("Bean Class: " + bean.getClass().getName());
			System.out.println("Is Proxy: " + org.springframework.aop.support.AopUtils.isAopProxy(bean));
			System.out.println("Stack Trace:");
			Thread.currentThread().getStackTrace();
			System.out.println("==================================");
		}
		return bean;
	}
}