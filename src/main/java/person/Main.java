package person;

import com.github.javafaker.Faker;
import org.jdbi.v3.core.Jdbi;
import org.jdbi.v3.sqlobject.SqlObjectPlugin;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;

public class Main {
    public static void main(String[] args) {
        Jdbi jdbi = Jdbi.create("jdbc:h2:mem:test");
        jdbi.installPlugin(new SqlObjectPlugin());

        List<Person> personList = jdbi.withExtension(PersonDao.class, dao -> {
            dao.createTable();
            for (int i = 1; i < Integer.parseInt(args[0]) + 1; i++) {
                dao.insert(createPerson(i));
            }
            Optional<Person> personWithID = dao.findById(1);
            System.out.println(personWithID);

            personWithID.ifPresent(dao::delete);

            return dao.listPerson();
        });
        personList.forEach(System.out::println);
    }

    static Person createPerson(int id){
        Faker faker = new Faker();
        LocalDate birthday = LocalDate.ofInstant(faker.date().birthday().toInstant(), ZoneId.systemDefault());
        return Person.builder()
                .id(id)
                .name(faker.name().fullName())
                .birthDate(birthday)
                .gender(faker.options().option(Person.Gender.MALE,Person.Gender.FEMALE))
                .email(faker.internet().emailAddress())
                .phone(faker.phoneNumber().phoneNumber())
                .profession(faker.company().profession())
                .married(faker.bool().bool())
                .build();
    }

}
