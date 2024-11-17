package com.management.restaurant.controller;

import com.management.restaurant.domain.response.invoice.ResCreateInvoiceDTO;
import com.management.restaurant.domain.response.invoice.ResInvoiceDTO;
import com.management.restaurant.domain.response.invoice.ResUpdateInvoiceDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import com.turkraft.springfilter.boot.Filter;
import com.management.restaurant.service.InvoiceService;

import com.management.restaurant.domain.Invoice;
import com.management.restaurant.domain.response.ResultPaginationDTO;

import com.management.restaurant.util.annotation.ApiMessage;
import com.management.restaurant.util.error.InfoInvalidException;

/**
 * REST controller for managing invoices.
 * This class accesses the {@link Invoice} entity
 */
@RestController
@RequestMapping("/api/v1")
public class InvoiceController {

    private final Logger log = LoggerFactory.getLogger(InvoiceController.class);

    private final InvoiceService invoiceService;

    public InvoiceController(InvoiceService invoiceService) {
        this.invoiceService = invoiceService;
    }

    /**
     * {@code POST  /invoices} : Create a new invoice.
     *
     * @param invoice the invoice to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new invoices
     * or with status {@code 400 (Bad Request)}.
     */
    @PostMapping("/invoices")
    @ApiMessage("Create a invoice")
    public ResponseEntity<ResCreateInvoiceDTO> createInvoice(@RequestBody Invoice invoice) {
        log.debug("REST request to save invoice : {}", invoice);

        Invoice dataInvoice = this.invoiceService.createInvoice(invoice);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(this.invoiceService.convertToResCreateInvoiceDTO(dataInvoice));
    }

    /**
     * {@code PUT  /invoices/:id} : Update an existing invoice.
     *
     * @param invoice the invoice to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the updated invoice in the body
     * or with status {@code 400 (Bad Request)}.
     * @throws InfoInvalidException if the invoice does not exist
     */
    @PutMapping("/invoices")
    @ApiMessage("Update a invoice")
    public ResponseEntity<ResUpdateInvoiceDTO> updateInvoice(@RequestBody Invoice invoice)
    throws InfoInvalidException {
        log.debug("REST request to update invoice : {}", invoice);

        Invoice dataInvoice = this.invoiceService.updateInvoice(invoice);
        if (dataInvoice == null) {
            throw new InfoInvalidException("Hóa đơn không tồn tại");
        }

        return ResponseEntity
                .ok(this.invoiceService.convertToResUpdateInvoiceDTO(dataInvoice));
    }

    /**
     * {@code DELETE  /invoices/:id} : delete the "id" invoice.
     *
     * @param id the id of the invoices to delete.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)}
     * or with status {@code 400 (Bad Request)}.
     */
    @DeleteMapping("/invoices/{id}")
    @ApiMessage("Delete a invoice by id")
    public ResponseEntity<Void> deleteInvoice(@PathVariable Long id) {
        log.debug("REST request to delete invoice by id : {}", id);

        this.invoiceService.deleteInvoiceById(id);
        return ResponseEntity.ok(null);
    }

    /**
     * {@code GET  /invoices/:id} : get the "id" invoice.
     *
     * @param id the id of the invoices to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the invoices,
     * or with status {@code 400 (Bad Request)}.
     */
    @GetMapping("/invoices/{id}")
    @ApiMessage("Get a invoice by id")
    public ResponseEntity<ResInvoiceDTO> getInvoiceById(@PathVariable Long id)
            throws InfoInvalidException {
        log.debug("REST request to get invoice : {}", id);

        Invoice dataInvoice = this.invoiceService.fetchInvoiceById(id);
        if (dataInvoice == null) {
            throw new InfoInvalidException("Hóa đơn không tồn tại!");
        }

        return ResponseEntity
                .ok(this.invoiceService.convertToResInvoiceDTO(dataInvoice));
    }

    /**
     * {@code GET  /invoices} : Fetch filter invoices.
     *
     * @param pageable the pagination information.
     * @param spec the filtering criteria to apply to the invoice list.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of invoices in the body
     * or with status {@code 400 (Bad Request)}.
     */
    @GetMapping("/invoices")
    @ApiMessage("Get filter invoices")
    public ResponseEntity<ResultPaginationDTO> getInvoices(Pageable pageable,@Filter Specification<Invoice> spec) {
        log.debug("REST request to get invoice filter");
        return ResponseEntity.ok(this.invoiceService.fetchInvoicesDTO(spec, pageable));
    }
}
