package com.cenck.es.boot;

import com.cenck.es.annotation.EsComponentScan;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.util.ClassUtils;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * @author zhz
 * @version V1.0
 * @since 2019/2/23 - 17:45
 **/
public class EsComponentScanRegister implements ImportBeanDefinitionRegistrar {

	@Override
	public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {

		Set<String> packagesToScan = this.getPackagesToScan(importingClassMetadata);

		registerServiceAnnotationBeanPostProcessor(packagesToScan, registry);

	}

	/**
	 *
	 * @param packagesToScan packages to scan without resolving placeholders
	 * @param registry       {@link BeanDefinitionRegistry}
	 * @since 2.5.8
	 */
	private void registerServiceAnnotationBeanPostProcessor(Set<String> packagesToScan, BeanDefinitionRegistry registry) {
//		BeanDefinitionBuilder builder = rootBeanDefinition(ServiceAnnotationBeanPostProcessor.class);
//		builder.addConstructorArgValue(packagesToScan);
//		builder.setRole(BeanDefinition.ROLE_INFRASTRUCTURE);
//		AbstractBeanDefinition beanDefinition = builder.getBeanDefinition();
//		BeanDefinitionReaderUtils.registerWithGeneratedName(beanDefinition, registry);
	}



	private Set<String> getPackagesToScan(AnnotationMetadata metadata) {
		AnnotationAttributes attributes = AnnotationAttributes.fromMap(
				metadata.getAnnotationAttributes(EsComponentScan.class.getName()));
		String[] basePackages = attributes.getStringArray("basePackages");
		Class<?>[] basePackageClasses = attributes.getClassArray("basePackageClasses");
		String[] value = attributes.getStringArray("value");
		// Appends value array attributes
		Set<String> packagesToScan = new LinkedHashSet<String>(Arrays.asList(value));
		packagesToScan.addAll(Arrays.asList(basePackages));
		for (Class<?> basePackageClass : basePackageClasses) {
			packagesToScan.add(ClassUtils.getPackageName(basePackageClass));
		}
		if (packagesToScan.isEmpty()) {
			return Collections.singleton(ClassUtils.getPackageName(metadata.getClassName()));
		}
		return packagesToScan;
	}
}
