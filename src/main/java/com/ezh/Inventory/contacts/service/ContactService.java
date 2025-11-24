package com.ezh.Inventory.contacts.service;

import com.ezh.Inventory.contacts.dto.ContactDto;
import com.ezh.Inventory.utils.common.CommonResponse;

import java.util.List;

public interface  ContactService {
    CommonResponse createContact(ContactDto contact);
    CommonResponse updateContact(Long id, ContactDto contactDto);
    ContactDto getContact(Long id);
    List<ContactDto> getAllContacts();
    CommonResponse toggleStatus(Long id, Boolean active);
}
