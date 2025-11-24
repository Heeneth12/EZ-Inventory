package com.ezh.Inventory.contacts.repository;

import com.ezh.Inventory.contacts.entiry.Contact;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ContactRepository extends JpaRepository<Contact, Long> {

    Boolean existsByContactCode(String contactCode);
}
