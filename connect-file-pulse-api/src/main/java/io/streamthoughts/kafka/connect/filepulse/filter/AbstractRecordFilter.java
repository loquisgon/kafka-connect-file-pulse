/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.streamthoughts.kafka.connect.filepulse.filter;

import io.streamthoughts.kafka.connect.filepulse.filter.condition.FilterCondition;
import io.streamthoughts.kafka.connect.filepulse.filter.config.CommonFilterConfig;
import io.streamthoughts.kafka.connect.filepulse.reader.FileInputRecord;
import io.streamthoughts.kafka.connect.filepulse.reader.RecordsIterable;
import io.streamthoughts.kafka.connect.filepulse.source.FileInputData;
import org.apache.kafka.common.config.ConfigDef;

import java.util.Map;

public abstract class AbstractRecordFilter<T extends AbstractRecordFilter> implements RecordFilter {

    private RecordFilterPipeline<FileInputRecord> failurePipeline;

    protected FilterCondition condition;

    protected boolean ignoreFailure;

    /**
     * {@inheritDoc}
     */
    @Override
    public abstract ConfigDef configDef();

    /**
     * {@inheritDoc}
     */
    @Override
    public void configure(final Map<String, ?> props) {
        final CommonFilterConfig config = new CommonFilterConfig(configDef(), props);
        condition = config.condition();
        failurePipeline = config.onFailure();
        ignoreFailure = config.ignoreFailure();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public abstract RecordsIterable<FileInputData> apply(final FilterContext context,
                                                         final FileInputData struct,
                                                         final boolean hasNext);

    @SuppressWarnings("unchecked")
    public T withOnCondition(final FilterCondition condition) {
        this.condition = condition;
        return (T) this;
    }

    @SuppressWarnings("unchecked")
    public T withOnFailure(final RecordFilterPipeline<FileInputRecord> failurePipeline) {
        this.failurePipeline = failurePipeline;
        return (T) this;
    }

    @SuppressWarnings("unchecked")
    public T withIgnoreFailure(final boolean ignoreFailure) {
        this.ignoreFailure = ignoreFailure;
        return (T) this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean accept(final FilterContext context, final FileInputData record) {
        return condition.apply(context, record);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public RecordFilterPipeline<FileInputRecord> onFailure() {
        return failurePipeline;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean ignoreFailure() {
        return ignoreFailure;
    }
}
