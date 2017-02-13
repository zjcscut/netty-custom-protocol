package org.throwable.utils;

import io.netty.handler.codec.marshalling.*;
import org.jboss.marshalling.*;
import org.throwable.protocol.MarshallingDecoderAdapter;
import org.throwable.protocol.MarshallingEncoderAdapter;

import java.io.IOException;

/**
 * @author zjc
 * @version 2017/2/12 21:57
 * @description marshalling工厂
 */
public class MarshallingCodeCFactory {

	public static MarshallingDecoderAdapter buildMarshallingDecoder() {
		final MarshallerFactory marshallerFactory
				= Marshalling.getProvidedMarshallerFactory("serial");
		final MarshallingConfiguration configuration = new MarshallingConfiguration();
		configuration.setVersion(5);
		UnmarshallerProvider provider = new DefaultUnmarshallerProvider(marshallerFactory,
				configuration);
		//单个对象的最大尺寸
		int maxSiez = 1024 << 2;
		return new MarshallingDecoderAdapter(provider, maxSiez);
	}

	public static MarshallingEncoderAdapter buildMarshallingEncoder() {
		final MarshallerFactory marshallerFactory = Marshalling
				.getProvidedMarshallerFactory("serial");
		final MarshallingConfiguration configuration = new MarshallingConfiguration();
		configuration.setVersion(5);
		MarshallerProvider provider = new DefaultMarshallerProvider(
				marshallerFactory, configuration);
		return new MarshallingEncoderAdapter(provider);
	}

	public static Marshaller buildMarshalling()throws IOException{
		final MarshallerFactory marshallerFactory
				= Marshalling.getProvidedMarshallerFactory("serial");
		final MarshallingConfiguration configuration = new MarshallingConfiguration();
		configuration.setVersion(5);
		return marshallerFactory.createMarshaller(configuration);
	}

	public static Unmarshaller buildUnmarshalling()throws IOException{
		final MarshallerFactory marshallerFactory
				= Marshalling.getProvidedMarshallerFactory("serial");
		final MarshallingConfiguration configuration = new MarshallingConfiguration();
		configuration.setVersion(5);
		return marshallerFactory.createUnmarshaller(configuration);
	}
}
