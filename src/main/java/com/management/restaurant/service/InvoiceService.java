package com.management.restaurant.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.management.restaurant.domain.Invoice;
import com.management.restaurant.domain.Invoice;
import com.management.restaurant.domain.response.ResultPaginationDTO;
import com.management.restaurant.domain.response.invoice.ResCreateInvoiceDTO;
import com.management.restaurant.domain.response.invoice.ResInvoiceDTO;
import com.management.restaurant.domain.response.invoice.ResUpdateInvoiceDTO;
import com.management.restaurant.repository.InvoiceRepository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

/**
 * Service class for managing dining table.
 */
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

    /**
     * Convert a {@link Invoice} entity to a {@link ResInvoiceDTO}.
     *
     * @param invoice the invoice entity to convert.
     * @return a {@link ResInvoiceDTO} containing the invoice details.
     */
    public ResInvoiceDTO convertToResInvoiceDTO(Invoice invoice) {
        // response invoice DTO
        ResInvoiceDTO res = new ResInvoiceDTO();
        res.setId(invoice.getId());
        res.setTotalAmount(invoice.getTotalAmount());
        res.setCustomerAmount(invoice.getCustomerPaid());
        res.setReturnAmount(invoice.getReturnAmount());
        res.setMethod(invoice.getMethod());
        res.setStatus(invoice.getStatus());

        res.setCreatedBy(invoice.getCreatedBy());
        res.setCreatedDate(invoice.getCreatedDate());
        res.setLastModifiedBy(invoice.getLastModifiedBy());
        res.setLastModifiedDate(invoice.getLastModifiedDate());

        // response user - invoice DTO
        ResInvoiceDTO.UserInvoice userInvoice = new ResInvoiceDTO.UserInvoice();
        if (invoice.getUser() != null) {
            userInvoice.setId(invoice.getUser().getId());
            userInvoice.setName(invoice.getUser().getName());
            res.setUser(userInvoice);
        }

        // response order - invoice DTO
        ResInvoiceDTO.OrderInvoice orderInvoice = new ResInvoiceDTO.OrderInvoice();
        if (invoice.getOrder() != null) {
            orderInvoice.setId(invoice.getOrder().getId());
            orderInvoice.setTableName(invoice.getOrder().getDiningTable().getName());
            res.setOrder(orderInvoice);
        }

        return res;
    }

    /**
     * Convert a {@link Invoice} entity to a {@link ResCreateInvoiceDTO} for invoice creation responses.
     *
     * @param invoice the invoice entity to convert.
     * @return a {@link ResCreateInvoiceDTO} containing the invoice details for creation.
     */
    public ResCreateInvoiceDTO convertToResCreateInvoiceDTO(Invoice invoice) {
        // create response invoice DTO
        ResCreateInvoiceDTO res = new ResCreateInvoiceDTO();
        res.setId(invoice.getId());
        res.setTotalAmount(invoice.getTotalAmount());
        res.setCustomerAmount(invoice.getCustomerPaid());
        res.setReturnAmount(invoice.getReturnAmount());
        res.setMethod(invoice.getMethod());
        res.setStatus(invoice.getStatus());

        res.setCreatedBy(invoice.getCreatedBy());
        res.setCreatedDate(invoice.getCreatedDate());

        // create response user - invoice DTO
        ResCreateInvoiceDTO.UserInvoice userInvoice = new ResCreateInvoiceDTO.UserInvoice();
        if (invoice.getUser() != null) {
            userInvoice.setId(invoice.getUser().getId());
            res.setUser(userInvoice);
        }

        // create response order - invoice DTO
        ResCreateInvoiceDTO.OrderInvoice orderInvoice = new ResCreateInvoiceDTO.OrderInvoice();
        if (invoice.getOrder() != null) {
            orderInvoice.setId(invoice.getOrder().getId());
            res.setOrder(orderInvoice);
        }

        return res;
    }

    /**
     * Convert a {@link Invoice} entity to a {@link ResUpdateInvoiceDTO} for invoice update responses.
     *
     * @param invoice the invoice entity to convert.
     * @return a {@link ResUpdateInvoiceDTO} containing the invoice details for updates.
     */
    public ResUpdateInvoiceDTO convertToResUpdateInvoiceDTO(Invoice invoice) {
        // update response invoice DTO
        ResUpdateInvoiceDTO res = new ResUpdateInvoiceDTO();
        res.setId(invoice.getId());
        res.setTotalAmount(invoice.getTotalAmount());
        res.setCustomerAmount(invoice.getCustomerPaid());
        res.setReturnAmount(invoice.getReturnAmount());
        res.setMethod(invoice.getMethod());
        res.setStatus(invoice.getStatus());

        res.setLastModifiedBy(invoice.getLastModifiedBy());
        res.setLastModifiedDate(invoice.getLastModifiedDate());

        // update response user - invoice DTO
        ResUpdateInvoiceDTO.UserInvoice userInvoice = new ResUpdateInvoiceDTO.UserInvoice();
        if (invoice.getUser() != null) {
            userInvoice.setId(invoice.getUser().getId());
            userInvoice.setName(invoice.getUser().getName());
            res.setUser(userInvoice);
        }

        // update response order - invoice DTO
        ResUpdateInvoiceDTO.OrderInvoice orderInvoice = new ResUpdateInvoiceDTO.OrderInvoice();
        if (invoice.getOrder() != null) {
            orderInvoice.setId(invoice.getOrder().getId());
            orderInvoice.setTableName(invoice.getOrder().getDiningTable().getName());
            res.setOrder(orderInvoice);
        }

        return res;
    }

    /**
     * Fetch a paginated list of invoices based on the given search criteria and pagination information.
     *
     * @param spec the filtering criteria to apply to the invoice list.
     * @param pageable the pagination information.
     * @return a {@link ResultPaginationDTO} containing the list of invoices and pagination metadata.
     */
    public ResultPaginationDTO fetchInvoicesDTO(Specification<Invoice> spec, Pageable pageable) {
        Page<Invoice> page = this.invoiceRepository.findAll(spec, pageable);
        ResultPaginationDTO rs = new ResultPaginationDTO();
        ResultPaginationDTO.Meta meta = new ResultPaginationDTO.Meta();

        meta.setPage(pageable.getPageNumber() + 1);
        meta.setPageSize(pageable.getPageSize());

        meta.setPages(page.getTotalPages());
        meta.setTotal(page.getTotalElements());

        rs.setMeta(meta);

        // remove sensitive data
        List<ResInvoiceDTO> listInvoice = page.getContent()
                .stream().map(this::convertToResInvoiceDTO)
                .collect(Collectors.toList());

        rs.setResult(listInvoice);

        return rs;
    }
}
