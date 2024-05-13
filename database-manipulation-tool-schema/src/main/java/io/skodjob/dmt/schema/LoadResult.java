/*
 *
 * MIT License
 *
 * Copyright (c) [2024] [Ondrej Babec <ond.babec@gmail.com>, Jiri Novotny <novotnyjirkajn@gmail.com>]
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY
 * KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO
 * EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE
 * ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR
 * OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package io.skodjob.dmt.schema;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Objects;

/**
 * Model class for time results from load generation in DMT. Every property is in milliseconds.
 */
public class LoadResult {
    /**
     * The Total time.
     */
    long totalTime;
    /**
     * The Last executor started.
     */
    long lastExecutorStarted;
    /**
     * The Last executor finished.
     */
    long LastExecutorFinished;

    /**
     * Instantiates a new Load result.
     *
     * @param totalTime            Total time it took to generate the queries/requests and to send it through the client/JDBC to database.
     * @param lastExecutorStarted  Time it took to start executing queries/request on all connections without the data generation.
     * @param lastExecutorFinished Time it took to finish executing queries/requests on all connections without the data generation.
     */
    public LoadResult(long totalTime, long lastExecutorStarted, long lastExecutorFinished) {
        this.totalTime = totalTime;
        this.lastExecutorStarted = lastExecutorStarted;
        this.LastExecutorFinished = lastExecutorFinished;
    }


    /**
     * Instantiates a new Load result.
     */
    public LoadResult() {
        this.totalTime = 0;
        this.LastExecutorFinished = 0;
        this.lastExecutorStarted = 0;
    }

    /**
     * Gets total time.
     *
     * @return the total time
     */
    public long getTotalTime() {
        return totalTime;
    }

    /**
     * Sets total time.
     *
     * @param totalTime the total time
     */
    public void setTotalTime(long totalTime) {
        this.totalTime = totalTime;
    }

    /**
     * Gets last executor started.
     *
     * @return the last executor started
     */
    public long getLastExecutorStarted() {
        return lastExecutorStarted;
    }

    /**
     * Sets last executor started.
     *
     * @param lastExecutorStarted the last executor started
     */
    public void setLastExecutorStarted(long lastExecutorStarted) {
        this.lastExecutorStarted = lastExecutorStarted;
    }

    /**
     * Gets last executor finished.
     *
     * @return the last executor finished
     */
    public long getLastExecutorFinished() {
        return LastExecutorFinished;
    }

    /**
     * Sets last executor finished.
     *
     * @param lastExecutorFinished the last executor finished
     */
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

    /**
     * To json string string.
     *
     * @return the string
     * @throws JsonProcessingException the json processing exception
     */
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
