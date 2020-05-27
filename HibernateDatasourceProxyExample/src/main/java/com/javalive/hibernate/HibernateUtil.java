package com.javalive.hibernate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.hibernate.SessionFactory;
import org.hibernate.boot.Metadata;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Environment;

import com.javalive.entity.Person;
import com.mysql.cj.jdbc.MysqlDataSource;

import net.ttddyy.dsproxy.ExecutionInfo;
import net.ttddyy.dsproxy.QueryInfo;
import net.ttddyy.dsproxy.listener.QueryExecutionListener;
import net.ttddyy.dsproxy.listener.logging.CommonsLogLevel;
import net.ttddyy.dsproxy.support.ProxyDataSourceBuilder;

/**
 * @author javalive.com
 */
public class HibernateUtil {

	private static StandardServiceRegistry registry;
	private static SessionFactory sessionFactory;

	public static SessionFactory getSessionFactory() {
		if (sessionFactory == null) {
			try {
				StandardServiceRegistryBuilder registryBuilder = new StandardServiceRegistryBuilder();

				Map<String, Object> settings = new HashMap<>();
				settings.put(Environment.DATASOURCE, getDataSource());
				// settings.put(Environment.HBM2DDL_AUTO, "update");

				registryBuilder.applySettings(settings);
				registry = registryBuilder.build();
				MetadataSources sources = new MetadataSources(registry).addAnnotatedClass(Person.class);
				Metadata metadata = sources.getMetadataBuilder().build();
				sessionFactory = metadata.getSessionFactoryBuilder().build();
			} catch (Exception e) {
				if (registry != null) {
					StandardServiceRegistryBuilder.destroy(registry);
				}
				e.printStackTrace();
			}
		}
		return sessionFactory;
	}

	public static void shutdown() {
		if (registry != null) {
			StandardServiceRegistryBuilder.destroy(registry);
		}
	}

	private static DataSource getDataSource() {

		// Create DataSource
		MysqlDataSource ds = new MysqlDataSource();
		ds.setURL("jdbc:mysql://localhost:3306/test1");
		ds.setUser("root");
		ds.setPassword("root");

		// Create ProxyDataSource
		DataSource dataSource = ProxyDataSourceBuilder.create(ds).logQueryByCommons(CommonsLogLevel.INFO)
				// .logQueryToSysOut()
				.countQuery().multiline().listener(new QueryExecutionListener() {
					@Override
					public void beforeQuery(ExecutionInfo info, List<QueryInfo> queryInfos) {
						System.out.println("Before Query Execution");
					}

					@Override
					public void afterQuery(ExecutionInfo info, List<QueryInfo> aqueryInfosrg1) {
						System.out.println("\nAfter Query Execution");
					}
				}).build();

		return dataSource;
	}
}
