package com.alexvait.accountingapi.accounting.controller.annotated;

import com.alexvait.accountingapi.accounting.model.response.InvoiceResponseModel;
import com.alexvait.accountingapi.accounting.model.response.PositionResponseModel;
import com.alexvait.accountingapi.accounting.model.response.hateoas.InvoiceResponseModelPagedList;
import com.alexvait.accountingapi.security.config.AuthorityConstants;
import com.alexvait.accountingapi.security.config.SecurityConstants;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;

@Secured(SecurityConstants.ROLE_USER)
public interface InvoiceControllerAnnotated {
    @Operation(
            summary = "Get all invoices",
            tags = "Invoice",
            description = "Use this endpoint to get a list of all invoices for the authenticated user",
            security = @SecurityRequirement(name = SecurityConstants.AUTHORIZATION_HEADER)
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of invoice was returned", content = {@Content(mediaType = "application/json")}),
            @ApiResponse(responseCode = "403", description = "Access denied", content = {@Content(mediaType = "application/json")})}
    )
    @PreAuthorize("hasAuthority('" + AuthorityConstants.MULTIPLE_INVOICES_READ + "')")
    InvoiceResponseModelPagedList getInvoices(
            @Parameter(description = "Page number") int page,
            @Parameter(description = "Page size") int size);


    @Operation(
            summary = "Get invoice by number",
            tags = "Invoice",
            description = "Use this endpoint to get an invoice",
            security = @SecurityRequirement(name = SecurityConstants.AUTHORIZATION_HEADER)
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Invoice was returned", content = {@Content(mediaType = "application/json")}),
            @ApiResponse(responseCode = "403", description = "Access denied", content = {@Content(mediaType = "application/json")})}
    )
    @PreAuthorize("hasAuthority('" + AuthorityConstants.INVOICE_READ + "')")
    EntityModel<InvoiceResponseModel> getInvoice(@Parameter(description = "Invoice number") @PathVariable String invoiceNr);


    @Operation(
            summary = "Get positions billed with the invoice",
            tags = "Invoice",
            description = "Use this endpoint to get all positions billed with invoice with the provided number",
            security = @SecurityRequirement(name = SecurityConstants.AUTHORIZATION_HEADER)
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of positions was returned", content = {@Content(mediaType = "application/json")}),
            @ApiResponse(responseCode = "403", description = "Access denied", content = {@Content(mediaType = "application/json")}),
            @ApiResponse(responseCode = "404", description = "Invoice not found", content = {@Content(mediaType = "application/json")})}
    )
    @PreAuthorize("hasAuthority('" + AuthorityConstants.MULTIPLE_POSITIONS_READ + "')")
    CollectionModel<EntityModel<PositionResponseModel>> getBilledPositions(@Parameter(description = "Invoice number") @PathVariable String invoiceNr);


    @Operation(
            summary = "Create an number",
            tags = "Invoice",
            description = "Use this endpoint to create an invoice, including all open positions",
            security = @SecurityRequirement(name = SecurityConstants.AUTHORIZATION_HEADER)
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Invoice was returned", content = {@Content(mediaType = "application/json")}),
            @ApiResponse(responseCode = "403", description = "Access denied", content = {@Content(mediaType = "application/json")}),
            @ApiResponse(responseCode = "404", description = "No open positions found", content = {@Content(mediaType = "application/json")})}
    )
    @PreAuthorize("hasAuthority('" + AuthorityConstants.INVOICE_GENERATE + "')")
    EntityModel<InvoiceResponseModel> createInvoice();

}
