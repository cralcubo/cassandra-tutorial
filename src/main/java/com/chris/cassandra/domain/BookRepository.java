package com.chris.cassandra.domain;

import java.util.UUID;

import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.stereotype.Repository;

import com.chris.cassandra.model.Book;

@Repository
public interface BookRepository extends CassandraRepository<Book, UUID> {

}
