package io.skodjob.dmt.schema;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Objects;

/**
 * Model class for time results from load generation in DMT. Every property is in milliseconds.
 */
public class LoadResult {
    long totalTime;
    long lastExecutorStarted;
    long LastExecutorFinished;

    /**
     * @param totalTime Total time it took to generate the queries/requests and to send it through the client/JDBC to database.
     * @param lastExecutorStarted Time it took to start executing queries/request on all connections without the data generation.
     * @param lastExecutorFinished Time it took to finish executing queries/requests on all connections without the data generation.
     */
    public LoadResult(long totalTime, long lastExecutorStarted, long lastExecutorFinished) {
        this.totalTime = totalTime;
        this.lastExecutorStarted = lastExecutorStarted;
        this.LastExecutorFinished = lastExecutorFinished;
    }

    public LoadResult() {
        this.totalTime = 0;
        this.LastExecutorFinished = 0;
        this.lastExecutorStarted = 0;
    }

    public long getTotalTime() {
        return totalTime;
    }

    public void setTotalTime(long totalTime) {
        this.totalTime = totalTime;
    }

    public long getLastExecutorStarted() {
        return lastExecutorStarted;
    }

    public void setLastExecutorStarted(long lastExecutorStarted) {
        this.lastExecutorStarted = lastExecutorStarted;
    }

    public long getLastExecutorFinished() {
        return LastExecutorFinished;
    }

    public void setLastExecutorFinished(long lastExecutorFinished) {
        this.LastExecutorFinished = lastExecutorFinished;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LoadResult that = (LoadResult) o;
        return totalTime == that.totalTime && lastExecutorStarted == that.lastExecutorStarted && LastExecutorFinished == that.LastExecutorFinished;
    }

    @Override
    public int hashCode() {
        return Objects.hash(totalTime, lastExecutorStarted, LastExecutorFinished);
    }

    public String toJsonString() throws JsonProcessingException {
        return new ObjectMapper().writeValueAsString(this);
    }

    @Override
    public String toString() {
        return "LoadResult{" +
                "totalTime=" + totalTime +
                ", lastExecutorStarted=" + lastExecutorStarted +
                ", LastExecutorFinished=" + LastExecutorFinished +
                '}';
    }
}
