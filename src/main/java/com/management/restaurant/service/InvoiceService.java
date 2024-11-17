package com.management.restaurant.service;

import java.util.Optional;

import com.management.restaurant.domain.Invoice;
import com.management.restaurant.domain.response.ResultPaginationDTO;
import com.management.restaurant.repository.InvoiceRepository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@Service
public class InvoiceService {

    private final InvoiceRepository invoiceRepository;

    public InvoiceService(InvoiceRepository invoiceRepository) {
        this.invoiceRepository = invoiceRepository;
    }

    public Invoice createInvoice(Invoice invoice) {
        return this.invoiceRepository.save(invoice);
    }

    public Invoice updateInvoice(Invoice invoice) {
        Invoice currentInvoice = this.fetchInvoiceById(invoice.getId());
        if (currentInvoice == null) {
            return null;
        }

        currentInvoice.setTotalAmount(invoice.getTotalAmount());
        currentInvoice.setCustomerPaid(invoice.getCustomerPaid());
        currentInvoice.setReturnAmount(invoice.getReturnAmount());
        currentInvoice.setMethod(invoice.getMethod());
        currentInvoice.setStatus(invoice.getStatus());

        return this.invoiceRepository.save(currentInvoice);
    }

    public void deleteInvoiceById(Long id) {
        this.invoiceRepository.deleteById(id);
    }

    public Invoice fetchInvoiceById(Long id) {
        Optional<Invoice> invoice = this.invoiceRepository.findById(id);
        return invoice.orElse(null);
    }

    public ResultPaginationDTO fetchInvoices(Specification<Invoice> spec, Pageable pageable) {
        Page<Invoice> pageInvoice = this.invoiceRepository.findAll(spec, pageable);
        ResultPaginationDTO rs = new ResultPaginationDTO();
        ResultPaginationDTO.Meta meta = new ResultPaginationDTO.Meta();

        meta.setPage(pageable.getPageNumber() + 1);
        meta.setPageSize(pageable.getPageSize());

        meta.setPages(pageInvoice.getTotalPages());
        meta.setTotal(pageInvoice.getTotalElements());

        rs.setMeta(meta);
        rs.setResult(pageInvoice.getContent());

        return rs;
    }
}
