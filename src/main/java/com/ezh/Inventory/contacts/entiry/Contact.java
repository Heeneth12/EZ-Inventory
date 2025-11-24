package com.ezh.Inventory.contacts.entiry;

import com.ezh.Inventory.utils.common.CommonSerializable;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import lombok.*;

@Entity
@Table(name = "contact")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Contact extends CommonSerializable{

    private String contactCode;
    private String name;
    private String email;
    private String phone;
    private String gstNumber;
    @Enumerated(EnumType.STRING)
    private ContactType type;
    private Boolean active;
}

