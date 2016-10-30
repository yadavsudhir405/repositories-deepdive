/*
 * Copyright 2012 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.olivergierke.deepdive;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

import java.util.List;

import org.hamcrest.Matchers;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import com.mysema.query.types.expr.BooleanExpression;

/**
 * Integration tests for {@link CustomerRepository}.
 * 
 * @author Oliver Gierke
 * @since Step 2
 */
public class CustomerRepositoryIntegrationTest extends AbstractIntegrationTest {

	@Autowired
	CustomerRepository repository;

	/**
	 * @since Step 2.1
	 */
	@Test
	public void findsCustomerById() {

		Customer customer = repository.findOne(1L);

		assertThat(customer, is(notNullValue()));
		assertThat(customer.getFirstname(), is("Dave"));
		assertThat(customer.getLastname(), is("Matthews"));
	}

	/**
	 * @since Step 2.2
	 */
	@Test
	public void savesNewCustomer() {

		Customer stefan = new Customer("Stefan", "Lassard");
		Customer result = repository.save(stefan);

		assertThat(result, is(notNullValue()));
		assertThat(result.getId(), is(notNullValue()));
		assertThat(result.getFirstname(), is("Stefan"));
		assertThat(result.getLastname(), is("Lassard"));
	}

	/**
	 * @since Step 2.3
	 */
	@Test
	public void savesExistingCustomer() {

		Customer dave = repository.findOne(1L);
		dave.setEmailAddress(new EmailAddress("davematthews@dmband.com"));
		repository.save(dave);

		Customer result = repository.findOne(1L);

		assertThat(result, is(notNullValue()));
		assertThat(result.getId(), is(notNullValue()));
		assertThat(result.getFirstname(), is("Dave"));
		assertThat(result.getEmailAddress(), is(new EmailAddress("davematthews@dmband.com")));
	}

	/**
	 * @since Step 2.4
	 */
	@Test
	public void findsCustomersByEmailAddress() {

		Customer result = repository.findByEmailAddress(new EmailAddress("dave@dmband.com"));

		assertThat(result, is(notNullValue()));
		assertThat(result.getFirstname(), is("Dave"));
		assertThat(result.getLastname(), is("Matthews"));
	}

	/**
	 * @since Step 3.1
	 */
	@Test
	public void findsAllCustomers() {

		List<Customer> customers = repository.findAll();
		assertThat(customers, hasSize(3));
	}

	/**
	 * @since Step 3.2
	 */
	@Test
	public void deletesCustomer() {

		repository.delete(1L);
		assertThat(repository.findOne(1L), is(nullValue()));
	}

	/**
	 * @since Step 4.1
	 */
	@Test
	public void accessesCustomersPageByPage() {

		Page<Customer> result = repository.findAll(new PageRequest(1, 1));

		assertThat(result, is(notNullValue()));
		assertThat(result.isFirstPage(), is(false));
		assertThat(result.isLastPage(), is(false));
		assertThat(result.getNumberOfElements(), is(1));
	}

	/**
	 * @since Step 8
	 */
	@Test
	public void executesQuerydslPredicate() {

		Customer dave = repository.findByEmailAddress(new EmailAddress("dave@dmband.com"));
		Customer carter = repository.findByEmailAddress(new EmailAddress("carter@dmband.com"));

		QCustomer customer = QCustomer.customer;

		BooleanExpression firstnameStartsWithDa = customer.firstname.startsWith("Da");
		BooleanExpression lastnameContainsEau = customer.lastname.contains("eau");

		Iterable<Customer> result = repository.findAll(firstnameStartsWithDa.or(lastnameContainsEau));

		assertThat(result, is(Matchers.<Customer> iterableWithSize(2)));
		assertThat(result, hasItems(dave, carter));
	}
}
