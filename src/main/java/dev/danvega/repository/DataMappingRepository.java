package dev.danvega.repository;

import dev.danvega.domain.DataMapping;
import dev.danvega.domain.Department;
import org.springframework.data.repository.CrudRepository;

public interface DataMappingRepository extends CrudRepository<DataMapping, Long> {

}
