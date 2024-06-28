package dev.danvega.service;

import dev.danvega.domain.Employee;
import dev.danvega.domain.Model.EmployeeModel;

public interface EmployeeService {
    public void saveEmployee(EmployeeModel employeeModel);

    public void employeeSave();

    public Iterable<Employee> list();
}
