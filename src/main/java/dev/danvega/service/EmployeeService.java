package dev.danvega.service;

import dev.danvega.domain.Employee;
import dev.danvega.domain.Model.RequestModel;

public interface EmployeeService {
    public void saveEmployee(RequestModel employeeModel);

    public void employeeSave();

    public Iterable<Employee> list();
}
