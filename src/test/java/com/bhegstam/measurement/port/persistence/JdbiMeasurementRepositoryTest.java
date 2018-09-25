package com.bhegstam.measurement.port.persistence;

import com.bhegstam.measurement.db.PaginationInformation;
import com.bhegstam.measurement.db.PaginationSettings;
import com.bhegstam.measurement.db.QueryResult;
import com.bhegstam.measurement.domain.Measurement;
import com.bhegstam.measurement.domain.MeasurementRepository;
import com.bhegstam.measurement.domain.MeasurementSource;
import com.bhegstam.measurement.util.TestDatabaseSetup;
import org.jdbi.v3.core.Jdbi;
import org.junit.Rule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import javax.inject.Inject;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static com.bhegstam.measurement.util.Matchers.isNotPresent;
import static com.bhegstam.measurement.util.Matchers.isPresentAnd;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertAll;

@ExtendWith(TestDatabaseSetup.class)
public class JdbiMeasurementRepositoryTest {
    private static final String SOURCE_1 = "source1";
    private static final String SOURCE_2 = "source2";
    private static final String TYPE = "TYPE";
    private static final String UNIT = "UNIT";

    @Inject
    private Jdbi jdbi;
    private MeasurementRepository measurementRepository;

    @Rule
    public TestDatabaseSetup testDatabaseSetup = new TestDatabaseSetup();

    @BeforeEach
    void setUp() {
        measurementRepository = jdbi.onDemand(JdbiMeasurementRepository.class);
    }

    @Test
    void getSources_emptyDb() {
        // when
        List<MeasurementSource> sources = measurementRepository.getSources();

        // then
        assertThat(sources.size(), is(0));
    }

    @Test
    void getSources_singleSourceInDb() {
        // given
        insertMeasurements(SOURCE_1, 1);

        // when
        List<MeasurementSource> sources = measurementRepository.getSources();

        // then
        assertThat(sources.size(), is(1));
        assertThat(sources.get(0).getName(), is(SOURCE_1));
    }

    @Test
    void getSources_twoSourcesInDb() {
        // given
        insertMeasurements(SOURCE_1, 1);
        insertMeasurements(SOURCE_2, 1);

        // when
        List<MeasurementSource> sources = measurementRepository.getSources();

        // then
        assertThat(sources.size(), is(2));
        assertThat(sources.get(0).getName(), is(SOURCE_1));
        assertThat(sources.get(1).getName(), is(SOURCE_2));
    }

    @Test
    void getMeasurements_emptyDb() {
        // when
        QueryResult<Measurement> queryResult = measurementRepository.getMeasurements(SOURCE_1, new PaginationSettings(1, 1));

        // then
        assertAll(
                () -> assertThat("Data count", queryResult.getData().size(), is(0)),
                () -> {
                    PaginationInformation paginationInformation = queryResult
                            .getPaginationInformation()
                            .get();
                    assertAll(
                            () -> assertThat("Total item count", paginationInformation.getTotalItemCount(), is(0)),
                            () -> assertThat("Page count", paginationInformation.getPageCount(), is(1)),
                            () -> assertThat("Per page", paginationInformation.getPerPage(), is(1)),
                            () -> assertThat("Prev page", paginationInformation.getPrevPage(), isNotPresent()),
                            () -> assertThat("Next page", paginationInformation.getNextPage(), isNotPresent()),
                            () -> assertThat("Offset", paginationInformation.getOffset(), is(0))
                    );
                }
        );
    }

    @Test
    void addAndGetMeasurement() {
        // given
        Instant createdAt = Instant.now();
        measurementRepository.addMeasurement(SOURCE_1, createdAt, TYPE, 17, UNIT);
        measurementRepository.addMeasurement(SOURCE_2, createdAt, TYPE, 18, UNIT);

        // when
        QueryResult<Measurement> queryResult = measurementRepository.getMeasurements(SOURCE_1, new PaginationSettings(1, 1));

        // then
        assertAll(
                () -> {
                    assertThat("Data count", queryResult.getData().size(), is(1));
                    Measurement measurement = queryResult.getData().get(0);
                    assertAll(
                            () -> assertThat("source", measurement.getSource(), is(SOURCE_1)),
                            () -> assertThat("createdAt", measurement.getCreatedAt(), is(createdAt.truncatedTo(ChronoUnit.MILLIS))),
                            () -> assertThat("type", measurement.getType(), is(TYPE)),
                            () -> assertThat("value", measurement.getValue(), is(17.0)),
                            () -> assertThat("unit", measurement.getUnit(), is(UNIT))
                    );
                },
                () -> {
                    PaginationInformation paginationInformation = queryResult
                            .getPaginationInformation()
                            .get();
                    assertAll(
                            () -> assertThat("Total item count", paginationInformation.getTotalItemCount(), is(1)),
                            () -> assertThat("Page count", paginationInformation.getPageCount(), is(1)),
                            () -> assertThat("Per page", paginationInformation.getPerPage(), is(1)),
                            () -> assertThat("Prev page", paginationInformation.getPrevPage(), isNotPresent()),
                            () -> assertThat("Next page", paginationInformation.getNextPage(), isNotPresent()),
                            () -> assertThat("Offset", paginationInformation.getOffset(), is(0))
                    );
                }
        );
    }

    @Test
    void getMeasurements_firstPage_hasNextPage() {
        // given
        insertMeasurements(SOURCE_1, 40);
        insertMeasurements(SOURCE_2, 40);

        // when
        QueryResult<Measurement> queryResult = measurementRepository.getMeasurements(SOURCE_1, new PaginationSettings(30, 1));

        // then
        assertAll(
                () -> assertThat("Data count", queryResult.getData().size(), is(30)),
                () -> {
                    PaginationInformation paginationInformation = queryResult
                            .getPaginationInformation()
                            .get();
                    assertAll(
                            () -> assertThat("Total item count", paginationInformation.getTotalItemCount(), is(40)),
                            () -> assertThat("Page count", paginationInformation.getPageCount(), is(2)),
                            () -> assertThat("Per page", paginationInformation.getPerPage(), is(30)),
                            () -> assertThat("Prev page", paginationInformation.getPrevPage(), isNotPresent()),
                            () -> assertThat("Next page", paginationInformation.getNextPage(), isPresentAnd(is(2))),
                            () -> assertThat("Offset", paginationInformation.getOffset(), is(0))
                    );
                }
        );
    }

    @Test
    void getMeasurements_secondPage_doesNotHaveNextPage() {
        // given
        insertMeasurements(SOURCE_1, 40);
        insertMeasurements(SOURCE_2, 40);

        // when
        QueryResult<Measurement> queryResult = measurementRepository.getMeasurements(SOURCE_1, new PaginationSettings(30, 2));

        // then
        assertAll(
                () -> assertThat("Data count", queryResult.getData().size(), is(10)),
                () -> {
                    PaginationInformation paginationInformation = queryResult
                            .getPaginationInformation()
                            .get();
                    assertAll(
                            () -> assertThat("Total item count", paginationInformation.getTotalItemCount(), is(40)),
                            () -> assertThat("Page count", paginationInformation.getPageCount(), is(2)),
                            () -> assertThat("Per page", paginationInformation.getPerPage(), is(30)),
                            () -> assertThat("Prev page", paginationInformation.getPrevPage(), isPresentAnd(is(1))),
                            () -> assertThat("Next page", paginationInformation.getNextPage(), isNotPresent()),
                            () -> assertThat("Offset", paginationInformation.getOffset(), is(30))
                    );
                }
        );
    }

    private void insertMeasurements(String sourceId, int count) {
        for (int i = 0; i < count; i++) {
            measurementRepository.addMeasurement(sourceId, Instant.now(), TYPE, i, UNIT);
        }
    }
}