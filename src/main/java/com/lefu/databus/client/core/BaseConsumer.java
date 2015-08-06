package com.lefu.databus.client.core;

import java.util.Map;

import org.apache.avro.generic.GenericRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lefu.databus.client.ConsumeUnit;
import com.linkedin.databus.client.consumer.AbstractDatabusCombinedConsumer;
import com.linkedin.databus.client.pub.ConsumerCallbackResult;
import com.linkedin.databus.client.pub.DbusEventDecoder;
import com.linkedin.databus.core.DbusEvent;

public class BaseConsumer extends AbstractDatabusCombinedConsumer {
	private final Logger log = LoggerFactory.getLogger(getClass());
	private Map<Integer,ConsumeUnit> dispatcher;
	
	public BaseConsumer() {
		
	}
	
	@Override
	public ConsumerCallbackResult onDataEvent(DbusEvent event,
			DbusEventDecoder eventDecoder) {
		return processEvent(event, eventDecoder);
	}

	@Override
	public ConsumerCallbackResult onBootstrapEvent(DbusEvent event,
			DbusEventDecoder eventDecoder) {
		return processEvent(event, eventDecoder);
	}
	
	private ConsumerCallbackResult processEvent(DbusEvent event,
			DbusEventDecoder eventDecoder) {
		try {
			GenericRecord decodedEvent = eventDecoder.getGenericRecord(event, null);
			ConsumeUnit unit = this.dispatcher.get(event.getSourceId());
			if (unit == null) {
				log.warn("SourceID {} consumer not found", event.getSourceId());
				return ConsumerCallbackResult.ERROR_FATAL;
			}
			unit.execute(event, decodedEvent);
			return ConsumerCallbackResult.SUCCESS;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ConsumerCallbackResult.ERROR;
	}

	public void setDispatcher(Map<Integer, ConsumeUnit> dispatcher) {
		this.dispatcher = dispatcher;
	}
}