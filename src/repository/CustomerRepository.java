package repository;

import domain.Account;
import domain.Customer;

import java.util.*;

public class CustomerRepository {
    public final Map<String, Customer> customerById = new HashMap<>();

    public List<Customer> findAll() {
        return new ArrayList<>(customerById.values());
    }

    public void save(Customer c) {
        customerById.put(c.getId(),c);
    }
}
