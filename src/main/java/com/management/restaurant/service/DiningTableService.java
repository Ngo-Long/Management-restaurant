package com.management.restaurant.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import com.management.restaurant.domain.DiningTable;
import com.management.restaurant.domain.response.ResultPaginationDTO;

import com.management.restaurant.repository.DiningTableRepository;

/**
 * Service class for managing dining table.
 */
@Service
public class DiningTableService {

    private final DiningTableRepository diningTableRepository;

    public DiningTableService(DiningTableRepository diningTableRepository) {
        this.diningTableRepository = diningTableRepository;
    }

    public DiningTable createDiningTable(DiningTable diningTable) {
        return diningTableRepository.save(diningTable);
    }

    public DiningTable updateDiningTable(DiningTable diningTable) {
        DiningTable diningTableDB = this.fetchDiningTableById(diningTable.getId());
        if (diningTableDB == null) {
            return null;
        }

        diningTableDB.setName(diningTable.getName());
        diningTableDB.setLocation(diningTable.getLocation());
        diningTableDB.setSeats(diningTable.getSeats());
        diningTableDB.setDescription(diningTable.getDescription());
        diningTableDB.setStatus(diningTable.getStatus());
        diningTableDB.setActive(diningTable.isActive());

        return diningTableRepository.save(diningTableDB);
    }

    public void deleteDiningTableById(Long id) {
        this.diningTableRepository.deleteById(id);
    }

    public DiningTable fetchDiningTableById(Long id) {
        Optional<DiningTable> diningTable = this.diningTableRepository.findById(id);
        return diningTable.orElse(null);
    }

    public ResultPaginationDTO fetchAllDiningTables(Specification<DiningTable> spec, Pageable pageable) {
        Page<DiningTable> pageDiningTable = this.diningTableRepository.findAll(spec, pageable);
        ResultPaginationDTO rs = new ResultPaginationDTO();
        ResultPaginationDTO.Meta meta = new ResultPaginationDTO.Meta();

        meta.setPage(pageable.getPageNumber() + 1);
        meta.setPageSize(pageable.getPageSize());

        meta.setPages(pageDiningTable.getTotalPages());
        meta.setTotal(pageDiningTable.getTotalElements());

        rs.setMeta(meta);
        rs.setResult(pageDiningTable.getContent());

        return rs;
    }

    public List<DiningTable> fetchDiningTableByName(String name) {
        return this.diningTableRepository.findByName(name);
    }

    public List<DiningTable> fetchDiningTableByLocation(String location) {
        return this.diningTableRepository.findByLocation(location);
    }

    public Boolean isNameExist(String name) {
        return this.diningTableRepository.existsByName(name);
    }

}
