package seb.api.carbs;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.kafka.ConcurrentKafkaListenerContainerFactoryConfigurer;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.boot.context.properties.PropertyMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.config.ContainerProperties;
import org.springframework.kafka.support.converter.RecordMessageConverter;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

@EnableKafka
@Configuration
public class KafkaConsumerConfig {

    @Value(value = "${spring.kafka.bootstrap-servers}")
    private String bootstrapServer;

    @Value(value = "${spring.kafka.consumer.group-id}")
    private String consumerGroup;


    @Bean
    ConsumerFactory<String, String> consumerFactory() {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServer);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, consumerGroup);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);

        return new DefaultKafkaConsumerFactory<>(props);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, String> kafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, String> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory());

        return factory;
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactoryConfigurer kafkaListenerContainerFactoryConfigurer(
            KafkaProperties kafkaProperties,
            ObjectProvider<RecordMessageConverter> messageConverterObjectProvider,
            ObjectProvider<KafkaTemplate<Object, Object>> kafkaTemplateObjectProvider) {

        RecordMessageConverter messageConverter = messageConverterObjectProvider.getIfUnique();
        KafkaTemplate<Object, Object> kafkaTemplate = kafkaTemplateObjectProvider.getIfUnique();

        return new ConcurrentKafkaListenerContainerFactoryConfigurer() {

            @Override
            public void configure(ConcurrentKafkaListenerContainerFactory<Object, Object> listenerFactory,
                                  ConsumerFactory<Object, Object> consumerFactory) {

                listenerFactory.setConsumerFactory(consumerFactory);
                configureListenerFactory(listenerFactory);
                configureContainer(listenerFactory.getContainerProperties());
            }

            private void configureListenerFactory(
                    ConcurrentKafkaListenerContainerFactory<Object, Object> factory) {
                PropertyMapper map = PropertyMapper.get();
                KafkaProperties.Listener properties = kafkaProperties.getListener();
                map.from(properties::getConcurrency).whenNonNull().to(factory::setConcurrency);
                map.from(() -> messageConverter).whenNonNull()
                        .to(factory::setMessageConverter);
                map.from(() -> kafkaTemplate).whenNonNull().to(factory::setReplyTemplate);
                map.from(properties::getType).whenEqualTo(KafkaProperties.Listener.Type.BATCH)
                        .toCall(() -> factory.setBatchListener(true));
            }

            private void configureContainer(ContainerProperties container) {
                PropertyMapper map = PropertyMapper.get();
                KafkaProperties.Listener properties = kafkaProperties.getListener();
                map.from(properties::getAckMode).whenNonNull().to(container::setAckMode);
                map.from(properties::getAckCount).whenNonNull().to(container::setAckCount);
                map.from(properties::getAckTime).whenNonNull().as(Duration::toMillis)
                        .to(container::setAckTime);
                map.from(properties::getPollTimeout).whenNonNull().as(Duration::toMillis)
                        .to(container::setPollTimeout);
                map.from(properties::getNoPollThreshold).whenNonNull()
                        .to(container::setNoPollThreshold);
                map.from(properties::getIdleEventInterval).whenNonNull().as(Duration::toMillis)
                        .to(container::setIdleEventInterval);
                map.from(properties::getMonitorInterval).whenNonNull().as(Duration::getSeconds)
                        .as(Number::intValue).to(container::setMonitorInterval);
            }

        };
    }




}
