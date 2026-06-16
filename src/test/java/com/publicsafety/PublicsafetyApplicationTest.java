package com.publicsafety;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PublicsafetyApplicationTest {

    @Mock
    private PublicsafetyRepository publicsafetyRepository;

    @Spy
    @InjectMocks
    private PublicsafetyApplication application;

    @BeforeEach
    void setUp() {
        application.setPublicsafetyRepository(publicsafetyRepository);
    }

    @Test
    void parseIntegerField_whenValueIsNull_returnsZero() {
        int result = application.parseIntegerField(null, 5);

        assertThat(result).isZero();
    }

    @Test
    void parseIntegerField_whenValueIsBlank_returnsZero() {
        int result = application.parseIntegerField("   ", 10);

        assertThat(result).isZero();
    }

    @Test
    void parseIntegerField_whenNumberIsInvalid_returnsZero() {
        int result = application.parseIntegerField("abc123", 7);

        assertThat(result).isZero();
    }

    @Test
    void parseIntegerField_whenValueIsNumeric_returnsParsedValue() {
        int result = application.parseIntegerField(" 42 ", 12);

        assertThat(result).isEqualTo(42);
    }

    @Test
    void parsePublicsafetyCsv_readsValidRowsAndSkipsBadRows() throws Exception {
        String csv = "police_officer,surveillance_operator,citizen,incident_date,camera_locations,incident_type,request_status,police_experience_years,observation_center_address,observation_center_phone\n"
                + "Петренко,Олена,Іван,2024-10-01,Центр-1,крадіжка,закрито,5,вул. Безпеки,380441230101\n"
                + "Неповний,рядок\n"
                + "Сидоренко,Ірина,Павло,2024-10-02,Центр-2,ДТП,прийнято,invalid,вул. Щаслива,380441230202\n";

        InputStream stream = new ByteArrayInputStream(csv.getBytes(StandardCharsets.UTF_8));
        List<Publicsafety> result = application.parsePublicsafetyCsv(stream);

        assertThat(result).hasSize(2);
        assertThat(result.get(0).getPoliceOfficer()).isEqualTo("Петренко");
        assertThat(result.get(0).getPoliceExperienceYears()).isEqualTo(5);
        assertThat(result.get(1).getRequestStatus()).isEqualTo("прийнято");
        assertThat(result.get(1).getPoliceExperienceYears()).isZero();
    }

    @Test
    void runCommand_choice4_returnsFalse() {
        boolean result = application.runCommand(4);

        assertThat(result).isFalse();
    }

    @Test
    void runCommand_invalidChoice_returnsTrue() {
        boolean result = application.runCommand(99);

        assertThat(result).isTrue();
    }

    @Test
    void addPublicsafetyFromCsv_savesActualCsvRecords() {
        application.addPublicsafetyFromCsv();

        @SuppressWarnings("unchecked")
        ArgumentCaptor<List<Publicsafety>> captor = ArgumentCaptor.forClass((Class) List.class);
        verify(publicsafetyRepository).saveAll(captor.capture());

        List<Publicsafety> savedList = captor.getValue();
        assertThat(savedList).isNotEmpty();
        assertThat(savedList.get(0).getPoliceOfficer()).isNotBlank();
    }

    @Test
    void addPublicsafetyFromCsv_whenResourceMissing_doesNotSave() {
        doReturn(null).when(application).getCsvInputStream();

        application.addPublicsafetyFromCsv();

        verifyNoInteractions(publicsafetyRepository);
    }

    @Test
    void runCommand_choice1_savesCsvRecords() {
        InputStream stream = new ByteArrayInputStream(
                ("police_officer,surveillance_operator,citizen,incident_date,camera_locations,incident_type,request_status,police_experience_years,observation_center_address,observation_center_phone\n"
                        + "Петренко,Олена,Іван,2024-10-01,Центр-1,крадіжка,закрито,5,вул. Безпеки,380441230101\n").getBytes(StandardCharsets.UTF_8));
        doReturn(stream).when(application).getCsvInputStream();

        application.runCommand(1);

        @SuppressWarnings("unchecked")
        ArgumentCaptor<List<Publicsafety>> captor = ArgumentCaptor.forClass((Class) List.class);
        verify(publicsafetyRepository).saveAll(captor.capture());

        List<Publicsafety> savedList = captor.getValue();
        assertThat(savedList).hasSize(1);
        assertThat(savedList.get(0).getPoliceOfficer()).isEqualTo("Петренко");
    }

    @Test
    void viewAllPublicsafety_emptyList_callsFindAll() {
        when(publicsafetyRepository.findAll()).thenReturn(Collections.emptyList());

        application.viewAllPublicsafety();

        verify(publicsafetyRepository).findAll();
    }

    @Test
    void viewAllPublicsafety_nonEmptyList_callsFindAll() {
        when(publicsafetyRepository.findAll()).thenReturn(
                Arrays.asList(new Publicsafety("Петренko", "Олена", "Іван", "2024-10-01", "Центр-1", "крадіжка", "закрито", 5, "вул. Безпеки", "380441230101"))
        );

        application.viewAllPublicsafety();

        verify(publicsafetyRepository).findAll();
    }

    @Test
    void run_withExitChoice_returnsImmediately() throws Exception {
        InputStream originalIn = System.in;
        try {
            System.setIn(new ByteArrayInputStream("4\n".getBytes(StandardCharsets.UTF_8)));
            application.run();
        } finally {
            System.setIn(originalIn);
        }
    }

    @Test
    void run_withViewChoice_callsFindAllAndExits() throws Exception {
        when(publicsafetyRepository.findAll()).thenReturn(Collections.emptyList());
        InputStream originalIn = System.in;
        try {
            System.setIn(new ByteArrayInputStream("2\n4\n".getBytes(StandardCharsets.UTF_8)));
            application.run();

            verify(publicsafetyRepository).findAll();
        } finally {
            System.setIn(originalIn);
        }
    }

    @Test
    void run_withAddChoice_readsCsvAndSaves() throws Exception {
        InputStream originalIn = System.in;
        try {
            doReturn(new ByteArrayInputStream(
                    ("police_officer,surveillance_operator,citizen,incident_date,camera_locations,incident_type,request_status,police_experience_years,observation_center_address,observation_center_phone\n"
                            + "Петренко,Олена,Іван,2024-10-01,Центр-1,крадіжка,закрито,5,вул. Безпеки,380441230101\n").getBytes(StandardCharsets.UTF_8)))
                    .when(application).getCsvInputStream();

            System.setIn(new ByteArrayInputStream("1\n4\n".getBytes(StandardCharsets.UTF_8)));
            application.run();

            @SuppressWarnings("unchecked")
            ArgumentCaptor<List<Publicsafety>> captor = ArgumentCaptor.forClass((Class) List.class);
            verify(publicsafetyRepository).saveAll(captor.capture());

            List<Publicsafety> savedList = captor.getValue();
            assertThat(savedList).hasSize(1);
            assertThat(savedList.get(0).getPoliceOfficer()).isEqualTo("Петренко");
            assertThat(savedList.get(0).getIncidentType()).isEqualTo("крадіжка");
        } finally {
            System.setIn(originalIn);
        }
    }

    @Test
    void run_withInvalidChoice_showsMessageAndContinues() throws Exception {
        InputStream originalIn = System.in;
        try {
            System.setIn(new ByteArrayInputStream("99\n4\n".getBytes(StandardCharsets.UTF_8)));
            application.run();
        } finally {
            System.setIn(originalIn);
        }
    }

    @Test
    void run_withDeleteChoice_callsDeleteAll() throws Exception {
        InputStream originalIn = System.in;
        try {
            System.setIn(new ByteArrayInputStream("3\n4\n".getBytes(StandardCharsets.UTF_8)));
            application.run();

            verify(publicsafetyRepository).deleteAll();
        } finally {
            System.setIn(originalIn);
        }
    }

    @Test
    void publicSafetyGetters_returnExpectedValues() {
        Publicsafety item = new Publicsafety(
                "Петренко",
                "Олена",
                "Іван",
                "2024-10-01",
                "Центр-1",
                "крадіжка",
                "закрито",
                5,
                "вул. Безпеки",
                "380441230101"
        );

        assertThat(item.getPoliceOfficer()).isEqualTo("Петренко");
        assertThat(item.getSurveillanceOperator()).isEqualTo("Олена");
        assertThat(item.getCitizen()).isEqualTo("Іван");
        assertThat(item.getIncidentDate()).isEqualTo("2024-10-01");
        assertThat(item.getCameraLocations()).isEqualTo("Центр-1");
        assertThat(item.getIncidentType()).isEqualTo("крадіжка");
        assertThat(item.getRequestStatus()).isEqualTo("закрито");
        assertThat(item.getPoliceExperienceYears()).isEqualTo(5);
        assertThat(item.getObservationCenterAddress()).isEqualTo("вул. Безпеки");
        assertThat(item.getObservationCenterPhone()).isEqualTo("380441230101");
    }

    @Test
    void runCommand_choice2_callsFindAll() {
        when(publicsafetyRepository.findAll()).thenReturn(Collections.emptyList());

        boolean result = application.runCommand(2);

        assertThat(result).isTrue();
        verify(publicsafetyRepository).findAll();
    }

    @Test
    void runCommand_choice3_callsDeleteAll() {
        boolean result = application.runCommand(3);

        assertThat(result).isTrue();
        verify(publicsafetyRepository).deleteAll();
    }

    @Test
    void getCsvInputStream_returnsResourceStream() {
        assertThat(application.getCsvInputStream()).isNotNull();
    }

    @Test
    void publicSafetyToString_containsFieldValues() {
        Publicsafety item = new Publicsafety(
                "Петренко",
                "Олена",
                "Іван",
                "2024-10-01",
                "Центр-1",
                "крадіжка",
                "закрито",
                5,
                "вул. Безпеки",
                "380441230101"
        );

        String text = item.toString();

        assertThat(text).contains("Петренко");
        assertThat(text).contains("крадіжка");
        assertThat(text).contains("380441230101");
    }
}
