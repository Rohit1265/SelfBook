package dev.danvega.service;

import dev.danvega.domain.Department;
import dev.danvega.domain.Model.DepartmentModel;

public interface DepartmentService {

    public void saveDepartment(DepartmentModel departmentModel);

    public void departmentSave();

    public Iterable<Department> list();


}
