package com.chris.cassandra;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import org.apache.cassandra.exceptions.ConfigurationException;
import org.apache.thrift.transport.TTransportException;
import org.cassandraunit.shaded.com.google.common.collect.ImmutableSet;
import org.cassandraunit.utils.EmbeddedCassandraServerHelper;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.cassandra.core.CassandraAdminOperations;
import org.springframework.data.cassandra.core.cql.CqlIdentifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.chris.cassandra.domain.BookRepository;
import com.chris.cassandra.model.Book;
import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.Session;
import com.datastax.driver.core.utils.UUIDs;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = CassandraConfig.class)
public class BookRepositoryIntegrationTest {
	
	@Autowired
	private BookRepository repository;
	
	@Autowired
	private CassandraAdminOperations admin;
	
	public static final String KEYSPACE_CREATION_QUERY = "CREATE KEYSPACE IF NOT EXISTS testKeySpace WITH replication = { 'class': 'SimpleStrategy', 'replication_factor': '3' };";

    public static final String KEYSPACE_ACTIVATE_QUERY = "USE testKeySpace;";

    public static final String DATA_TABLE_NAME = "book";
	
	
	@BeforeClass
	public static void startCassandra() throws ConfigurationException, TTransportException, IOException, InterruptedException {
		EmbeddedCassandraServerHelper.startEmbeddedCassandra();
		Cluster cluster = Cluster.builder()
							    .addContactPoint("127.0.0.1")
							    .withPort(9142)
							    .build();
		Session session = cluster.connect();
		session.execute(KEYSPACE_CREATION_QUERY);
		session.execute(KEYSPACE_ACTIVATE_QUERY);
	}
	
	@AfterClass
	public static void stopCassandra() {
		EmbeddedCassandraServerHelper.cleanEmbeddedCassandra();
	}
	
	@Before
	public void createTable () {
		admin.createTable(true, CqlIdentifier.of(DATA_TABLE_NAME), Book.class, new HashMap<>());
	}
	
	@After
	public void deleteTable() {
		admin.dropTable(CqlIdentifier.of(DATA_TABLE_NAME));
	}
	
	@Test
	public void createBook() {
		Book book = new Book(UUIDs.timeBased(), "book 1", "author", ImmutableSet.of("Computer", "SW"));
		repository.save(book);
		List<Book> books = repository.findAll();
		
		assertEquals(1, books.size());
		assertEquals("book 1", books.get(0).getTitle());
		
		System.out.println(books.get(0).getPublisher());
	}

}
