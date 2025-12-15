package com.ezh.Inventory.contacts.service;

import com.ezh.Inventory.contacts.dto.ContactDto;
import com.ezh.Inventory.contacts.dto.ContactFilter;
import com.ezh.Inventory.utils.common.CommonResponse;
import com.ezh.Inventory.utils.exception.CommonException;
import org.springframework.data.domain.Page;

import java.util.List;

public interface  ContactService {
    CommonResponse createContact(ContactDto contact);
    CommonResponse updateContact(Long id, ContactDto contactDto);
    ContactDto getContact(Long id);
    Page<ContactDto> getAllContacts(ContactFilter contactFilter, Integer page, Integer size);
    CommonResponse toggleStatus(Long id, Boolean active);
    List<ContactDto> searchContact(ContactFilter contactFilter) throws CommonException;
}
