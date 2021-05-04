package com.alexvait.accountingapi.security.config;

public class AuthorityConstants {
    public static final String MULTIPLE_INVOICES_READ = "multiple.invoices.read";
    public static final String INVOICE_READ = "invoice.read";
    public static final String INVOICE_GENERATE = "invoice.generate";

    public static final String MULTIPLE_POSITIONS_READ = "multiple.positions.read";
    public static final String POSITION_READ = "position.read";
    public static final String POSITION_CREATE = "position.create";

    public static final String LIST_PAYMENTS = "list.payments";

    public static final String MULTIPLE_USERS_READ = "multiple.users.create";
    public static final String USER_CREATE = "user.create";
    public static final String USER_READ = "user.read";
    public static final String USER_UPDATE = "user.update";
    public static final String USER_DELETE = "user.delete";
}
