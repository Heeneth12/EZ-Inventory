package com.ezh.Inventory.contacts.controller;


import com.ezh.Inventory.contacts.dto.ContactDto;
import com.ezh.Inventory.contacts.service.ContactService;
import com.ezh.Inventory.utils.common.CommonResponse;
import com.ezh.Inventory.utils.common.ResponseResource;
import com.ezh.Inventory.utils.exception.CommonException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/v1/contact")
@AllArgsConstructor
public class ContactsController {

    private final ContactService contactService;

    /**
     * @Method : createContact
     * @Discriptim :
     *
     * @param contactDto
     * @return
     * @throws CommonException
     */
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseResource<CommonResponse> createContact(@RequestBody ContactDto contactDto) throws CommonException{
        log.info("Creating new Contact with {}", contactDto);
        CommonResponse response = contactService.createContact(contactDto);
        return ResponseResource.success(HttpStatus.CREATED, response, " SUCCESSFULLY Created ");
    }

    /**
     *
     * @Method : updateContact
     * @Discriptim :
     *
     * @param id
     * @param contactDto
     * @return
     * @throws CommonException
     */
    @PostMapping(value = "/{id}/update", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseResource<CommonResponse> updateContact(@PathVariable Long id, @RequestBody ContactDto contactDto) throws CommonException {
        log.info("Updating Contact {}: {}", id, contactDto);
        CommonResponse response = contactService.updateContact(id, contactDto);
        return ResponseResource.success(HttpStatus.OK, response, "Successfully Updated");
    }

    /**
     * @Method : getContact
     * @Discriptim :
     *
     * @param id
     * @return
     * @throws CommonException
     */
    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseResource<ContactDto> getContact(@PathVariable Long id) throws CommonException  {
        log.info("Fetching Contact with ID: {}", id);
        ContactDto contactDto = contactService.getContact(id);
        return ResponseResource.success(HttpStatus.OK, contactDto, "Contact fetched successfully");
    }

//    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
//    public ResponseResource<List<ContactDto>> getAllContacts(@RequestParam(required = false) ContactType type) {
//        log.info("Fetching all contacts with type: {}", type);
//        List<ContactDto> contacts = (type != null) ? contactService.getContactsByType(type) : contactService.getAllContacts();
//        return ResponseResource.success(HttpStatus.OK, contacts, "Contacts fetched successfully");
//    }

    /**
     * @Method : toggleStatus
     * @Discriptim :
     *
     * @param id
     * @param active
     * @return
     * @throws CommonException
     */
    @PatchMapping(value = "/{id}/status")
    public ResponseResource<CommonResponse> toggleStatus(@PathVariable Long id, @RequestParam Boolean active) throws CommonException {
        log.info("Toggling status of Contact {} to {}", id, active);
        CommonResponse response = contactService.toggleStatus(id, active);
        return ResponseResource.success(HttpStatus.OK, response, "Status updated successfully");
    }
}
