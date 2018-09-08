package com.bhegstam.measurement.port.persistence;

import com.bhegstam.measurement.db.PaginationInformation;
import com.bhegstam.measurement.db.PaginationSettings;
import com.bhegstam.measurement.db.QueryResult;
import com.bhegstam.measurement.domain.Measurement;
import com.bhegstam.measurement.domain.MeasurementSource;
import com.bhegstam.measurement.domain.MeasurementRepository;
import org.jdbi.v3.sqlobject.config.RegisterRowMapper;
import org.jdbi.v3.sqlobject.customizer.Bind;
import org.jdbi.v3.sqlobject.statement.SqlQuery;
import org.jdbi.v3.sqlobject.statement.SqlUpdate;
import org.jdbi.v3.sqlobject.transaction.Transaction;

import java.sql.Timestamp;
import java.util.List;

@RegisterRowMapper(MeasurementSourceMapper.class)
@RegisterRowMapper(MeasurementMapper.class)
public interface JdbiMeasurementRepository extends MeasurementRepository {
    @SqlQuery("select distinct source from measurement order by source")
    List<MeasurementSource> getSources();

    default void addMeasurement(String sourceId, long createdAtMillis, String type, double value, String unit) {
        insertMeasurement(sourceId, new Timestamp(createdAtMillis), type, value, unit);
    }

    @SqlUpdate("insert into measurement(source, timestamp, type, value, unit) values (:source, :timestamp, :type, :value, :unit)")
    void insertMeasurement(@Bind("source") String sourceId, @Bind("timestamp") Timestamp timestamp, @Bind("type") String type, @Bind("value") double value, @Bind("unit") String unit);

    @Transaction
    default QueryResult<Measurement> getMeasurements(String sourceId, PaginationSettings paginationSettings){
        int totalCount = getTotalMeasurementCount(sourceId);
        PaginationInformation paginationInformation = PaginationInformation.calculate(totalCount, paginationSettings);

        List<Measurement> measurements = selectMeasurements(sourceId, paginationInformation.getPerPage(), paginationInformation.getOffset());

        return new QueryResult<>(measurements, paginationInformation);
    }

    @SqlQuery("select count(id) from measurement where source = :source")
    int getTotalMeasurementCount(@Bind("source") String source);

    @SqlQuery("select * from measurement where source = :source order by timestamp limit :perPage offset :offset")
    List<Measurement> selectMeasurements(@Bind("source") String source, @Bind("perPage") int perPage, @Bind("offset") int offset);
}
