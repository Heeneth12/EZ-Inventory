package com.ezh.Inventory.contacts.service;

import com.ezh.Inventory.contacts.dto.ContactDto;
import com.ezh.Inventory.contacts.entiry.Contact;
import com.ezh.Inventory.contacts.repository.ContactRepository;
import com.ezh.Inventory.utils.common.CommonResponse;
import com.ezh.Inventory.utils.common.Status;
import com.ezh.Inventory.utils.exception.BadRequestException;
import com.ezh.Inventory.utils.exception.CommonException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ContactServiceImpl implements ContactService {

    private final ContactRepository repository;

    @Override
    @Transactional
    public CommonResponse createContact(ContactDto contactDto) throws CommonException {
        log.info("");
        if (repository.existsByContactCode(contactDto.getContactCode())) {
            throw new BadRequestException("Contact code already exists");
        }

        Contact contact = convertToEntity(contactDto);
        repository.save(contact);

        return CommonResponse.builder()
                .message("Contact created successfully")
                .status(Status.SUCCESS)
                .id(String.valueOf(contact.getId()))
                .build();
    }

    @Override
    @Transactional
    public CommonResponse updateContact(Long id, ContactDto contactDto) throws CommonException {
        Contact existing = repository.findById(id)
                .orElseThrow(() -> new BadRequestException("Contact not found"));

        existing.setContactCode(contactDto.getContactCode());
        existing.setName(contactDto.getName());
        existing.setEmail(contactDto.getEmail());
        existing.setPhone(contactDto.getPhone());
        existing.setGstNumber(contactDto.getGstNumber());
        existing.setType(contactDto.getType());
        existing.setActive(contactDto.getActive() != null ? contactDto.getActive() : true);

        repository.save(existing);

        return CommonResponse.builder()
                .message("Contact updated successfully")
                .status(Status.SUCCESS)
                .id(String.valueOf(existing.getId()))
                .build();
    }


    @Override
    @Transactional(readOnly = true)
    public ContactDto getContact(Long id) throws CommonException {
        Contact contact = repository.findById(id)
                .orElseThrow(() -> new BadRequestException("Contact not found"));

        return convertToDTO(contact);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ContactDto> getAllContacts() {
        return repository.findAll()
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public CommonResponse toggleStatus(Long id, Boolean active) throws CommonException {
        Contact contact = repository.findById(id)
                .orElseThrow(() -> new BadRequestException("Contact not found"));

        contact.setActive(active);
        repository.save(contact);

        String statusMsg = active ? "activated" : "deactivated";
        return CommonResponse.builder()
                .message("Contact " + statusMsg + " successfully")
                .status(Status.SUCCESS)
                .id(String.valueOf(contact.getId()))
                .build();
    }


    private Contact convertToEntity(ContactDto contactDto) {
        return Contact.builder()
                .contactCode(contactDto.getContactCode())
                .name(contactDto.getName())
                .email(contactDto.getEmail())
                .phone(contactDto.getPhone())
                .gstNumber(contactDto.getGstNumber())
                .type(contactDto.getType())
                .active(contactDto.getActive() != null ? contactDto.getActive() : true) // default true
                .build();
    }

    private ContactDto convertToDTO(Contact contact) {
        return ContactDto.builder()
                .id(contact.getId())
                .contactCode(contact.getContactCode())
                .name(contact.getName())
                .email(contact.getEmail())
                .phone(contact.getPhone())
                .gstNumber(contact.getGstNumber())
                .type(contact.getType())
                .active(contact.getActive())
                .build();
    }
}
