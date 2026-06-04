
/*
 * Copyright (c) 2024-2026, the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package baby.mumu.log.infra.system.gatewayimpl;

import baby.mumu.log.domain.system.SystemLog;
import baby.mumu.log.domain.system.gateway.SystemLogGateway;
import baby.mumu.log.infra.system.convertor.SystemLogEsPersistenceConvertor;
import baby.mumu.log.infra.system.convertor.SystemLogKafkaPersistenceConvertor;
import baby.mumu.log.infra.system.gatewayimpl.elasticsearch.SystemLogEsRepository;
import baby.mumu.log.infra.system.gatewayimpl.elasticsearch.po.SystemLogEsPO;
import baby.mumu.log.infra.system.gatewayimpl.elasticsearch.po.SystemLogEsPOMetamodel;
import baby.mumu.log.infra.system.gatewayimpl.kafka.SystemLogKafkaRepository;
import baby.mumu.log.infra.system.gatewayimpl.kafka.po.SystemLogKafkaPO;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.Criteria;
import org.springframework.data.elasticsearch.core.query.CriteriaQuery;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.stereotype.Component;
import tools.jackson.databind.json.JsonMapper;

import static baby.mumu.basis.constants.CommonConstants.ES_QUERY_EN;
import static baby.mumu.basis.constants.CommonConstants.ES_QUERY_SP;
import static baby.mumu.log.client.config.LogConstants.SYSTEM_LOG_KAFKA_TOPIC_NAME;

/**
 * 系统日志领域网关实现
 *
 * @author <a href="mailto:kaiyu.shan@outlook.com">Kaiyu Shan</a>
 * @since 1.0.0
 */
@Component
public class SystemLogGatewayImpl implements SystemLogGateway {

    private final SystemLogKafkaRepository systemLogKafkaRepository;
    private final SystemLogEsRepository systemLogEsRepository;
    private final JsonMapper jsonMapper;
    private final ElasticsearchTemplate elasticsearchTemplate;
    private final SystemLogKafkaPersistenceConvertor kafkaConvertor;
    private final SystemLogEsPersistenceConvertor esConvertor;

    @Autowired
    public SystemLogGatewayImpl(SystemLogKafkaRepository systemLogKafkaRepository,
                                SystemLogEsRepository systemLogEsRepository, JsonMapper jsonMapper,
                                ElasticsearchTemplate elasticsearchTemplate,
                                SystemLogKafkaPersistenceConvertor kafkaConvertor,
                                SystemLogEsPersistenceConvertor esConvertor) {
        this.systemLogKafkaRepository = systemLogKafkaRepository;
        this.systemLogEsRepository = systemLogEsRepository;
        this.jsonMapper = jsonMapper;
        this.elasticsearchTemplate = elasticsearchTemplate;
        this.kafkaConvertor = kafkaConvertor;
        this.esConvertor = esConvertor;
    }

    @Override
    public void submit(SystemLog systemLog) {
        kafkaConvertor.toKafkaPO(systemLog).ifPresent(res -> systemLogKafkaRepository.send(SYSTEM_LOG_KAFKA_TOPIC_NAME, jsonMapper.writeValueAsString(res)));
    }

    @Override
    public void save(SystemLog systemLog) {
        esConvertor.toEsPO(systemLog).ifPresent(systemLogEsRepository::save);
    }

    @Override
    public void saveFromKafkaMessage(String message) {
        SystemLogKafkaPO kafkaPO = jsonMapper.readValue(message, SystemLogKafkaPO.class);
        kafkaConvertor.toEntity(kafkaPO).ifPresent(this::save);
    }

    @Override
    @SuppressWarnings("DuplicatedCode")
    public Page<SystemLog> findAll(SystemLog systemLog, int current, int pageSize) {
        PageRequest pageRequest = PageRequest.of(current - 1, pageSize);
        Criteria criteria = new Criteria();
        Optional.ofNullable(systemLog).ifPresent(sysLog -> {
            Optional.ofNullable(sysLog.getId()).ifPresent(id -> criteria.and(new Criteria(SystemLogEsPOMetamodel.ID).matches(id)));
            Optional.ofNullable(sysLog.getContent()).ifPresent(content -> { String p = SystemLogEsPOMetamodel.CONTENT; criteria.and(new Criteria(p).matches(content).or(p.concat(ES_QUERY_EN)).matches(content).or(p.concat(ES_QUERY_SP)).matches(content)); });
            Optional.ofNullable(sysLog.getCategory()).ifPresent(category -> criteria.and(new Criteria(SystemLogEsPOMetamodel.CATEGORY).matches(category)));
            Optional.ofNullable(sysLog.getSuccess()).ifPresent(success -> { String p = SystemLogEsPOMetamodel.SUCCESS; criteria.and(new Criteria(p).matches(success).or(p.concat(ES_QUERY_EN)).matches(success).or(p.concat(ES_QUERY_SP)).matches(success)); });
            Optional.ofNullable(sysLog.getFail()).ifPresent(fail -> { String p = SystemLogEsPOMetamodel.FAIL; criteria.and(new Criteria(p).matches(fail).or(p.concat(ES_QUERY_SP)).matches(fail)); });
            Optional.ofNullable(sysLog.getRecordTime()).ifPresent(recordTime -> criteria.and(new Criteria(SystemLogEsPOMetamodel.RECORD_TIME).matches(recordTime)));
            Optional.ofNullable(sysLog.getRecordStartTime()).ifPresent(recordStartTime -> criteria.and(new Criteria(SystemLogEsPOMetamodel.RECORD_TIME).greaterThan(recordStartTime)));
            Optional.ofNullable(sysLog.getRecordEndTime()).ifPresent(recordEndTime -> criteria.and(new Criteria(SystemLogEsPOMetamodel.RECORD_TIME).lessThan(recordEndTime)));
        });
        Query query = new CriteriaQuery(criteria).setPageable(pageRequest).addSort(Sort.by(SystemLogEsPOMetamodel.RECORD_TIME).descending());
        SearchHits<SystemLogEsPO> searchHits = elasticsearchTemplate.search(query, SystemLogEsPO.class);
        List<SystemLog> systemLogs = searchHits.getSearchHits().stream().map(SearchHit::getContent).map(esConvertor::toEntity).filter(Optional::isPresent).map(Optional::get).peek(sd -> sd.setRecordTime(sd.getRecordTime())).toList();
        return new PageImpl<>(systemLogs, pageRequest, searchHits.getTotalHits());
    }
}
