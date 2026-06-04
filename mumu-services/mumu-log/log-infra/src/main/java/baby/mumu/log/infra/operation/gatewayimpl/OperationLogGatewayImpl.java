
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

package baby.mumu.log.infra.operation.gatewayimpl;

import baby.mumu.genix.client.api.PrimaryKeyGrpcService;
import baby.mumu.log.domain.operation.OperationLog;
import baby.mumu.log.domain.operation.gateway.OperationLogGateway;
import baby.mumu.log.infra.operation.convertor.OperationLogEsPersistenceConvertor;
import baby.mumu.log.infra.operation.convertor.OperationLogKafkaPersistenceConvertor;
import baby.mumu.log.infra.operation.gatewayimpl.elasticsearch.OperationLogEsRepository;
import baby.mumu.log.infra.operation.gatewayimpl.elasticsearch.po.OperationLogEsPO;
import baby.mumu.log.infra.operation.gatewayimpl.elasticsearch.po.OperationLogEsPOMetamodel;
import baby.mumu.log.infra.operation.gatewayimpl.kafka.OperationLogKafkaRepository;
import baby.mumu.log.infra.operation.gatewayimpl.kafka.po.OperationLogKafkaPO;
import java.time.LocalDateTime;
import java.time.ZoneId;
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
import static baby.mumu.log.client.config.LogConstants.OPERATION_LOG_KAFKA_TOPIC_NAME;

/**
 * 操作日志领域网关实现
 *
 * @author <a href="mailto:kaiyu.shan@outlook.com">Kaiyu Shan</a>
 * @since 1.0.0
 */
@Component
public class OperationLogGatewayImpl implements OperationLogGateway {

    private final OperationLogKafkaRepository operationLogKafkaRepository;
    private final OperationLogEsRepository operationLogEsRepository;
    private final JsonMapper jsonMapper;
    private final PrimaryKeyGrpcService primaryKeyGrpcService;
    private final ElasticsearchTemplate elasticsearchTemplate;
    private final OperationLogKafkaPersistenceConvertor kafkaConvertor;
    private final OperationLogEsPersistenceConvertor esConvertor;

    @Autowired
    public OperationLogGatewayImpl(OperationLogKafkaRepository operationLogKafkaRepository,
                                   OperationLogEsRepository operationLogEsRepository, JsonMapper jsonMapper,
                                   PrimaryKeyGrpcService primaryKeyGrpcService,
                                   ElasticsearchTemplate elasticsearchTemplate,
                                   OperationLogKafkaPersistenceConvertor kafkaConvertor,
                                   OperationLogEsPersistenceConvertor esConvertor) {
        this.operationLogKafkaRepository = operationLogKafkaRepository;
        this.operationLogEsRepository = operationLogEsRepository;
        this.jsonMapper = jsonMapper;
        this.primaryKeyGrpcService = primaryKeyGrpcService;
        this.elasticsearchTemplate = elasticsearchTemplate;
        this.kafkaConvertor = kafkaConvertor;
        this.esConvertor = esConvertor;
    }

    @Override
    public void submit(OperationLog operationLog) {
        kafkaConvertor.toKafkaPO(operationLog).ifPresent(
            res -> operationLogKafkaRepository.send(OPERATION_LOG_KAFKA_TOPIC_NAME, jsonMapper.writeValueAsString(res)));
    }

    @Override
    public void save(OperationLog operationLog) {
        esConvertor.toEsPO(operationLog).ifPresent(operationLogEsRepository::save);
    }

    @Override
    public void saveFromKafkaMessage(String message) {
        OperationLogKafkaPO kafkaPO = jsonMapper.readValue(message, OperationLogKafkaPO.class);
        kafkaConvertor.toEntity(kafkaPO).ifPresent(this::save);
    }

    @Override
    public Optional<OperationLog> findOperationLogById(String id) {
        Optional<OperationLog> optionalOperationLog = operationLogEsRepository.findById(id).flatMap(esConvertor::toEntity);
        OperationLog operationLog = new OperationLog();
        operationLog.setId(String.valueOf(primaryKeyGrpcService.snowflake()));
        operationLog.setBizNo(id);
        operationLog.setContent("根据日志ID获取操作日志");
        operationLog.setOperatingTime(LocalDateTime.now(ZoneId.of("UTC")));
        optionalOperationLog.ifPresentOrElse(op -> operationLog.setSuccess(op.toString()),
            () -> operationLog.setFail(String.format("未找到ID为%s的操作日志", id)));
        this.submit(operationLog);
        return optionalOperationLog;
    }

    @Override
    @SuppressWarnings("DuplicatedCode")
    public Page<OperationLog> findAll(OperationLog operationLog, int current, int pageSize) {
        PageRequest pageRequest = PageRequest.of(current - 1, pageSize);
        Criteria criteria = new Criteria();
        Optional.ofNullable(operationLog).ifPresent(optLog -> {
            Optional.ofNullable(optLog.getId()).ifPresent(id -> criteria.and(new Criteria(OperationLogEsPOMetamodel.ID).matches(id)));
            Optional.ofNullable(optLog.getContent()).ifPresent(content -> { String p = OperationLogEsPOMetamodel.CONTENT; criteria.and(new Criteria(p).matches(content).or(p.concat(ES_QUERY_EN)).matches(content).or(p.concat(ES_QUERY_SP)).matches(content)); });
            Optional.ofNullable(optLog.getOperator()).ifPresent(operator -> { String p = OperationLogEsPOMetamodel.OPERATOR; criteria.and(new Criteria(p).matches(operator).or(p.concat(ES_QUERY_EN)).matches(operator).or(p.concat(ES_QUERY_SP)).matches(operator)); });
            Optional.ofNullable(optLog.getBizNo()).ifPresent(bizNo -> criteria.and(new Criteria(OperationLogEsPOMetamodel.BIZ_NO).matches(bizNo)));
            Optional.ofNullable(optLog.getCategory()).ifPresent(category -> criteria.and(new Criteria(OperationLogEsPOMetamodel.CATEGORY).matches(category)));
            Optional.ofNullable(optLog.getDetail()).ifPresent(detail -> { String p = OperationLogEsPOMetamodel.DETAIL; criteria.and(new Criteria(p).matches(detail).or(p.concat(ES_QUERY_EN)).matches(detail).or(p.concat(ES_QUERY_SP)).matches(detail)); });
            Optional.ofNullable(optLog.getSuccess()).ifPresent(success -> { String p = OperationLogEsPOMetamodel.SUCCESS; criteria.and(new Criteria(p).matches(success).or(p.concat(ES_QUERY_EN)).matches(success).or(p.concat(ES_QUERY_SP)).matches(success)); });
            Optional.ofNullable(optLog.getFail()).ifPresent(fail -> { String p = OperationLogEsPOMetamodel.FAIL; criteria.and(new Criteria(p).matches(fail).or(p.concat(ES_QUERY_EN)).matches(fail).or(p.concat(ES_QUERY_SP)).matches(fail)); });
            Optional.ofNullable(optLog.getOperatingTime()).ifPresent(operatingTime -> criteria.and(new Criteria(OperationLogEsPOMetamodel.OPERATING_TIME).matches(operatingTime)));
            Optional.ofNullable(optLog.getOperatingStartTime()).ifPresent(operatingStartTime -> criteria.and(new Criteria(OperationLogEsPOMetamodel.OPERATING_TIME).greaterThan(operatingStartTime)));
            Optional.ofNullable(optLog.getOperatingEndTime()).ifPresent(operatingEndTime -> criteria.and(new Criteria(OperationLogEsPOMetamodel.OPERATING_TIME).lessThan(operatingEndTime)));
        });
        Query query = new CriteriaQuery(criteria).setPageable(pageRequest).addSort(Sort.by(OperationLogEsPOMetamodel.OPERATING_TIME).descending());
        SearchHits<OperationLogEsPO> searchHits = elasticsearchTemplate.search(query, OperationLogEsPO.class);
        List<OperationLog> operationLogs = searchHits.getSearchHits().stream().map(SearchHit::getContent).map(esConvertor::toEntity).filter(Optional::isPresent).map(Optional::get).peek(od -> od.setOperatingTime(od.getOperatingTime())).toList();
        return new PageImpl<>(operationLogs, pageRequest, searchHits.getTotalHits());
    }
}
