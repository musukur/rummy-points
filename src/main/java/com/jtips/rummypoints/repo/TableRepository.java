package com.jtips.rummypoints.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.jtips.rummypoints.model.Table;

public interface TableRepository extends JpaRepository<Table, Integer> {

	List<Table> findByIsCompleted(boolean isCompleted);
}
