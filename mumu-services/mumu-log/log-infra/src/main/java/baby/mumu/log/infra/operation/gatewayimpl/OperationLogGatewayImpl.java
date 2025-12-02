/*
 * Copyright (c) 2024-2025, the original author or authors.
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

import static baby.mumu.basis.constants.CommonConstants.ES_QUERY_EN;
import static baby.mumu.basis.constants.CommonConstants.ES_QUERY_SP;

import baby.mumu.basis.kotlin.tools.TimeUtils;
import baby.mumu.genix.client.api.PrimaryKeyGrpcService;
import baby.mumu.log.domain.operation.OperationLog;
import baby.mumu.log.domain.operation.gateway.OperationLogGateway;
import baby.mumu.log.infra.config.LogProperties;
import baby.mumu.log.infra.operation.convertor.OperationLogConvertor;
import baby.mumu.log.infra.operation.gatewayimpl.elasticsearch.OperationLogEsRepository;
import baby.mumu.log.infra.operation.gatewayimpl.elasticsearch.po.OperationLogEsPO;
import baby.mumu.log.infra.operation.gatewayimpl.elasticsearch.po.OperationLogEsPOMetamodel;
import baby.mumu.log.infra.operation.gatewayimpl.kafka.OperationLogKafkaRepository;
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
import tools.jackson.databind.ObjectMapper;

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
  private final ObjectMapper objectMapper;
  private final PrimaryKeyGrpcService primaryKeyGrpcService;
  private final ElasticsearchTemplate elasticsearchTemplate;
  private final OperationLogConvertor operationLogConvertor;

  @Autowired
  public OperationLogGatewayImpl(OperationLogKafkaRepository operationLogKafkaRepository,
    OperationLogEsRepository operationLogEsRepository, ObjectMapper objectMapper,
    PrimaryKeyGrpcService primaryKeyGrpcService, ElasticsearchTemplate elasticsearchTemplate,
    OperationLogConvertor operationLogConvertor) {
    this.operationLogKafkaRepository = operationLogKafkaRepository;
    this.operationLogEsRepository = operationLogEsRepository;
    this.objectMapper = objectMapper;
    this.primaryKeyGrpcService = primaryKeyGrpcService;
    this.elasticsearchTemplate = elasticsearchTemplate;
    this.operationLogConvertor = operationLogConvertor;
  }

  @Override
  public void submit(OperationLog operationLog) {
    operationLogConvertor.toOperationLogKafkaPO(operationLog).ifPresent(
      res -> operationLogKafkaRepository.send(LogProperties.OPERATION_LOG_KAFKA_TOPIC_NAME,
        objectMapper.writeValueAsString(
          res)));
  }

  @Override
  public void save(OperationLog operationLog) {
    operationLogConvertor.toOperationLogEsPO(operationLog)
      .ifPresent(operationLogEsRepository::save);
  }

  @Override
  public Optional<OperationLog> findOperationLogById(String id) {
    Optional<OperationLog> optionalOperationLog = operationLogEsRepository.findById(
      id).flatMap(operationLogConvertor::toEntity);
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
      Optional.ofNullable(optLog.getId())
        .ifPresent(id -> criteria.and(
          new Criteria(OperationLogEsPOMetamodel.ID).matches(id)));
      Optional.ofNullable(optLog.getContent())
        .ifPresent(content -> {
          String propertyName = OperationLogEsPOMetamodel.CONTENT;
          criteria.and(
            new Criteria(propertyName).matches(content).or(propertyName.concat(ES_QUERY_EN))
              .matches(content).or(propertyName.concat(ES_QUERY_SP))
              .matches(content));
        });
      Optional.ofNullable(optLog.getOperator())
        .ifPresent(operator -> {
          String propertyName = OperationLogEsPOMetamodel.OPERATOR;
          criteria.and(
            new Criteria(propertyName).matches(operator).or(propertyName.concat(ES_QUERY_EN))
              .matches(operator).or(propertyName.concat(ES_QUERY_SP))
              .matches(operator));
        });
      Optional.ofNullable(optLog.getBizNo())
        .ifPresent(bizNo -> criteria.and(
          new Criteria(OperationLogEsPOMetamodel.BIZ_NO).matches(bizNo)));
      Optional.ofNullable(optLog.getCategory())
        .ifPresent(category -> criteria.and(
          new Criteria(OperationLogEsPOMetamodel.CATEGORY).matches(
            category)));
      Optional.ofNullable(optLog.getDetail())
        .ifPresent(detail -> {
          String propertyName = OperationLogEsPOMetamodel.DETAIL;
          criteria.and(
            new Criteria(propertyName).matches(detail).or(propertyName.concat(ES_QUERY_EN))
              .matches(detail).or(propertyName.concat(ES_QUERY_SP))
              .matches(detail));
        });
      Optional.ofNullable(optLog.getSuccess())
        .ifPresent(success -> {
          String propertyName = OperationLogEsPOMetamodel.SUCCESS;
          criteria.and(
            new Criteria(propertyName).matches(success).or(propertyName.concat(ES_QUERY_EN))
              .matches(success).or(propertyName.concat(ES_QUERY_SP))
              .matches(success));
        });
      Optional.ofNullable(optLog.getFail())
        .ifPresent(fail -> {
          String propertyName = OperationLogEsPOMetamodel.FAIL;
          criteria.and(
            new Criteria(propertyName).matches(fail).or(propertyName.concat(ES_QUERY_EN))
              .matches(fail).or(propertyName.concat(ES_QUERY_SP))
              .matches(fail));
        });

      Optional.ofNullable(optLog.getOperatingTime())
        .ifPresent(
          operatingTime -> criteria.and(new Criteria(
            OperationLogEsPOMetamodel.OPERATING_TIME).matches(
            TimeUtils.convertAccountZoneToUTC(operatingTime))));
      Optional.ofNullable(optLog.getOperatingStartTime())
        .ifPresent(
          operatingStartTime -> criteria.and(
            new Criteria(
              OperationLogEsPOMetamodel.OPERATING_TIME).greaterThan(
              TimeUtils.convertAccountZoneToUTC(operatingStartTime))));
      Optional.ofNullable(optLog.getOperatingEndTime())
        .ifPresent(
          operatingEndTime -> criteria.and(
            new Criteria(
              OperationLogEsPOMetamodel.OPERATING_TIME).lessThan(
              TimeUtils.convertAccountZoneToUTC(operatingEndTime))));
    });
    Query query = new CriteriaQuery(criteria).setPageable(pageRequest)
      .addSort(
        Sort.by(OperationLogEsPOMetamodel.OPERATING_TIME).descending());
    SearchHits<OperationLogEsPO> searchHits = elasticsearchTemplate.search(query,
      OperationLogEsPO.class);
    List<OperationLog> operationLogs = searchHits.getSearchHits().stream()
      .map(SearchHit::getContent).map(operationLogConvertor::toEntity)
      .filter(Optional::isPresent).map(Optional::get)
      .peek(operationLogDomain ->
        operationLogDomain.setOperatingTime(
          TimeUtils.convertUTCToAccountZone(operationLogDomain.getOperatingTime())))
      .toList();
    return new PageImpl<>(operationLogs, pageRequest, searchHits.getTotalHits());
  }
}
