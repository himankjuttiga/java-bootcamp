import java.util.List;

public class NamesDemo {
    public static void main(String[] args) {
        List<Employee> employees = EmployeeData.sample();

        // TODO: stream pipeline — map each Employee to its name, collect to List<String>
        List<String> names = employees.stream()
               .map(Employee::name)
               .toList();

        System.out.println("Employee names:");
        names.forEach(System.out::println);
    }
}