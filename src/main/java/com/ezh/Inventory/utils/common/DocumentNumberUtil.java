package com.ezh.Inventory.utils.common;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public final class DocumentNumberUtil {

    private static final DateTimeFormatter DATE_FORMAT =
            DateTimeFormatter.ofPattern("yyMMdd");
    private DocumentNumberUtil() {}

    /**
     * FORMAT:
     * PREFIX-YYMMDD-XXXX
     */
    public static String generate(DocPrefix prefix) {

        String datePart = LocalDate.now().format(DATE_FORMAT);

        String randomPart = RandomCodeUtil.randomCode(4);

        return prefix.name() + "-" + datePart + "-" + randomPart;
    }
}
