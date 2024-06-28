package dev.danvega.service;

import dev.danvega.domain.Department;
import dev.danvega.domain.Model.RequestModel;

public interface DepartmentService {

    public void saveDepartment(RequestModel departmentModel);

    public void saveJsonData();

    public Iterable<Department> list();

//    void saveJsonDat1();


}
